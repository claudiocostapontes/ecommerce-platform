package br.com.ecommerce.order.domain.enums;

public enum OrderStatus {
    CREATED("Criado", "Pedido foi criado"),
    PAYMENT_PENDING("Pagamento Pendente", "Aguardando pagamento"),
    PAID("Pago", "Pagamento confirmado"),
    PROCESSING("Processando", "Preparando para envio"),
    SHIPPED("Enviado", "Pedido foi despachado"),
    DELIVERED("Entregue", "Pedido foi entregue"),
    CANCELLED("Cancelado", "Pedido foi cancelado"),
    PAYMENT_FAILED("Falha no Pagamento", "Pagamento foi recusado"),
    REFUNDED("Reembolsado", "Pedido foi reembolsado"),
    RETURNED("Devolvido", "Pedido foi devolvido");
    
    private final String displayName;
    private final String description;
    
    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case CREATED -> newStatus == PAYMENT_PENDING || newStatus == CANCELLED;
            case PAYMENT_PENDING -> newStatus == PAID || newStatus == PAYMENT_FAILED || newStatus == CANCELLED;
            case PAID -> newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING -> newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED -> newStatus == DELIVERED || newStatus == RETURNED;
            case DELIVERED -> newStatus == RETURNED;
            case PAYMENT_FAILED -> newStatus == PAYMENT_PENDING;
            default -> false;
        };
    }
}