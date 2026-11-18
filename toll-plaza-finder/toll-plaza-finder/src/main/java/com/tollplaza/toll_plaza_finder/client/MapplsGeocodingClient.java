
package com.tollplaza.toll_plaza_finder.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import tools.jackson.databind.JsonNode;

@Component
public class MapplsGeocodingClient {

    private final RestClient restClient;

    @Value("${mappls.api-key}")
    private String apiKey;

    public MapplsGeocodingClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public JsonNode getCoordinates(String pincode) {

        String url = "https://search.mappls.com/search/address/geocode";

        return restClient.get()
                .uri(UriComponentsBuilder.fromUriString(url)
                        .queryParam("address", pincode + ", India")
                        .queryParam("podFilter", "pincode")
                        .queryParam("access_token", apiKey)
                        .build()
                        .encode()
                        .toUri())
                .retrieve()
                .body(JsonNode.class);
    }
}
