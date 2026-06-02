package com.maket.maket_backend.modules.order.controller;

import com.maket.maket_backend.modules.order.dto.OrderRequestDTO;
import com.maket.maket_backend.modules.order.dto.OrderResponseDTO;
import com.maket.maket_backend.modules.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody OrderRequestDTO dto, @AuthenticationPrincipal Jwt jwt) {
        // On récupère l'ID utilisateur directement du Token Keycloak
        return ResponseEntity.ok(orderService.createOrder(dto, jwt.getSubject()));
    }
}