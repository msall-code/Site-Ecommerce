package com.maket.maket_backend.modules.order.service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maket.maket_backend.modules.order.dto.OrderRequestDTO;
import com.maket.maket_backend.modules.order.dto.OrderResponseDTO;
import com.maket.maket_backend.modules.order.model.Order;
import com.maket.maket_backend.modules.order.model.OrderItem;
import com.maket.maket_backend.modules.order.repository.OrderRepository;
import com.maket.maket_backend.modules.shop.model.Product;
import com.maket.maket_backend.modules.shop.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DeliveryFeeService deliveryService;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto, String clientId) {
        Order order = new Order();
        order.setClientId(clientId);
        
        order.setDeliveryLatitude(dto.getDeliveryLatitude());
        order.setDeliveryLongitude(dto.getDeliveryLongitude());
        order.setDeliveryAddress(dto.getDeliveryAddress());

        AtomicReference<Double> maxDistance = new AtomicReference<>(0.0);
        AtomicBoolean coldChainNeeded = new AtomicBoolean(false);

        List<OrderItem> items = dto.getItems().stream().map(itemDto -> {
            Product product = productRepository.findById(Objects.requireNonNull(itemDto.getProductId()))
                .orElseThrow(() -> new RuntimeException("Produit non trouvé : " + itemDto.getProductId()));
            
            double currentDist = deliveryService.calculateDistance(
                0.0, 0.0, 
                dto.getDeliveryLatitude(), dto.getDeliveryLongitude()
            );

            if (currentDist > maxDistance.get()) {
                maxDistance.set(currentDist);
            }

            // --- CORRECTION CI-DESSOUS ---
            // On utilise getRequiresColdChain() car c'est maintenant un objet Boolean.
            // On utilise Boolean.TRUE.equals() pour gérer les valeurs null en base.
            if (product.getCategory() != null && Boolean.TRUE.equals(product.getCategory().getRequiresColdChain())) {
                coldChainNeeded.set(true);
            }

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            return item;
        }).toList();

        order.setItems(items);
        
        double productsTotal = items.stream()
                .mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity())
                .sum();

        double deliveryFee = deliveryService.calculateDeliveryFee(maxDistance.get(), coldChainNeeded.get());
        
        order.setDeliveryFee(deliveryFee);
        order.setTotalAmount(productsTotal + deliveryFee);

        Order savedOrder = orderRepository.save(order);
        return mapToResponseDTO(savedOrder);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}