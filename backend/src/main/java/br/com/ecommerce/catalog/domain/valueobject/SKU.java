package br.com.ecommerce.catalog.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SKU {

    private String code;

    public static SKU of(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("SKU code cannot be null or blank");
        }
        return new SKU(code.trim().toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SKU sku = (SKU) o;
        return Objects.equals(code, sku.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
