package br.com.ecommerce.shipping.application.dto;

import java.math.BigDecimal;

public record ShippingQuoteRequest(
    String zipCode,
    Integer weight,
    Integer quantity
) {}