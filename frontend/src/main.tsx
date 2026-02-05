import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

// Initialiser OpenTelemetry AVANT tout le reste
import './otel-config';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <App />
);
