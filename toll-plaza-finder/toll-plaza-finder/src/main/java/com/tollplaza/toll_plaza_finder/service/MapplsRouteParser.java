
package com.tollplaza.toll_plaza_finder.service;

import com.tollplaza.toll_plaza_finder.dto.MapplsRouteData;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class MapplsRouteParser {

    public MapplsRouteData parseRoute(JsonNode response) {

        if (response == null) {
            throw new IllegalArgumentException(
                    "Mappls returned an empty response"
            );
        }

        String code = response.path("code").asString("");

        if (!"ok".equalsIgnoreCase(code)) {
            String message = response.path("message")
                    .asString("Unable to calculate route using Mappls");

            throw new IllegalArgumentException(
                    "Mappls routing error: " + message
            );
        }

        JsonNode routes = response.path("routes");

        if (!routes.isArray() || routes.isEmpty()) {
            throw new IllegalArgumentException(
                    "No route found in Mappls response"
            );
        }

        JsonNode route = routes.get(0);

        double distanceInMeters =
                route.path("distance").asDouble(-1);

        if (distanceInMeters <= 0) {
            throw new IllegalArgumentException(
                    "Invalid route distance received from Mappls"
            );
        }

        JsonNode coordinates =
                route.path("geometry").path("coordinates");

        if (!coordinates.isArray() || coordinates.size() < 2) {
            throw new IllegalArgumentException(
                    "Route geometry is missing or invalid"
            );
        }

        List<double[]> routeCoordinates = new ArrayList<>();

        for (JsonNode coordinate : coordinates) {

            if (!coordinate.isArray() || coordinate.size() < 2) {
                continue;
            }

            double longitude = coordinate.get(0).asDouble();
            double latitude = coordinate.get(1).asDouble();

            routeCoordinates.add(
                    new double[]{longitude, latitude}
            );
        }

        if (routeCoordinates.size() < 2) {
            throw new IllegalArgumentException(
                    "Insufficient route coordinates received from Mappls"
            );
        }

        return new MapplsRouteData(
                distanceInMeters / 1000.0,
                routeCoordinates
        );
    }
}
