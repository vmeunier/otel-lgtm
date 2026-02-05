package com.bouygtel.otel.loadtests;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Simulation Gatling pour tester l'application e-commerce
 * - 5 à 10 utilisateurs simultanés
 * - Durée : 15 minutes
 * - Scénarios incluant des erreurs et de la lenteur
 */
public class EcommerceSimulation extends Simulation {

    private static final Random random = new Random();

    // Configuration HTTP
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8084") // Order Service (orchestrateur)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .userAgentHeader("Gatling Load Test");

    // Feeder pour les smartphones depuis le service inventaire
    // Les données seront récupérées dynamiquement via un appel HTTP dans le scénario
    FeederBuilder<Object> smartphoneIdFeeder =
        listFeeder(
            java.util.stream.Stream.generate(() -> {
                Map<String, Object> map = new HashMap<>();
                map.put("smartphoneId", (long) (random.nextInt(8) + 1)); // IDs de 1 à 8
                return map;
            }).limit(1000).toList()
        ).circular();

    // Feeder pour les quantités
    FeederBuilder<Object> quantityFeeder =
        listFeeder(
            java.util.stream.Stream.generate(() -> {
                Map<String, Object> map = new HashMap<>();
                map.put("quantity", random.nextInt(3) + 1);
                return map;
            }).limit(1000).toList()
        ).circular();

    // Feeder pour les utilisateurs depuis le fichier CSV
    FeederBuilder<String> userFeeder = csv("users.csv").circular();

    // Feeder pour les IDs de panier
    FeederBuilder<Object> cartIdFeeder =
        listFeeder(
            java.util.stream.Stream.generate(() -> {
                Map<String, Object> map = new HashMap<>();
                map.put("cartId", random.nextInt(1000));
                return map;
            }).limit(1000).toList()
        ).circular();

    /**
     * Scénario 1 : Consultation du catalogue (utilisateur qui browse)
     */
    ScenarioBuilder browseCatalog = scenario("Browse Catalog")
        .exec(http("Get All Smartphones")
            .get("http://localhost:8083/api/smartphones")
            .check(status().in(200, 500)))
        .repeat(3).on(
            feed(smartphoneIdFeeder)
                .exec(http("Get Smartphone Details")
                    .get("http://localhost:8083/api/smartphones/#{smartphoneId}")
                    .check(status().in(200, 404, 500)))
        );

    /**
     * Scénario 2 : Création et modification de panier (utilisateur actif)
     */
    ScenarioBuilder manageCart = scenario("Manage Cart")
        .feed(userFeeder)
        .exec(http("Get or Create Cart")
            .get("http://localhost:8085/api/carts/users/#{userId}")
            .check(status().in(200, 201, 500))
            .check(jsonPath("$.id").saveAs("createdCartId")))
        .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
        .repeat(4).on(
            feed(smartphoneIdFeeder)
                .exec(http("Get Smartphone Details")
                    .get("http://localhost:8083/api/smartphones/#{smartphoneId}")
                    .check(status().in(200, 404, 500))
                    .check(jsonPath("$.id").saveAs("smartphoneId"))
                    .check(jsonPath("$.brand").saveAs("smartphoneBrand"))
                    .check(jsonPath("$.model").saveAs("smartphoneModel"))
                    .check(jsonPath("$.price").saveAs("smartphonePrice")))
                .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
                .feed(quantityFeeder)
                .exec(http("Add Item to Cart")
                    .post("http://localhost:8085/api/carts/users/#{userId}/items")
                    .body(StringBody("""
                        {
                          "smartphoneId": #{smartphoneId},
                          "smartphoneBrand": "#{smartphoneBrand}",
                          "smartphoneModel": "#{smartphoneModel}",
                          "smartphonePrice": #{smartphonePrice},
                          "quantity": #{quantity}
                        }
                        """)).asJson()
                    .check(status().in(200, 400, 404, 500)))
                .pause(Duration.ofSeconds(1), Duration.ofSeconds(3))
        )
        .exec(http("Get Cart")
            .get("http://localhost:8085/api/carts/#{createdCartId}")
            .check(status().in(200, 404, 500)))
        .pause(2);

    /**
     * Scénario 3 : Parcours complet - Achat (utilisateur qui convertit)
     */
    ScenarioBuilder completePurchase = scenario("Complete Purchase")
        .feed(cartIdFeeder)
        // 1. Consulter le catalogue
        .exec(http("Browse Catalog")
            .get("http://localhost:8083/api/smartphones")
            .check(status().in(200, 500)))
        .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))

        // 2. Créer un panier
        .feed(userFeeder)
        .exec(http("Get or Create Cart")
            .get("http://localhost:8085/api/carts/users/#{userId}")
            .check(status().in(200, 201, 500))
            .check(jsonPath("$.id").saveAs("createdCartId")))
        .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))

        // 3. Ajouter des articles
        .repeat(2).on(
            feed(smartphoneIdFeeder)
                .exec(http("Get Smartphone Details")
                    .get("http://localhost:8083/api/smartphones/#{smartphoneId}")
                    .check(status().in(200, 404, 500))
                    .check(jsonPath("$.id").saveAs("smartphoneId"))
                    .check(jsonPath("$.brand").saveAs("smartphoneBrand"))
                    .check(jsonPath("$.model").saveAs("smartphoneModel"))
                    .check(jsonPath("$.price").saveAs("smartphonePrice")))
                .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
                .feed(quantityFeeder)
                .exec(http("Add Item to Cart")
                    .post("http://localhost:8085/api/carts/users/#{userId}/items")
                    .body(StringBody("""
                        {
                          "smartphoneId": #{smartphoneId},
                          "smartphoneBrand": "#{smartphoneBrand}",
                          "smartphoneModel": "#{smartphoneModel}",
                          "smartphonePrice": #{smartphonePrice},
                          "quantity": #{quantity}
                        }
                        """)).asJson()
                    .check(status().in(200, 400, 404, 500)))
                .pause(Duration.ofSeconds(1), Duration.ofSeconds(3))
        )

        // 4. Vérifier le panier
        .exec(http("Get Cart")
            .get("http://localhost:8085/api/carts/#{createdCartId}")
            .check(jsonPath("$.totalAmount").optional().saveAs("cartTotal"), status().in(200, 404, 500)))

        // 5. Passer commande (orchestration via Order Service)
        .exec(session -> {
            String total = session.getString("cartTotal");
            if (total == null) {
                total = "299.99";
            }
            return session.set("orderAmount", total);
        })
        .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
        .exec(http("Create Order")
            .post("http://localhost:8084/api/orders")
            .body(StringBody("{\"cartId\": #{createdCartId}, \"amount\": #{orderAmount}}")).asJson()
            .check(status().in(200, 201, 400, 404, 500, 503)));

    // Configuration de la simulation
    // - Durée totale : 20 minutes
    // - 5-10 utilisateurs simultanés
    // - Mix de scénarios réalistes et problématiques
    {
        setUp(
            // Utilisateurs qui browsent (50% du trafic)
            browseCatalog.injectOpen(
                rampUsers(2).during(Duration.ofSeconds(30)),
                constantUsersPerSec(3).during(Duration.ofMinutes(15))
            ),

            // Utilisateurs qui gèrent leur panier (30% du trafic)
            manageCart.injectOpen(
                rampUsers(2).during(Duration.ofMinutes(1)),
                constantUsersPerSec(2).during(Duration.ofMinutes(15))
            ),

            // Utilisateurs qui achètent (20% du trafic)
            completePurchase.injectOpen(
                rampUsers(1).during(Duration.ofMinutes(1)),
                constantUsersPerSec(1).during(Duration.ofMinutes(15))
            )
        ).protocols(httpProtocol)
         .maxDuration(Duration.ofMinutes(15))
         .assertions(
             global().responseTime().max().lt(10000), // Temps de réponse max < 10s
             global().successfulRequests().percent().gt(70.0) // Au moins 70% de succès
         );
    }
}
