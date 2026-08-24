package br.com.ecommerce.catalog.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private UUID parentId;
    private List<CategoryDTO> subcategories;
    private Boolean active;
    private Integer displayOrder;
}