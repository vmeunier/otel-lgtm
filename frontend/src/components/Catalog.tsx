import { useState, useEffect } from 'react';
import api from '../services/api';
import type { Smartphone, CartItem } from '../types';
import './Catalog.css';

const Catalog: React.FC = () => {
  const [smartphones, setSmartphones] = useState<Smartphone[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [addingToCart, setAddingToCart] = useState<number | null>(null);
  const [notification, setNotification] = useState<string>('');

  const userId = '1337'; // Utilisateur de démo

  useEffect(() => {
    loadSmartphones();
  }, []);

  const loadSmartphones = async (): Promise<void> => {
    try {
      setLoading(true);
      const response = await api.getSmartphones();
      setSmartphones(response.data);
      setError(null);
    } catch (err) {
      setError('Impossible de charger le catalogue. Assurez-vous que le service inventory est démarré.');
      console.error('Erreur lors du chargement des smartphones:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (smartphone: Smartphone): Promise<void> => {
    try {
      setAddingToCart(smartphone.id);
      const cartItem: CartItem = {
        smartphoneId: smartphone.id,
        smartphoneBrand: smartphone.brand,
        smartphoneModel: smartphone.model,
        smartphonePrice: smartphone.price,
        quantity: 1
      };

      await api.addToCart(userId, cartItem);
      setNotification(`${smartphone.brand} ${smartphone.model} ajouté au panier !`);
      setTimeout(() => setNotification(''), 3000);
    } catch (err) {
      console.error('Erreur lors de l\'ajout au panier:', err);
      setNotification('Erreur lors de l\'ajout au panier');
      setTimeout(() => setNotification(''), 3000);
    } finally {
      setAddingToCart(null);
    }
  };

  if (loading) {
    return <div className="loading">Chargement du catalogue...</div>;
  }

  if (error) {
    return (
      <div className="error-container">
        <h2>Erreur</h2>
        <p>{error}</p>
        <button onClick={loadSmartphones}>Réessayer</button>
      </div>
    );
  }

  return (
    <div className="catalog">
      <h1>Catalogue de Smartphones</h1>

      {notification && (
        <div className="notification">{notification}</div>
      )}

      <div className="products-grid">
        {smartphones.map((phone) => (
          <div key={phone.id} className="product-card">
            <div className="product-info">
              <h3>{phone.brand} {phone.model}</h3>
              <p className="product-description">{phone.description}</p>
              <div className="product-footer">
                <span className="product-price">{phone.price.toFixed(2)} €</span>
                <span className={`product-stock ${phone.stock < 10 ? 'low' : ''}`}>
                  Stock: {phone.stock}
                </span>
              </div>
              <button
                className="add-to-cart-btn"
                onClick={() => handleAddToCart(phone)}
                disabled={phone.stock === 0 || addingToCart === phone.id}
              >
                {addingToCart === phone.id ? 'Ajout...' :
                 phone.stock === 0 ? 'Rupture de stock' : 'Ajouter au panier'}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Catalog;
