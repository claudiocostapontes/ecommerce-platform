package br.com.ecommerce.shipping.application.service;

import br.com.ecommerce.order.domain.entity.Order;
import br.com.ecommerce.order.domain.repository.OrderRepository;
import br.com.ecommerce.shipping.application.dto.CreateShipmentRequest;
import br.com.ecommerce.shipping.application.dto.ShipmentDTO;
import br.com.ecommerce.shipping.application.dto.ShippingQuoteDTO;
import br.com.ecommerce.shipping.application.dto.ShippingQuoteRequest;
import br.com.ecommerce.shipping.domain.entity.Shipment;
import br.com.ecommerce.shipping.domain.repository.ShipmentRepository;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {
    
    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    
    @Transactional(readOnly = true)
    public ShipmentDTO getShipment(UUID shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", shipmentId));
        return toDTO(shipment);
    }
    
    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentByOrder(UUID orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found for order: " + orderId));
        return toDTO(shipment);
    }
    
    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentByTracking(String trackingCode) {
        Shipment shipment = shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with tracking: " + trackingCode));
        return toDTO(shipment);
    }
    
    @Transactional
    public ShipmentDTO createShipment(CreateShipmentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));
        
        Shipment shipment = Shipment.builder()
                .order(order)
                .trackingCode(request.trackingCode())
                .carrier(request.carrier())
                .shippingMethod(request.shippingMethod())
                .cost(request.cost())
                .estimatedDelivery(request.estimatedDelivery())
                .recipientName(order.getDeliveryRecipientName())
                .recipientAddress(order.getDeliveryAddress())
                .notes(request.notes())
                .build();
        
        shipment = shipmentRepository.save(shipment);
        
        // Update order with tracking
        order.setTrackingCode(request.trackingCode());
        order.setEstimatedDelivery(request.estimatedDelivery());
        orderRepository.save(order);
        
        log.info("Shipment created for order: {}", order.getOrderNumber());
        
        return toDTO(shipment);
    }
    
    @Transactional
    public void markAsDelivered(UUID shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", shipmentId));
        
        shipment.setActualDelivery(LocalDateTime.now());
        shipmentRepository.save(shipment);
        
        log.info("Shipment marked as delivered: {}", shipmentId);
    }
    
    @Transactional(readOnly = true)
    public List<ShippingQuoteDTO> getShippingQuotes(ShippingQuoteRequest request) {
        // Mock implementation - in production, integrate with real shipping APIs
        List<ShippingQuoteDTO> quotes = new ArrayList<>();
        
        // Standard Shipping
        quotes.add(new ShippingQuoteDTO(
                "Correios",
                "PAC",
                new BigDecimal("15.00"),
                java.time.LocalDate.now().plusDays(7)
        ));
        
        // Express Shipping
        quotes.add(new ShippingQuoteDTO(
                "Correios",
                "SEDEX",
                new BigDecimal("30.00"),
                java.time.LocalDate.now().plusDays(2)
        ));
        
        // Fedex
        quotes.add(new ShippingQuoteDTO(
                "Fedex",
                "Standard",
                new BigDecimal("25.00"),
                java.time.LocalDate.now().plusDays(5)
        ));
        
        return quotes;
    }
    
    private ShipmentDTO toDTO(Shipment shipment) {
        return ShipmentDTO.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrder().getId())
                .trackingCode(shipment.getTrackingCode())
                .carrier(shipment.getCarrier())
                .shippingMethod(shipment.getShippingMethod())
                .cost(shipment.getCost())
                .estimatedDelivery(shipment.getEstimatedDelivery())
                .actualDelivery(shipment.getActualDelivery())
                .recipientName(shipment.getRecipientName())
                .recipientAddress(shipment.getRecipientAddress())
                .notes(shipment.getNotes())
                .build();
    }
}