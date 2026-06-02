package com.maket.maket_backend.modules.shop.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class QRCodeService {
    
    public String generateProductQRCode(Long productId, Double lat, Double lon) {
        // Format : ID_PROD|LAT|LON|CHAINE_UNIQUE
        String uniqueKey = UUID.randomUUID().toString().substring(0, 8);
        double latitude = (lat != null) ? lat : 0.0;
        double longitude = (lon != null) ? lon : 0.0;

        return String.format("MKP-%d|%.6f|%.6f|%s", 
            productId, latitude, longitude, uniqueKey);
    }
}