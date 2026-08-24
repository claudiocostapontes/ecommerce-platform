package br.com.ecommerce.coupon.domain.enums;

public enum DiscountType {
    PERCENTAGE("Percentual", "%"),
    FIXED_AMOUNT("Valor Fixo", "R$");
    
    private final String displayName;
    private final String symbol;
    
    DiscountType(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getSymbol() {
        return symbol;
    }
}