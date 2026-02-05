import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import type { Cart as CartType } from '../types';
import './Cart.css';

const Cart: React.FC = () => {
  const [cart, setCart] = useState<CartType | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [processing, setProcessing] = useState<boolean>(false);
  const navigate = useNavigate();

  const userId = '1337'; // Utilisateur de démo

  useEffect(() => {
    loadCart();
  }, []);

  const loadCart = async (): Promise<void> => {
    try {
      setLoading(true);
      const response = await api.getCart(userId);
      setCart(response.data);
      setError(null);
    } catch (err) {
      setError('Impossible de charger le panier');
      console.error('Erreur lors du chargement du panier:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQuantity = async (itemId: number, newQuantity: number): Promise<void> => {
    if (!cart) return;

    try {
      await api.updateCartItem(cart.id, itemId, newQuantity);
      await loadCart();
    } catch (err) {
      console.error('Erreur lors de la mise à jour de la quantité:', err);
    }
  };

  const handleRemoveItem = async (itemId: number): Promise<void> => {
    if (!cart) return;

    try {
      await api.removeFromCart(cart.id, itemId);
      await loadCart();
    } catch (err) {
      console.error('Erreur lors de la suppression de l\'article:', err);
    }
  };

  const handleCheckout = async (): Promise<void> => {
    if (!cart || cart.items.length === 0) {
      alert('Votre panier est vide');
      return;
    }

    try {
      setProcessing(true);
      const response = await api.createOrder(cart.id, 'CARD');
      const order = response.data;

      if (order.status === 'COMPLETED') {
        navigate(`/order-confirmation/${order.id}`);
      } else {
        alert(`Échec de la commande: ${order.failureReason || 'Erreur inconnue'}`);
      }
    } catch (err) {
      console.error('Erreur lors de la commande:', err);
      alert('Erreur lors du traitement de la commande');
    } finally {
      setProcessing(false);
    }
  };

  const getTotalPrice = (): number => {
    if (!cart || !cart.items) return 0;
    return cart.items.reduce((total, item) =>
      total + (item.smartphonePrice * item.quantity), 0
    );
  };

  if (loading) {
    return <div className="loading">Chargement du panier...</div>;
  }

  if (error) {
    return (
      <div className="error-container">
        <h2>Erreur</h2>
        <p>{error}</p>
      </div>
    );
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="empty-cart">
        <h2>Votre panier est vide</h2>
        <button onClick={() => navigate('/')} className="continue-shopping-btn">
          Continuer vos achats
        </button>
      </div>
    );
  }

  return (
    <div className="cart">
      <h1>Votre Panier</h1>

      <div className="cart-items">
        {cart.items.map((item) => (
          <div key={item.id} className="cart-item">
            <div className="item-info">
              <h3>{item.smartphoneBrand} {item.smartphoneModel}</h3>
              <p className="item-price">{item.smartphonePrice.toFixed(2)} €</p>
            </div>

            <div className="item-quantity">
              <button
                onClick={() => item.id && handleUpdateQuantity(item.id, item.quantity - 1)}
                disabled={item.quantity <= 1}
              >
                -
              </button>
              <span>{item.quantity}</span>
              <button
                onClick={() => item.id && handleUpdateQuantity(item.id, item.quantity + 1)}
              >
                +
              </button>
            </div>

            <div className="item-total">
              <span className="total-label">Total:</span>
              <span className="total-price">
                {(item.smartphonePrice * item.quantity).toFixed(2)} €
              </span>
            </div>

            <button
              className="remove-btn"
              onClick={() => item.id && handleRemoveItem(item.id)}
            >
              ✕
            </button>
          </div>
        ))}
      </div>

      <div className="cart-summary">
        <div className="summary-row">
          <span>Sous-total:</span>
          <span>{getTotalPrice().toFixed(2)} €</span>
        </div>
        <div className="summary-row total">
          <span>Total:</span>
          <span>{getTotalPrice().toFixed(2)} €</span>
        </div>

        <button
          className="checkout-btn"
          onClick={handleCheckout}
          disabled={processing}
        >
          {processing ? 'Traitement...' : 'Passer commande'}
        </button>

        <button
          className="continue-shopping-btn"
          onClick={() => navigate('/')}
        >
          Continuer vos achats
        </button>
      </div>
    </div>
  );
};

export default Cart;
