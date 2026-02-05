import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import './App.css';
import Catalog from './components/Catalog';
import Cart from './components/Cart';
import OrderConfirmation from './components/OrderConfirmation';

function App() {
  return (
    <Router>
      <div className="App">
        <nav className="navbar">
          <div className="nav-container">
            <Link to="/" className="nav-logo">📱 E-Commerce Smartphones</Link>
            <ul className="nav-menu">
              <li className="nav-item">
                <Link to="/" className="nav-link">Catalogue</Link>
              </li>
              <li className="nav-item">
                <Link to="/cart" className="nav-link">Panier</Link>
              </li>
            </ul>
          </div>
        </nav>

        <div className="container">
          <Routes>
            <Route path="/" element={<Catalog />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/order-confirmation/:orderId" element={<OrderConfirmation />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
