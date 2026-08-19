package com.workforce.ai.attendanceservice.service;

import org.springframework.stereotype.Component;

@Component
public class GeofenceUtil {
    private static final double EARTH_RADIUS_METERS = 6371000;

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2){
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));

        return EARTH_RADIUS_METERS * c;
    }

    public boolean isWithGeofence(double userLat, double userLon,
                                  double officeLat, double officeLon,
                                  double allowedRadiusMeters){
        double distance = calculateDistance(userLat, userLon,officeLat, officeLon);
        return  distance <= allowedRadiusMeters;
    }
}
