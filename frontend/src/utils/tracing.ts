import { trace, SpanStatusCode, Span } from '@opentelemetry/api';

// Tracer pour l'application frontend
const tracer = trace.getTracer('frontend-ecommerce', '1.0.0');

/**
 * Crée un span pour une opération spécifique
 * @param name Nom de l'opération
 * @param fn Fonction à exécuter dans le contexte du span
 * @param attributes Attributs additionnels pour le span
 */
export async function withSpan<T>(
  name: string,
  fn: (span: Span) => Promise<T>,
  attributes?: Record<string, string | number | boolean>
): Promise<T> {
  return tracer.startActiveSpan(name, async (span) => {
    try {
      // Ajouter les attributs personnalisés
      if (attributes) {
        Object.entries(attributes).forEach(([key, value]) => {
          span.setAttribute(key, value);
        });
      }

      // Exécuter la fonction
      const result = await fn(span);

      // Marquer le span comme réussi
      span.setStatus({ code: SpanStatusCode.OK });

      return result;
    } catch (error) {
      // Enregistrer l'erreur dans le span
      span.setStatus({
        code: SpanStatusCode.ERROR,
        message: error instanceof Error ? error.message : 'Unknown error',
      });

      if (error instanceof Error) {
        span.recordException(error);
      }

      throw error;
    } finally {
      span.end();
    }
  });
}

/**
 * Ajoute des attributs au span actif
 */
export function addSpanAttributes(attributes: Record<string, string | number | boolean>): void {
  const span = trace.getActiveSpan();
  if (span) {
    Object.entries(attributes).forEach(([key, value]) => {
      span.setAttribute(key, value);
    });
  }
}

/**
 * Ajoute un événement au span actif
 */
export function addSpanEvent(name: string, attributes?: Record<string, string | number | boolean>): void {
  const span = trace.getActiveSpan();
  if (span) {
    span.addEvent(name, attributes);
  }
}

/**
 * Récupère le contexte de trace actuel (pour debug)
 */
export function getCurrentTraceContext(): { traceId: string; spanId: string } | null {
  const span = trace.getActiveSpan();
  if (span) {
    const spanContext = span.spanContext();
    return {
      traceId: spanContext.traceId,
      spanId: spanContext.spanId,
    };
  }
  return null;
}

export { tracer };
