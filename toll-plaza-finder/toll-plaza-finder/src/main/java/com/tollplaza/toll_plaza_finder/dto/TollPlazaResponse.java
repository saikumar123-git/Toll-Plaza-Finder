
package com.tollplaza.toll_plaza_finder.dto;

import java.util.List;

public record TollPlazaResponse(

        RouteInfo route,
        List<TollPlazaInfo> tollPlazas

) {

    public record RouteInfo(
            String sourcePincode,
            String destinationPincode,
            double distanceInKm
    ) {}

    public record TollPlazaInfo(
            String name,
            double latitude,
            double longitude,
            double distanceFromSourceInKm
    ) {}
}
