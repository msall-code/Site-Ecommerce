package com.maket.maket_backend.modules.delivery.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException; // Import spécifique
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix; // Import spécifique
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QRCodeGeneratorService {

    /**
     * Génère un QR Code en Base64.
     * Les exceptions spécifiques WriterException et IOException remplacent le générique Exception.
     */
    public String generateQRCodeBase64(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        // Génération de la matrice
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            // Encodage en Base64
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
    }
}