package com.maket.maket_backend.modules.order.service;

import org.springframework.stereotype.Service;

@Service
public class DeliveryFeeService {
    // Rayon de la terre en KM
    private static final double EARTH_RADIUS = 6371;

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public double calculateDeliveryFee(double distanceKm, boolean requiresColdChain) {
        double total = 1000.0 + (distanceKm * 200.0); // 1000 base + 200/km
        return requiresColdChain ? total + 500.0 : total;
    }
}