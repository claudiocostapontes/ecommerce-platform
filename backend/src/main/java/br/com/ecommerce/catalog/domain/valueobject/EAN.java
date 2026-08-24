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
public class EAN {

    private String code;

    public static EAN of(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String normalized = code.trim();
        if (normalized.length() != 13 && normalized.length() != 8) {
            throw new IllegalArgumentException("EAN code must be 8 or 13 digits");
        }
        return new EAN(normalized);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EAN ean = (EAN) o;
        return Objects.equals(code, ean.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
