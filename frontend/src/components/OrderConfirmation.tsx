import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import type { Order } from '../types';
import './OrderConfirmation.css';

const OrderConfirmation: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const navigate = useNavigate();
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (orderId) {
      loadOrder();
    }
  }, [orderId]);

  const loadOrder = async (): Promise<void> => {
    if (!orderId) return;

    try {
      setLoading(true);
      const response = await api.getOrder(parseInt(orderId, 10));
      setOrder(response.data);
      setError(null);
    } catch (err) {
      setError('Impossible de charger les détails de la commande');
      console.error('Erreur lors du chargement de la commande:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Chargement de la commande...</div>;
  }

  if (error) {
    return (
      <div className="error-container">
        <h2>Erreur</h2>
        <p>{error}</p>
      </div>
    );
  }

  if (!order) {
    return <div>Commande introuvable</div>;
  }

  return (
    <div className="order-confirmation">
      {order.status === 'COMPLETED' ? (
        <>
          <div className="success-icon">✓</div>
          <h1>Commande confirmée !</h1>
          <p className="success-message">
            Merci pour votre achat. Votre commande a été traitée avec succès.
          </p>
        </>
      ) : (
        <>
          <div className="error-icon">✕</div>
          <h1>Échec de la commande</h1>
          <p className="error-message">
            {order.failureReason || 'Une erreur est survenue lors du traitement de votre commande.'}
          </p>
        </>
      )}

      <div className="order-details">
        <h2>Détails de la commande</h2>
        <div className="detail-row">
          <span>Numéro de commande:</span>
          <span>#{order.id}</span>
        </div>
        <div className="detail-row">
          <span>Statut:</span>
          <span className={`status ${order.status.toLowerCase()}`}>
            {order.status}
          </span>
        </div>
        <div className="detail-row">
          <span>Montant total:</span>
          <span>{order.totalAmount.toFixed(2)} €</span>
        </div>
        <div className="detail-row">
          <span>Date:</span>
          <span>{new Date(order.createdAt).toLocaleString('fr-FR')}</span>
        </div>
      </div>

      <div className="order-items">
        <h3>Articles commandés</h3>
        {order.items.map((item) => (
          <div key={item.id} className="order-item">
            <div className="item-details">
              <span className="item-name">
                {item.smartphoneBrand} {item.smartphoneModel}
              </span>
              <span className="item-quantity">x{item.quantity}</span>
            </div>
            <span className="item-price">
              {(item.smartphonePrice * item.quantity).toFixed(2)} €
            </span>
          </div>
        ))}
      </div>

      <div className="action-buttons">
        <button
          className="back-home-btn"
          onClick={() => navigate('/')}
        >
          Retour à l'accueil
        </button>
      </div>
    </div>
  );
};

export default OrderConfirmation;
