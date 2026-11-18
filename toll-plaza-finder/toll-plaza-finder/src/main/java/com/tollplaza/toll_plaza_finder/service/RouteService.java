
package com.tollplaza.toll_plaza_finder.service;

import org.springframework.stereotype.Service;

@Service
public class RouteService {

    private final PincodeService pincodeService;

    public RouteService(PincodeService pincodeService) {
        this.pincodeService = pincodeService;
    }

    public double calculateDistance(String sourcePincode,
                                    String destinationPincode) {

        double[] source =
                pincodeService.getCoordinates(sourcePincode);

        double[] destination =
                pincodeService.getCoordinates(destinationPincode);

        double earthRadius = 6371.0;

        double lat1 = Math.toRadians(source[0]);
        double lon1 = Math.toRadians(source[1]);

        double lat2 = Math.toRadians(destination[0]);
        double lon2 = Math.toRadians(destination[1]);

        double latDifference = lat2 - lat1;
        double lonDifference = lon2 - lon1;

        double a = Math.pow(Math.sin(latDifference / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(lonDifference / 2), 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return earthRadius * c;
    }
}
