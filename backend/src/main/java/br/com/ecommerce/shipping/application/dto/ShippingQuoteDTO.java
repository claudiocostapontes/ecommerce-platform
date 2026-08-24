package br.com.ecommerce.shipping.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ShippingQuoteDTO(
        String carrier,
        String shippingMethod,
        BigDecimal cost,
        LocalDate estimatedDelivery
) {}