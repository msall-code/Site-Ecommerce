package com.maket.maket_backend.modules.delivery.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.maket.maket_backend.modules.delivery.service.DeliveryService;
import com.maket.maket_backend.modules.delivery.service.QRCodeGeneratorService;
import com.google.zxing.WriterException; // Ajouté pour le multicatch
import java.io.IOException;            // Ajouté pour le multicatch

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final QRCodeGeneratorService qrCodeService;

    @PostMapping("/{id}/pickup")
    public ResponseEntity<String> pickup(
            @PathVariable Long id, 
            @RequestParam String code, 
            @AuthenticationPrincipal Jwt jwt) {
        
        if (jwt == null) {
            return ResponseEntity.status(401).body("Utilisateur non authentifié.");
        }

        String riderId = jwt.getSubject(); 
        log.info("Tentative de pickup par le livreur Keycloak ID : {}", riderId);
        
        return ResponseEntity.ok(deliveryService.validatePickup(id, code, riderId));
    }

    @GetMapping("/qrcode/{orderId}")
    public ResponseEntity<String> getQRCode(@PathVariable String orderId) {
        try {
            String urlToScan = "http://192.168.2.181:8099/views/scanner-action.html?id=" + orderId;
            String qrCodeBase64 = qrCodeService.generateQRCodeBase64(urlToScan);
            return ResponseEntity.ok(qrCodeBase64);
        } catch (WriterException | IOException e) { 
            // C'est ici le "multicatch" suggéré par ton IDE
            log.error("Erreur technique lors de la génération du QR Code", e);
            return ResponseEntity.status(500).body("Erreur de génération QR : " + e.getMessage());
        }
    }

    @GetMapping("/initiate-test")
    public ResponseEntity<String> initiateTest() {
        deliveryService.initiateDelivery(101L);
        return ResponseEntity.ok("Livraison de test créée !");
    }
}