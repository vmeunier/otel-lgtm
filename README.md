# otel-microservices

Démonstration de l'Observabilité via OpenTelemetry et la stack LGTM pour réussir à mieux trouver des problèmes grâce aux trois piliers : logs, métriques, et traces.

## 🎯 Description du Projet

Ce projet présente une application **e-commerce complète** utilisant une **architecture microservices** avec Spring Boot et React. Il est conçu pour démontrer l'observabilité distribuée avec OpenTelemetry.

## 🏗️ Architecture

L'application est composée de :

### Backend - 4 Microservices Spring Boot
1. **Inventory Service** (8083) - Gestion du catalogue de smartphones
2. **Cart Service** (8081) - Gestion des paniers d'achat
3. **Payment Service** (8082) - Traitement des paiements
4. **Order Service** (8084) - Orchestration des commandes

### Frontend - React Application
- Interface utilisateur moderne (port 3000)
- Catalogue de produits
- Gestion de panier
- Processus de commande

## 🚀 Démarrage Rapide

```cmd
# 1. Démarrer la stack d'observabilité 
cd dev/docker/lgtm
docker-compose up 

cd otel.microservices
.\start-services.cmd
```

Accédez à l'application : **http://localhost:8080**

## 📚 Documentation

Toute la documentation est disponible dans le dossier `otel.microservices/` :

- **[README.md](otel.microservices/README.md)** - Documentation complète de l'application

## 🛠️ Technologies

- **Backend**: Spring Boot 4.0.1, Spring Data JPA, H2 Database
- **Frontend**: React 18, React Router, Axios
- **Communication**: HTTP REST synchrone
- **Observabilité**: OpenTelemetry

## ✨ Fonctionnalités

✅ Catalogue de smartphones avec stock en temps réel  
✅ Panier d'achat dynamique  
✅ Simulation de paiement
✅ Orchestration de commandes
✅ Gestion automatique du stock  
✅ Interface utilisateur responsive
✅ **Tests de charge Gatling** avec simulation d'erreurs  

## 🎓 Objectif Pédagogique

Ce projet sert de base pour démontrer :
- Architecture microservices
- Communication inter-services
- Observabilité distribuée avec OpenTelemetry
- Stack LGTM (Loki, Grafana, Tempo, Mimir)
- Traçabilité des requêtes distribuées
- Analyse des performances
- Contexte spécifique pour les logs et traces pour chercher des problèmes directement par identifiant métier
- **Tests de charge** avec génération d'erreurs et de lenteurs

## 🧪 Tests de Charge

Le projet inclut un module Gatling complet pour générer du trafic réaliste :

```cmd
cd otel.microservices
.\run-load-tests.cmd
```

**Caractéristiques :**
- ⏱️ 20 minutes de test
- 👥 5-10 utilisateurs simultanés
- 🎯 3 scénarios réalistes (browse, cart, purchase,)
- 🎲 Simulation aléatoire d'erreurs et de lenteurs
- 📊 Rapports HTML détaillés

**Génération de problèmes sur l'inventory-service :**
- 5% de requêtes lentes (2-5s)
- 3% d'erreurs serveur (500)
- 3% de timeouts (8-12s)
- Logs ERROR, WARN, INFO variés

📚 **Documentation complète** : [otel.microservices/load-tests/README.md](otel.microservices/load-tests/README.md)

## 📦 Contenu du Projet

```
otel-microservices/
├── README.md (ce fichier)
└── otel.microservices/
    ├── inventory-service/      # Service de gestion du stock
    ├── cart-service/            # Service de gestion des paniers
    ├── payment-service/         # Service de traitement des paiements
    ├── order-service/           # Service d'orchestration des commandes
    ├── frontend/                # Application React
    ├── common/                  # Composants partagés (ChaosInterceptor)
    └── load-tests/              # Tests de charge Gatling
```

### 📚 Documentation Disponible

- 📖 [README.md](README.md) - Ce fichier (vue d'ensemble)
- 📖 [otel.microservices/README.md](otel.microservices/README.md) - Documentation technique complète


---

**Bon apprentissage avec l'observabilité ! 📊**

