package br.com.ecommerce.inventory.domain.enums;

public enum MovementType {
    INITIAL_STOCK("Initial Stock"),
    PURCHASE("Purchase"),
    SALE("Sale"),
    RETURN("Return"),
    ADJUSTMENT("Adjustment"),
    RESERVATION("Reservation"),
    RELEASE("Release"),
    DAMAGE("Damage"),
    TRANSFER("Transfer");
    
    private final String description;
    
    MovementType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}