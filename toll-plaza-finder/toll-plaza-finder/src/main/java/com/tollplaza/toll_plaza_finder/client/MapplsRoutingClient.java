
package com.tollplaza.toll_plaza_finder.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import tools.jackson.databind.JsonNode;

import java.net.URI;

@Component
public class MapplsRoutingClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String routingUrl;

    public MapplsRoutingClient(
            RestClient restClient,
            @Value("${mappls.api-key}") String apiKey,
            @Value("${mappls.routing-url}") String routingUrl) {

        this.restClient = restClient;
        this.apiKey = apiKey;
        this.routingUrl = routingUrl;
    }

    public JsonNode getRoute(
            double sourceLongitude,
            double sourceLatitude,
            double destinationLongitude,
            double destinationLatitude) {

        String coordinates =
                sourceLongitude + "," + sourceLatitude + ";" +
                        destinationLongitude + "," + destinationLatitude;

        URI uri = UriComponentsBuilder
                .fromUriString(routingUrl)
                .path("/route_adv/driving/" + coordinates)
                .queryParam("access_token", apiKey)
                .queryParam("geometries", "geojson")
                .queryParam("overview", "full")
                .build()
                .encode()
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(JsonNode.class);
    }
}
