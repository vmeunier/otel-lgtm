package com.bouygtel.otel.loadtests;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Simulation simple de 2 minutes pour tests rapides
 */
public class QuickSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8083")
        .acceptHeader("application/json");

    ScenarioBuilder quickTest = scenario("Quick Test")
        .exec(http("Get All Smartphones")
            .get("/api/inventory")
            .check(status().in(200, 500)))
        .pause(1)
        .exec(http("Get Smartphone 1")
            .get("/api/inventory/1")
            .check(status().in(200, 404, 500)))
        .pause(1);

    {
        setUp(
            quickTest.injectOpen(
                rampUsers(3).during(Duration.ofSeconds(10)),
                constantUsersPerSec(0.5).during(Duration.ofSeconds(110))
            )
        ).protocols(httpProtocol)
         .maxDuration(Duration.ofMinutes(2));
    }
}
