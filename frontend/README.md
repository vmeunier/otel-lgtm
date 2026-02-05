# Frontend E-Commerce avec Vite + TypeScript

Ce projet est un frontend React développé avec **Vite** et **TypeScript** pour une application e-commerce de démonstration OpenTelemetry.

## Technologies utilisées

- **Vite** - Build tool moderne et rapide
- **React 18** - Bibliothèque UI
- **TypeScript** - Typage statique
- **React Router** - Navigation
- **Axios** - Client HTTP
- **CSS3** - Styling

## Prérequis

- Node.js 18+
- npm ou yarn

## Installation

```bash
npm install
```

## Scripts disponibles

### Développement
```bash
npm run dev
```
Lance le serveur de développement Vite sur http://localhost:8080

### Build de production
```bash
npm run build
```
Compile TypeScript et crée un build optimisé dans le dossier `build/`

### Preview
```bash
npm run preview
```
Prévisualise le build de production localement

### Lint
```bash
npm run lint
```
Vérifie le code avec ESLint

## Structure du projet

```
src/
├── components/       # Composants React TypeScript
│   ├── Catalog.tsx
│   ├── Cart.tsx
│   └── OrderConfirmation.tsx
├── services/        # Services API
│   └── api.ts
├── types/           # Définitions TypeScript
│   └── index.ts
├── App.tsx          # Composant principal
├── main.tsx         # Point d'entrée
└── index.css        # Styles globaux
```

## Configuration

Les URLs des services backend sont définies dans `src/services/api.ts`:
- Inventory Service: http://localhost:8083/api
- Cart Service: http://localhost:8085/api
- Order Service: http://localhost:8084/api

## 🔍 Traçabilité distribuée

Le frontend est instrumenté avec **OpenTelemetry** pour propager automatiquement les traces aux services backend via le standard **W3C Trace Context**.

### Fonctionnalités
- ✅ Propagation automatique du header `traceparent` sur tous les appels HTTP
- ✅ Support des standards W3C Trace Context
- ✅ Instrumentation automatique de `fetch` et `XMLHttpRequest`
- ✅ Traçage du chargement de la page
- ✅ Utilitaires pour créer des spans personnalisés

### Démarrage rapide
Aucune configuration supplémentaire n'est nécessaire ! Les traces sont automatiquement propagées vers vos services backend.

**Pour en savoir plus, consultez [TRACING.md](TRACING.md)**

### Configuration backend requise
Les services backend doivent autoriser les headers W3C dans CORS. Exemple de configuration fourni dans `CorsConfig.java.example`.

## Migration de Create React App vers Vite

Ce projet a été migré de Create React App vers Vite pour bénéficier de:
- ⚡ Démarrage instantané du serveur de développement
- 🔥 Hot Module Replacement (HMR) ultra-rapide
- 📦 Build optimisé avec Rollup
- 🎯 Support natif de TypeScript et JSX
- 🛠️ Configuration simplifiée

## Learn More

- [Documentation Vite](https://vitejs.dev/)
- [Documentation TypeScript](https://www.typescriptlang.org/)
- [Documentation React](https://react.dev/)
