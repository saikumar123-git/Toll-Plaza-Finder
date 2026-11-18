
package com.tollplaza.toll_plaza_finder.service;

import com.tollplaza.toll_plaza_finder.dto.TollPlazaResponse.TollPlazaInfo;
import com.tollplaza.toll_plaza_finder.entity.TollPlaza;
import com.tollplaza.toll_plaza_finder.repository.TollPlazaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TollPlazaService {

    private final TollPlazaRepository tollPlazaRepository;
    private final double routeCorridorKm;

    public TollPlazaService(
            TollPlazaRepository tollPlazaRepository,
            @Value("${toll-plaza.route-corridor-km:5.0}")
            double routeCorridorKm) {
        this.tollPlazaRepository = tollPlazaRepository;
        this.routeCorridorKm = routeCorridorKm;
    }

    public List<TollPlazaInfo> findTollPlazasAlongRoute(
            List<double[]> routeCoordinates) {

        if (routeCoordinates == null || routeCoordinates.size() < 2) {
            throw new IllegalArgumentException(
                    "Route must contain at least two coordinates");
        }

        double minLat = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = -Double.MAX_VALUE;

        for (double[] point : routeCoordinates) {
            double lon = point[0];
            double lat = point[1];

            minLat = Math.min(minLat, lat);
            maxLat = Math.max(maxLat, lat);
            minLon = Math.min(minLon, lon);
            maxLon = Math.max(maxLon, lon);
        }

        // Approximate 5 km search buffer
        double latBuffer = routeCorridorKm / 111.0;
        double lonBuffer = routeCorridorKm / 85.0;

        List<TollPlaza> candidates =
                tollPlazaRepository.findByLatitudeBetweenAndLongitudeBetween(
                        minLat - latBuffer,
                        maxLat + latBuffer,
                        minLon - lonBuffer,
                        maxLon + lonBuffer
                );

        List<TollPlazaInfo> result = new ArrayList<>();

        for (TollPlaza toll : candidates) {
            double[] projection =
                    calculateDistanceFromRoute(toll, routeCoordinates);

            double distanceFromRoute = projection[0];
            double distanceAlongRoute = projection[1];

            if (distanceFromRoute <= routeCorridorKm) {
                result.add(new TollPlazaInfo(
                        toll.getName(),
                        toll.getLatitude(),
                        toll.getLongitude(),
                        distanceAlongRoute
                ));
            }
        }

        result.sort(Comparator.comparingDouble(
                TollPlazaInfo::distanceFromSourceInKm));

        return result;
    }

    private double[] calculateDistanceFromRoute(
            TollPlaza toll,
            List<double[]> coordinates) {

        double cumulativeDistance = 0;
        double closestDistance = Double.MAX_VALUE;
        double closestAlongRoute = 0;

        for (int i = 0; i < coordinates.size() - 1; i++) {

            double[] start = coordinates.get(i);
            double[] end = coordinates.get(i + 1);

            double segmentLength = haversine(
                    start[1], start[0], end[1], end[0]);

            double latitudeScale = 111.0;
            double longitudeScale =
                    111.0 * Math.cos(Math.toRadians(toll.getLatitude()));

            double ax = (start[0] - toll.getLongitude()) * longitudeScale;
            double ay = (start[1] - toll.getLatitude()) * latitudeScale;
            double bx = (end[0] - toll.getLongitude()) * longitudeScale;
            double by = (end[1] - toll.getLatitude()) * latitudeScale;

            double dx = bx - ax;
            double dy = by - ay;

            double denominator = dx * dx + dy * dy;

            double t = denominator == 0
                    ? 0
                    : -(ax * dx + ay * dy) / denominator;

            t = Math.max(0, Math.min(1, t));

            double closestX = ax + t * dx;
            double closestY = ay + t * dy;

            double distance = Math.sqrt(
                    closestX * closestX + closestY * closestY);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestAlongRoute = cumulativeDistance
                        + (segmentLength * t);
            }

            cumulativeDistance += segmentLength;
        }

        return new double[]{closestDistance, closestAlongRoute};
    }

    private double haversine(
            double lat1, double lon1,
            double lat2, double lon2) {

        double earthRadius = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * earthRadius
                * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
