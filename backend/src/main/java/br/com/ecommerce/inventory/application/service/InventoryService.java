package br.com.ecommerce.inventory.application.service;

import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.catalog.domain.repository.ProductRepository;
import br.com.ecommerce.inventory.application.dto.InventoryDTO;
import br.com.ecommerce.inventory.application.dto.InventoryMovementDTO;
import br.com.ecommerce.inventory.application.dto.StockAdjustmentRequest;
import br.com.ecommerce.inventory.application.dto.StockReservationRequest;
import br.com.ecommerce.inventory.domain.entity.Inventory;
import br.com.ecommerce.inventory.domain.entity.InventoryMovement;
import br.com.ecommerce.inventory.domain.enums.MovementType;
import br.com.ecommerce.inventory.domain.repository.InventoryMovementRepository;
import br.com.ecommerce.inventory.domain.repository.InventoryRepository;
import br.com.ecommerce.shared.dto.PageResponse;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public InventoryDTO findByProductId(UUID productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        return toDTO(inventory);
    }
    
    @Transactional(readOnly = true)
    public List<InventoryDTO> findBelowMinimum() {
        return inventoryRepository.findBelowMinimumStock()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<InventoryDTO> findOutOfStock() {
        return inventoryRepository.findOutOfStock()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PageResponse<InventoryMovementDTO> getMovementHistory(UUID productId, Pageable pageable) {
        Page<InventoryMovement> page = movementRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        Page<InventoryMovementDTO> dtoPage = page.map(this::toMovementDTO);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional
    public InventoryDTO addStock(StockAdjustmentRequest request) {
        Inventory inventory = getOrCreateInventory(request.productId());
        
        int quantityBefore = inventory.getTotalQuantity();
        inventory.addStock(request.quantity());
        
        inventory = inventoryRepository.save(inventory);
        
        recordMovement(inventory, MovementType.PURCHASE, request.quantity(), 
                quantityBefore, inventory.getTotalQuantity(), null, null, request.notes());
        
        log.info("Added {} units to product {} inventory", request.quantity(), request.productId());
        
        return toDTO(inventory);
    }
    
    @Transactional
    public InventoryDTO adjustStock(UUID productId, Integer newQuantity, String notes) {
        Inventory inventory = getOrCreateInventory(productId);
        
        int quantityBefore = inventory.getTotalQuantity();
        int difference = newQuantity - inventory.getQuantityAvailable();
        
        if (difference > 0) {
            inventory.addStock(difference);
        } else if (difference < 0) {
            inventory.removeStock(Math.abs(difference));
        }
        
        inventory = inventoryRepository.save(inventory);
        
        recordMovement(inventory, MovementType.ADJUSTMENT, Math.abs(difference), 
                quantityBefore, inventory.getTotalQuantity(), null, null, notes);
        
        log.info("Adjusted product {} inventory to {}", productId, newQuantity);
        
        return toDTO(inventory);
    }
    
    @Transactional
    public void reserveStock(StockReservationRequest request) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + request.productId()));
        
        if (!inventory.hasStock(request.quantity())) {
            throw new BusinessException("Insufficient stock for product: " + request.productId());
        }
        
        int quantityBefore = inventory.getTotalQuantity();
        inventory.reserveStock(request.quantity());
        
        inventory = inventoryRepository.save(inventory);
        
        recordMovement(inventory, MovementType.RESERVATION, request.quantity(), 
                quantityBefore, inventory.getTotalQuantity(), 
                request.referenceId(), request.referenceType(), "Stock reserved");
        
        log.info("Reserved {} units of product {} for {}", 
                request.quantity(), request.productId(), request.referenceId());
    }
    
    @Transactional
    public void releaseReservation(UUID productId, Integer quantity, UUID referenceId) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        
        int quantityBefore = inventory.getTotalQuantity();
        inventory.releaseReservation(quantity);
        
        inventory = inventoryRepository.save(inventory);
        
        recordMovement(inventory, MovementType.RELEASE, quantity, 
                quantityBefore, inventory.getTotalQuantity(), 
                referenceId, "ORDER", "Reservation released");
        
        log.info("Released {} reserved units of product {}", quantity, productId);
    }
    
    @Transactional
    public void confirmReservation(UUID productId, Integer quantity, UUID referenceId) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        
        int quantityBefore = inventory.getTotalQuantity();
        inventory.confirmReservation(quantity);
        
        inventory = inventoryRepository.save(inventory);
        
        recordMovement(inventory, MovementType.SALE, quantity, 
                quantityBefore, inventory.getTotalQuantity(), 
                referenceId, "ORDER", "Sale confirmed");
        
        log.info("Confirmed sale of {} units of product {}", quantity, productId);
    }
    
    @Transactional
    public void returnStock(UUID productId, Integer quantity, UUID referenceId, String notes) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        
        int quantityBefore = inventory.getTotalQuantity();
        inventory.addStock(quantity);
        
        inventory = inventoryRepository.save(inventory);
        
        recordMovement(inventory, MovementType.RETURN, quantity, 
                quantityBefore, inventory.getTotalQuantity(), 
                referenceId, "ORDER", notes);
        
        log.info("Returned {} units to product {} inventory", quantity, productId);
    }
    
    @Transactional
    public InventoryDTO setMinimumStock(UUID productId, Integer minimumStock) {
        Inventory inventory = getOrCreateInventory(productId);
        inventory.setMinimumStock(minimumStock);
        inventory = inventoryRepository.save(inventory);
        
        log.info("Set minimum stock for product {} to {}", productId, minimumStock);
        
        return toDTO(inventory);
    }
    
    private Inventory getOrCreateInventory(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
                    
                    Inventory newInventory = Inventory.builder()
                            .product(product)
                            .quantityAvailable(0)
                            .quantityReserved(0)
                            .minimumStock(0)
                            .build();
                    
                    return inventoryRepository.save(newInventory);
                });
    }
    
    private void recordMovement(Inventory inventory, MovementType type, Integer quantity,
                                Integer quantityBefore, Integer quantityAfter,
                                UUID referenceId, String referenceType, String notes) {
        InventoryMovement movement = InventoryMovement.builder()
                .product(inventory.getProduct())
                .movementType(type)
                .quantity(quantity)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .notes(notes)
                .build();
        
        movementRepository.save(movement);
    }
    
    private InventoryDTO toDTO(Inventory inventory) {
        return InventoryDTO.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .productSku(inventory.getProduct().getSku().getCode())
                .quantityAvailable(inventory.getQuantityAvailable())
                .quantityReserved(inventory.getQuantityReserved())
                .totalQuantity(inventory.getTotalQuantity())
                .minimumStock(inventory.getMinimumStock())
                .location(inventory.getLocation())
                .belowMinimum(inventory.isBelowMinimum())
                .build();
    }
    
    private InventoryMovementDTO toMovementDTO(InventoryMovement movement) {
        return InventoryMovementDTO.builder()
                .id(movement.getId())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .movementType(movement.getMovementType())
                .quantity(movement.getQuantity())
                .quantityBefore(movement.getQuantityBefore())
                .quantityAfter(movement.getQuantityAfter())
                .referenceId(movement.getReferenceId())
                .referenceType(movement.getReferenceType())
                .notes(movement.getNotes())
                .createdBy(movement.getCreatedBy())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}