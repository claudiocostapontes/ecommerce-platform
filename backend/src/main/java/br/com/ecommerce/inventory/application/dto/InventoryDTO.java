package br.com.ecommerce.inventory.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productSku;
    private Integer quantityAvailable;
    private Integer quantityReserved;
    private Integer totalQuantity;
    private Integer minimumStock;
    private String location;
    private Boolean belowMinimum;
}