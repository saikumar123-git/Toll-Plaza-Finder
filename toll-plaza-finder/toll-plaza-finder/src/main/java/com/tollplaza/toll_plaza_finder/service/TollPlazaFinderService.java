
package com.tollplaza.toll_plaza_finder.service;

import com.tollplaza.toll_plaza_finder.client.MapplsRoutingClient;
import com.tollplaza.toll_plaza_finder.dto.MapplsRouteData;
import com.tollplaza.toll_plaza_finder.dto.TollPlazaRequest;
import com.tollplaza.toll_plaza_finder.dto.TollPlazaResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class TollPlazaFinderService {
    private static final Logger log =
            LoggerFactory.getLogger(TollPlazaFinderService.class);

    private final MapplsRoutingClient mapplsRoutingClient;
    private final MapplsRouteParser mapplsRouteParser;
    private final TollPlazaService tollPlazaService;
    private final PincodeService pincodeService;

    public TollPlazaFinderService(
            MapplsRoutingClient mapplsRoutingClient,
            MapplsRouteParser mapplsRouteParser,
            TollPlazaService tollPlazaService,
            PincodeService pincodeService) {

        this.mapplsRoutingClient = mapplsRoutingClient;
        this.mapplsRouteParser = mapplsRouteParser;
        this.tollPlazaService = tollPlazaService;
        this.pincodeService = pincodeService;
    }

    @Cacheable(
            value = "tollPlazaRoutes",
            key = "#request.sourcePincode() + '-' + #request.destinationPincode()"
    )
    public TollPlazaResponse findTollPlazas(TollPlazaRequest request) {

        String source = request.sourcePincode();
        String destination = request.destinationPincode();

        // Validate source and destination
        if (source.equals(destination)) {
            throw new IllegalArgumentException(
                    "Source and destination pincodes must be different"
            );
        }

        // Get coordinates
        // Array format: [latitude, longitude]
        double[] sourceLocation =
                pincodeService.getCoordinates(source);

        double[] destinationLocation =
                pincodeService.getCoordinates(destination);

        // Mappls expects longitude first, latitude second
        log.info("Calling Mappls API for route: {} -> {}", source, destination);

        var mapplsResponse = mapplsRoutingClient.getRoute(
                sourceLocation[1],
                sourceLocation[0],
                destinationLocation[1],
                destinationLocation[0]
        );

        // Parse Mappls response
        MapplsRouteData routeData =
                mapplsRouteParser.parseRoute(mapplsResponse);

        // Find toll plazas along route
        var tollPlazas =
                tollPlazaService.findTollPlazasAlongRoute(
                        routeData.coordinates()
                );

//        if (tollPlazas.isEmpty()) {
//            throw new IllegalArgumentException(
//                    "No toll plazas found along this route"
//            );
//        }

        // Build route response
        var routeInfo = new TollPlazaResponse.RouteInfo(
                source,
                destination,
                routeData.distanceInKm()
        );

        return new TollPlazaResponse(routeInfo, tollPlazas);
    }
}
