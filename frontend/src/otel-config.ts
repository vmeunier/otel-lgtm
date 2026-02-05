import { WebTracerProvider, BatchSpanProcessor } from '@opentelemetry/sdk-trace-web';
import { Resource } from '@opentelemetry/resources';
import { SEMRESATTRS_SERVICE_NAME, SEMRESATTRS_SERVICE_VERSION } from '@opentelemetry/semantic-conventions';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';
import { ZoneContextManager } from '@opentelemetry/context-zone';
import { registerInstrumentations } from '@opentelemetry/instrumentation';
import { FetchInstrumentation } from '@opentelemetry/instrumentation-fetch';
import { XMLHttpRequestInstrumentation } from '@opentelemetry/instrumentation-xml-http-request';
import { DocumentLoadInstrumentation } from '@opentelemetry/instrumentation-document-load';

// Configuration de l'exporteur OTLP
const OTEL_EXPORTER_OTLP_ENDPOINT = import.meta.env.VITE_OTEL_EXPORTER_OTLP_ENDPOINT || 'http://localhost:4318/v1/traces';

// Créer la ressource avec les informations du service
const resource = new Resource({
  [SEMRESATTRS_SERVICE_NAME]: 'frontend-ecommerce',
  [SEMRESATTRS_SERVICE_VERSION]: '1.0.0',
  'deployment.environment': import.meta.env.MODE || 'development',
});

// Créer le provider de traces
const provider = new WebTracerProvider({
  resource: resource,
});

// Configurer l'exporteur OTLP
const exporter = new OTLPTraceExporter({
  url: OTEL_EXPORTER_OTLP_ENDPOINT,
  headers: {},
});

// Ajouter le processeur de spans
provider.addSpanProcessor(new BatchSpanProcessor(exporter));

// Configurer le gestionnaire de contexte
provider.register({
  contextManager: new ZoneContextManager(),
});

// Enregistrer les instrumentations automatiques
registerInstrumentations({
  instrumentations: [
    // Instrumentation pour fetch API (utilisé par axios)
    new FetchInstrumentation({
      propagateTraceHeaderCorsUrls: [
        /localhost:8083/, // Inventory Service
        /localhost:8085/, // Cart Service
        /localhost:8084/, // Order Service
        /localhost:808[0-9]/, // Tous les services locaux
      ],
      clearTimingResources: true,
      applyCustomAttributesOnSpan: (span, request, result) => {
        // Ajouter des attributs personnalisés aux spans
        if (request instanceof Request) {
          span.setAttribute('http.url', request.url);
          span.setAttribute('http.method', request.method);
        }
        if (result instanceof Response) {
          span.setAttribute('http.status_code', result.status);
        }
      },
    }),
    // Instrumentation pour XMLHttpRequest (fallback)
    new XMLHttpRequestInstrumentation({
      propagateTraceHeaderCorsUrls: [
        /localhost:8083/,
        /localhost:8085/,
        /localhost:8084/,
        /localhost:808[0-9]/,
      ],
    }),
    // Instrumentation pour le chargement du document
    new DocumentLoadInstrumentation(),
  ],
});

console.log('✅ OpenTelemetry initialisé avec propagation W3C Trace Context');

export { provider };
