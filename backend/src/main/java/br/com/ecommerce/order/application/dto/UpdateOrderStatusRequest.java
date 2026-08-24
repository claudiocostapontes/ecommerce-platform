package br.com.ecommerce.order.application.dto;

import br.com.ecommerce.order.domain.enums.OrderStatus;

public record UpdateOrderStatusRequest(
    OrderStatus status,
    String reason
) {}