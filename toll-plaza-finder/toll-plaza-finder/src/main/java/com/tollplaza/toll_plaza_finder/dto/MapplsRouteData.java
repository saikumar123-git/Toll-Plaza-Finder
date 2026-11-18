
package com.tollplaza.toll_plaza_finder.dto;

import java.util.List;

public record MapplsRouteData(
        double distanceInKm,
        List<double[]> coordinates
) {
}
