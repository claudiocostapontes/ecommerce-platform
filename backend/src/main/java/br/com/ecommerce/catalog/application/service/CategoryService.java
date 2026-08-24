package br.com.ecommerce.catalog.application.service;

import br.com.ecommerce.catalog.application.dto.CategoryDTO;
import br.com.ecommerce.catalog.application.dto.CategoryRequest;
import br.com.ecommerce.catalog.domain.entity.Category;
import br.com.ecommerce.catalog.domain.repository.CategoryRepository;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import br.com.ecommerce.shared.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAllRootCategories() {
        return categoryRepository.findRootCategories()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CategoryDTO findById(UUID id) {
        Category category = categoryRepository.findByIdWithSubcategories(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return toDTOWithSubcategories(category);
    }
    
    @Transactional(readOnly = true)
    public CategoryDTO findBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return toDTO(category);
    }
    
    @Transactional
    public CategoryDTO create(CategoryRequest request) {
        String slug = SlugGenerator.toSlug(request.name());
        
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("Category with this name already exists");
        }
        
        Category category = Category.builder()
                .name(request.name())
                .slug(slug)
                .description(request.description())
                .imageUrl(request.imageUrl())
                .displayOrder(request.displayOrder() != null ? request.displayOrder() : 0)
                .active(true)
                .build();
        
        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category", request.parentId()));
            category.setParent(parent);
        }
        
        category = categoryRepository.save(category);
        log.info("Category created: {}", category.getName());
        
        return toDTO(category);
    }
    
    @Transactional
    public CategoryDTO update(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        
        if (!category.getName().equals(request.name())) {
            String slug = SlugGenerator.toSlug(request.name());
            if (categoryRepository.existsBySlug(slug)) {
                throw new BusinessException("Category with this name already exists");
            }
            category.setSlug(slug);
        }
        
        category.setName(request.name());
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());
        category.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : category.getDisplayOrder());
        
        if (request.parentId() != null) {
            if (request.parentId().equals(id)) {
                throw new BusinessException("Category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category", request.parentId()));
            category.setParent(parent);
        }
        
        category = categoryRepository.save(category);
        log.info("Category updated: {}", category.getName());
        
        return toDTO(category);
    }
    
    @Transactional
    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        
        category.setActive(false);
        categoryRepository.save(category);
        log.info("Category deactivated: {}", category.getName());
    }
    
    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .active(category.getActive())
                .displayOrder(category.getDisplayOrder())
                .build();
    }
    
    private CategoryDTO toDTOWithSubcategories(Category category) {
        CategoryDTO dto = toDTO(category);
        
        if (!category.getChildren().isEmpty()) {
            List<CategoryDTO> subcategoryDTOs = category.getChildren()
                    .stream()
                    .filter(Category::getActive)
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            dto.setSubcategories(subcategoryDTOs);
        }
        
        return dto;
    }
}