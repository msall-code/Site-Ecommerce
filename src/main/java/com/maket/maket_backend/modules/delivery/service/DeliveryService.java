package com.maket.maket_backend.modules.delivery.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maket.maket_backend.modules.delivery.model.Delivery;
import com.maket.maket_backend.modules.delivery.model.DeliveryStatus;
import com.maket.maket_backend.modules.delivery.repository.DeliveryRepository;
import com.maket.maket_backend.modules.order.model.Order;
import com.maket.maket_backend.modules.order.model.OrderStatus;
import com.maket.maket_backend.modules.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Delivery initiateDelivery(Long orderId) {
        Objects.requireNonNull(orderId, "OrderId ne peut pas être nul");
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.WAITING_FOR_RIDER);
        delivery.setPickupCode("TEST"); 
        delivery.setDeliveryCode("DELIV-" + UUID.randomUUID().toString().substring(0, 8));
        
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public String validatePickup(Long deliveryId, String scannedCode, String riderId) {
        Objects.requireNonNull(deliveryId, "DeliveryId ne peut pas être nul");
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Livraison #" + deliveryId + " non trouvée"));

        if (delivery.getPickupCode().equals(scannedCode)) {
            delivery.setRiderId(riderId);
            delivery.setPickedUp(true);
            delivery.setStatus(DeliveryStatus.IN_TRANSIT);
            deliveryRepository.save(delivery);

            updateOrderStatus(delivery.getOrderId(), OrderStatus.SHIPPED);

            return "Pickup validé !";
        }
        return "Code Pickup invalide.";
    }

    @Transactional
    public String validateFinalDelivery(Long deliveryId, String scannedCode) {
        Objects.requireNonNull(deliveryId, "DeliveryId ne peut pas être nul");
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));

        if (delivery.getDeliveryCode().equals(scannedCode)) {
            delivery.setDelivered(true);
            delivery.setStatus(DeliveryStatus.DELIVERED);
            deliveryRepository.save(delivery);
            updateOrderStatus(delivery.getOrderId(), OrderStatus.DELIVERED);
            return "Livraison confirmée !";
        }
        return "Code final invalide.";
    }

    private void updateOrderStatus(Long orderId, OrderStatus status) {
        Objects.requireNonNull(orderId, "OrderId ne peut pas être nul");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande #" + orderId + " non trouvée"));
        order.setStatus(status);
        orderRepository.save(order);
    }
}