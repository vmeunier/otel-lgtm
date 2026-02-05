# E-Commerce Microservices avec Spring Boot et React

Application e-commerce démontrant une architecture microservices avec Spring Boot et un front-end React.

## Architecture

L'application est composée de 4 microservices Spring Boot :

1. **inventory-service** (port 8083) - Gestion du stock de smartphones
2. **cart-service** (port 8081) - Gestion des paniers
3. **payment-service** (port 8082) - Traitement des paiements
4. **order-service** (port 8084) - Orchestration des commandes

Et d'un front-end React (port 8080) pour l'interface utilisateur.

## Prérequis

- Java 25 (ou version compatible)
- Maven 3.6+
- Node.js 16+ et npm
- Git

## Installation et Démarrage

### 1. Démarrer la stack d'observabilité via docker-compose

Allez dans le dossier `dev/docker/lgtm` puis faites :

```bash
docker-compose up
```

### 2. Démarrer les microservices

**Avec l'invite de commande Windows (cmd) :**

Au préalable il faut compiler les services si ce n'est pas déjà fait :
```cmd
cd otel.microservices
compile.cmd
```

#### Option A - Démarrage automatique (Recommandé)

**Avec l'invite de commande Windows (cmd) :**

```cmd
cd otel.microservices
start-services.cmd
```

#### Option B - Démarrage manuel

Ouvrez 4 terminaux différents et démarrez chaque service :

Attention, prérequis, il faut inclure le fichier `logging.properties` qui est dans `dev/common` comme des additional-properties de Spring.

Exemple en cmd :

```cmd
set PROG_ARGS=-Dspring-boot.run.arguments="--spring.config.additional-location=%~dp0\..\dev\common\logging.properties"
```

Sans cela, la configuration OpenTelemetry + le niveau des logs ne seront pas pris en compte.

#### Terminal 1 - Inventory Service
```bash
cd otel.microservices/inventory-service
mvn spring-boot:run %PROG_ARGS%
```

#### Terminal 2 - Cart Service
```bash
cd otel.microservices/cart-service
mvn spring-boot:run %PROG_ARGS%
```

#### Terminal 3 - Payment Service
```bash
cd otel.microservices/payment-service
mvn spring-boot:run %PROG_ARGS%
```

#### Terminal 4 - Order Service
```bash
cd otel.microservices/order-service
mvn spring-boot:run %PROG_ARGS%
```

### 3. Démarrer le front-end React (si pas démarré automatiquement)

Dans un 5ème terminal :

```bash
cd otel.microservices/frontend
npm run dev
```

L'application sera accessible sur http://localhost:8080

## Fonctionnalités

### Catalogue de smartphones
- Affichage de tous les smartphones disponibles
- Informations : marque, modèle, prix, stock, description
- Ajout au panier avec un clic

### Panier
- Visualisation des articles ajoutés
- Modification des quantités
- Suppression d'articles
- Calcul automatique du total

### Passage de commande
Le processus de commande est orchestré par l'order-service qui :
1. Récupère le panier via le cart-service
2. Traite le paiement via le payment-service
3. Réduit le stock via l'inventory-service
4. Marque le panier comme commandé
5. Crée la commande finale

### Simulation de paiement
Le payment-service simule un traitement de paiement avec :
- 95% de taux de succès pour les montants < 10000€
- 80% de taux de succès pour les montants > 10000€

## Endpoints API

### Inventory Service (http://localhost:8083)
- `GET /api/smartphones` - Liste tous les smartphones
- `GET /api/smartphones/{id}` - Détails d'un smartphone
- `POST /api/smartphones/{id}/reduce-stock` - Réduire le stock
- `POST /api/smartphones/{id}/check-stock` - Vérifier le stock

### Cart Service (http://localhost:8081)
- `GET /api/carts/user/{userId}` - Récupérer le panier d'un utilisateur
- `GET /api/carts/{cartId}` - Détails d'un panier
- `POST /api/carts/user/{userId}/items` - Ajouter un article
- `PUT /api/carts/{cartId}/items/{itemId}` - Modifier la quantité
- `DELETE /api/carts/{cartId}/items/{itemId}` - Supprimer un article
- `DELETE /api/carts/{cartId}` - Vider le panier
- `POST /api/carts/{cartId}/mark-ordered` - Marquer comme commandé

### Payment Service (http://localhost:8082)
- `POST /api/payments` - Traiter un paiement
- `GET /api/payments/{paymentId}` - Détails d'un paiement
- `GET /api/payments/cart/{cartId}` - Paiements d'un panier

### Order Service (http://localhost:8084)
- `POST /api/orders` - Créer une commande
- `GET /api/orders/{orderId}` - Détails d'une commande
- `GET /api/orders` - Liste toutes les commandes

## Consoles H2

Chaque service utilise une base de données H2 en mémoire accessible via :

- Inventory: http://localhost:8083/h2-console (JDBC URL: `jdbc:h2:mem:inventorydb`)
- Cart: http://localhost:8081/h2-console (JDBC URL: `jdbc:h2:mem:cartdb`)
- Payment: http://localhost:8082/h2-console (JDBC URL: `jdbc:h2:mem:paymentdb`)
- Order: http://localhost:8084/h2-console (JDBC URL: `jdbc:h2:mem:orderdb`)

Credentials: username=`sa`, password=(vide)

## Données de test

L'inventory-service est pré-chargé avec 8 smartphones :
- iPhone 15 Pro, iPhone 15
- Samsung Galaxy S24 Ultra, Galaxy S24
- Xiaomi 14 Pro
- Google Pixel 8 Pro
- OnePlus 12
- Huawei P60 Pro

## Technologies utilisées

### Backend
- Spring Boot 4.0.1
- Spring Data JPA
- H2 Database
- Spring Web
- RestClient pour communication inter-services

### Frontend
- React 18
- React Router DOM
- Axios
- CSS3

### Observabilité
- docker-compose avec l'image grafana/otel-lgtm
- 3 dashboard Grafana dont 
  - Métriques systèmes
  - Métriques applicatives
  - Logs

## Communication entre services

Les microservices communiquent via des appels HTTP REST synchrones :
- Order Service → Cart Service (récupération du panier)
- Order Service → Payment Service (traitement du paiement)
- Order Service → Inventory Service (réduction du stock)

## Workflow de commande

```
1. Utilisateur clique sur "Passer commande"
   ↓
2. Order Service récupère le panier (Cart Service)
   ↓
3. Order Service traite le paiement (Payment Service)
   ↓
4. Si paiement réussi → Réduction du stock (Inventory Service)
   ↓
5. Panier marqué comme commandé (Cart Service)
   ↓
6. Commande finalisée avec statut COMPLETED ou FAILED
```

## Observabilité avec OpenTelemetry

Ce projet est conçu pour démontrer l'observabilité via OpenTelemetry et la stack LGTM (Loki, Grafana, Tempo, Mimir) pour mieux identifier les problèmes grâce aux trois piliers : logs, métriques et traces.

## 🚀 Tests de Charge avec Gatling

Un module de tests de charge est inclus pour simuler une utilisation réaliste de l'application.

### Démarrage rapide

```cmd
cd otel.microservices
.\run-load-tests.cmd
```

### Caractéristiques des tests

- **Durée** : 20 minutes
- **Utilisateurs** : 5-10 simultanés
- **Scénarios** :
  - 50% - Navigation dans le catalogue
  - 30% - Gestion de panier
  - 20% - Achats complets

### Simulation de problèmes

Les tests incluent un **ChaosInterceptor** qui génère :
- ⏱️ **10% de lenteur** : Délais de 2-5 secondes
- ❌ **5% d'erreurs** : Erreurs serveur (500)
- ⏰ **3% de timeouts** : Délais de 8-12 secondes

Cela produit des logs variés :
- ✅ **INFO** : Requêtes normales
- ⚠️ **WARN** : Requêtes lentes
- ❌ **ERROR** : Erreurs et timeouts

### Rapports

Après exécution, consultez les rapports dans :
```
load-tests/target/gatling/ecommercesimulation-YYYYMMDDHHMMSS/index.html
```
