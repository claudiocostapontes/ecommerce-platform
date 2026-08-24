package br.com.ecommerce.payment.domain.enums;

public enum PaymentStatus {
    PENDING("Pendente", "Aguardando processamento"),
    PROCESSING("Processando", "Processando pagamento"),
    AUTHORIZED("Autorizado", "Pagamento autorizado"),
    PAID("Pago", "Pagamento confirmado"),
    DECLINED("Recusado", "Pagamento foi recusado"),
    FAILED("Falha", "Erro ao processar pagamento"),
    CANCELLED("Cancelado", "Pagamento foi cancelado"),
    REFUNDED("Reembolsado", "Pagamento foi reembolsado"),
    PARTIALLY_REFUNDED("Reembolso Parcial", "Pagamento foi parcialmente reembolsado");
    
    private final String displayName;
    private final String description;
    
    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}