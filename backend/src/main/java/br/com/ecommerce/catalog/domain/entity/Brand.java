package br.com.ecommerce.catalog.domain.entity;

import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands", indexes = {
        @Index(name = "idx_brands_slug", columnList = "slug"),
        @Index(name = "idx_brands_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends AuditableEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(length = 1000)
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
