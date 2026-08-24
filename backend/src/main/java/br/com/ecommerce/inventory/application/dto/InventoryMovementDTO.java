package br.com.ecommerce.inventory.application.dto;

import br.com.ecommerce.inventory.domain.enums.MovementType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryMovementDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private MovementType movementType;
    private Integer quantity;
    private Integer quantityBefore;
    private Integer quantityAfter;
    private UUID referenceId;
    private String referenceType;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
}