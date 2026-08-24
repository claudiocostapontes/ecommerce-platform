package br.com.ecommerce.catalog.application.service;

import br.com.ecommerce.catalog.application.dto.*;
import br.com.ecommerce.catalog.domain.entity.Brand;
import br.com.ecommerce.catalog.domain.entity.Category;
import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.catalog.domain.entity.ProductImage;
import br.com.ecommerce.catalog.domain.repository.BrandRepository;
import br.com.ecommerce.catalog.domain.repository.CategoryRepository;
import br.com.ecommerce.catalog.domain.repository.ProductRepository;
import br.com.ecommerce.catalog.domain.valueobject.EAN;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.catalog.domain.valueobject.SKU;
import br.com.ecommerce.shared.dto.PageResponse;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import br.com.ecommerce.shared.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAll(Pageable pageable) {
        Page<Product> page = productRepository.findByActiveTrue(pageable);
        Page<ProductResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse findById(UUID id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.incrementViewCount();
        productRepository.save(product);

        return toDetailResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse findBySlug(String slug) {
        Product product = productRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));

        product.incrementViewCount();
        productRepository.save(product);

        return toDetailResponse(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findByCategory(UUID categoryId, Pageable pageable) {
        Page<Product> page = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        Page<ProductResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findByBrand(UUID brandId, Pageable pageable) {
        Page<Product> page = productRepository.findByBrandIdAndActiveTrue(brandId, pageable);
        Page<ProductResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(String query, Pageable pageable) {
        Page<Product> page = productRepository.searchProducts(query, pageable);
        Page<ProductResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findFeatured(Pageable pageable) {
        Page<Product> page = productRepository.findFeaturedProducts(pageable);
        Page<ProductResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findMostViewed() {
        return productRepository.findTop10ByActiveTrueOrderByViewCountDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findTopRated() {
        return productRepository.findTop10ByActiveTrueOrderByRatingAverageDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findWithFilters(
            UUID categoryId,
            UUID brandId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String search,
            Pageable pageable) {

        Specification<Product> spec = Specification.where(null);

        spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), true));

        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }

        if (brandId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("brand").get("id"), brandId));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price").get("amount"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price").get("amount"), maxPrice));
        }

        if (search != null && !search.isBlank()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), searchPattern),
                            cb.like(cb.lower(root.get("description")), searchPattern)
                    )
            );
        }

        Page<Product> page = productRepository.findAll(spec, pageable);
        Page<ProductResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public ProductDetailResponse create(ProductRequest request) {
        String slug = SlugGenerator.toSlug(request.name());

        if (productRepository.existsBySlug(slug)) {
            throw new BusinessException("Product with this name already exists");
        }

        SKU sku = SKU.of(request.sku());
        if (productRepository.existsBySku(sku.getCode())) {
            throw new BusinessException("Product with this SKU already exists");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        Brand brand = null;
        if (request.brandId() != null) {
            brand = brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", request.brandId()));
        }

        Money price = Money.of(request.price());
        Money promotionalPrice = request.promotionalPrice() != null
                ? Money.of(request.promotionalPrice())
                : null;

        if (promotionalPrice != null && promotionalPrice.isGreaterThan(price)) {
            throw new BusinessException("Promotional price cannot be greater than regular price");
        }

        Map<String, String> specifications = null;
        if (request.specifications() != null) {
            specifications = request.specifications().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() != null ? e.getValue().toString() : null));
        }

        Product product = Product.builder()
                .name(request.name())
                .slug(slug)
                .sku(sku)
                .ean(request.ean() != null ? EAN.of(request.ean()) : null)
                .description(request.description())
                .shortDescription(request.shortDescription())
                .price(price)
                .promotionalPrice(promotionalPrice)
                .promotionStart(request.promotionStart())
                .promotionEnd(request.promotionEnd())
                .weight(request.weight() != null ? request.weight() : BigDecimal.ZERO)
                .width(request.width() != null ? request.width() : BigDecimal.ZERO)
                .height(request.height() != null ? request.height() : BigDecimal.ZERO)
                .depth(request.depth() != null ? request.depth() : BigDecimal.ZERO)
                .category(category)
                .brand(brand)
                .specifications(specifications)
                .active(true)
                .featured(request.featured() != null ? request.featured() : false)
                .viewCount(0L)
                .ratingAverage(BigDecimal.ZERO)
                .ratingCount(0)
                .build();

        if (request.images() != null && !request.images().isEmpty()) {
            for (int i = 0; i < request.images().size(); i++) {
                ProductImageRequest imgReq = request.images().get(i);
                ProductImage image = ProductImage.builder()
                        .imageUrl(imgReq.imageUrl())
                        .thumbnailUrl(imgReq.thumbnailUrl())
                        .altText(imgReq.altText())
                        .displayOrder(imgReq.displayOrder() != null ? imgReq.displayOrder() : i)
                        .isPrimary(imgReq.isPrimary() != null ? imgReq.isPrimary() : (i == 0))
                        .build();
                product.addImage(image);
            }
        }

        product = productRepository.save(product);
        log.info("Product created: {} (SKU: {})", product.getName(), product.getSku());

        return toDetailResponse(product);
    }

    @Transactional
    public ProductDetailResponse update(UUID id, ProductRequest request) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (!product.getName().equals(request.name())) {
            String slug = SlugGenerator.toSlug(request.name());
            if (productRepository.existsBySlug(slug)) {
                throw new BusinessException("Product with this name already exists");
            }
            product.setSlug(slug);
        }

        if (!product.getSku().equals(SKU.of(request.sku()))) {
            SKU newSku = SKU.of(request.sku());
            if (productRepository.existsBySku(newSku.getCode())) {
                throw new BusinessException("Product with this SKU already exists");
            }
            product.setSku(newSku);
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        Brand brand = null;
        if (request.brandId() != null) {
            brand = brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", request.brandId()));
        }

        Money price = Money.of(request.price());
        Money promotionalPrice = request.promotionalPrice() != null
                ? Money.of(request.promotionalPrice())
                : null;

        if (promotionalPrice != null && promotionalPrice.isGreaterThan(price)) {
            throw new BusinessException("Promotional price cannot be greater than regular price");
        }

        product.setName(request.name());
        product.setEan(request.ean() != null ? EAN.of(request.ean()) : null);
        product.setDescription(request.description());
        product.setShortDescription(request.shortDescription());
        product.setPrice(price);
        product.setPromotionalPrice(promotionalPrice);
        product.setPromotionStart(request.promotionStart());
        product.setPromotionEnd(request.promotionEnd());
        product.setWeight(request.weight() != null ? request.weight() : BigDecimal.ZERO);
        product.setWidth(request.width() != null ? request.width() : BigDecimal.ZERO);
        product.setHeight(request.height() != null ? request.height() : BigDecimal.ZERO);
        product.setDepth(request.depth() != null ? request.depth() : BigDecimal.ZERO);
        product.setCategory(category);
        product.setBrand(brand);
        if (request.specifications() != null) {
            Map<String, String> stringSpecs = request.specifications().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() != null ? e.getValue().toString() : null));
            product.setSpecifications(stringSpecs);
        }
        product.setFeatured(request.featured() != null ? request.featured() : product.getFeatured());

        // Update images
        if (request.images() != null) {
            product.getImages().clear();
            for (int i = 0; i < request.images().size(); i++) {
                ProductImageRequest imgReq = request.images().get(i);
                ProductImage image = ProductImage.builder()
                        .imageUrl(imgReq.imageUrl())
                        .thumbnailUrl(imgReq.thumbnailUrl())
                        .altText(imgReq.altText())
                        .displayOrder(imgReq.displayOrder() != null ? imgReq.displayOrder() : i)
                        .isPrimary(imgReq.isPrimary() != null ? imgReq.isPrimary() : (i == 0))
                        .build();
                product.addImage(image);
            }
        }

        product = productRepository.save(product);
        log.info("Product updated: {} (SKU: {})", product.getName(), product.getSku());

        return toDetailResponse(product);
    }

    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setActive(false);
        productRepository.save(product);
        log.info("Product deactivated: {} (SKU: {})", product.getName(), product.getSku());
    }

    @Transactional
    public void activate(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setActive(true);
        productRepository.save(product);
        log.info("Product activated: {} (SKU: {})", product.getName(), product.getSku());
    }

    private ProductResponse toResponse(Product product) {
        String primaryImage = product.getImages().stream()
                .filter(ProductImage::getIsPrimary)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(product.getImages().isEmpty() ? null : product.getImages().get(0).getImageUrl());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku().getCode())
                .ean(product.getEan() != null ? product.getEan().getCode() : null)
                .shortDescription(product.getShortDescription())
                .price(product.getPrice().getAmount())
                .promotionalPrice(product.getPromotionalPrice() != null
                        ? product.getPromotionalPrice().getAmount()
                        : null)
                .discountPercentage(product.getDiscountPercentage())
                .onPromotion(product.isPromotionActive())
                .categoryName(product.getCategory().getName())
                .categoryId(product.getCategory().getId())
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .primaryImage(primaryImage)
                .active(product.getActive())
                .featured(product.getFeatured())
                .ratingAverage(product.getRatingAverage())
                .ratingCount(product.getRatingCount())
                .build();
    }

    private ProductDetailResponse toDetailResponse(Product product) {
        List<ProductImageDTO> imageDTOs = product.getImages().stream()
                .map(img -> ProductImageDTO.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .thumbnailUrl(img.getThumbnailUrl())
                        .altText(img.getAltText())
                        .displayOrder(img.getDisplayOrder())
                        .isPrimary(img.getIsPrimary())
                        .build())
                .collect(Collectors.toList());

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(product.getCategory().getId())
                .name(product.getCategory().getName())
                .slug(product.getCategory().getSlug())
                .build();

        BrandDTO brandDTO = null;
        if (product.getBrand() != null) {
            brandDTO = BrandDTO.builder()
                    .id(product.getBrand().getId())
                    .name(product.getBrand().getName())
                    .slug(product.getBrand().getSlug())
                    .logoUrl(product.getBrand().getLogoUrl())
                    .build();
        }

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku().getCode())
                .ean(product.getEan() != null ? product.getEan().getCode() : null)
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .price(product.getPrice().getAmount())
                .promotionalPrice(product.getPromotionalPrice() != null
                        ? product.getPromotionalPrice().getAmount()
                        : null)
                .discountPercentage(product.getDiscountPercentage())
                .onPromotion(product.isPromotionActive())
                .promotionStart(product.getPromotionStart())
                .promotionEnd(product.getPromotionEnd())
                .weight(product.getWeight())
                .width(product.getWidth())
                .height(product.getHeight())
                .depth(product.getDepth())
                .category(categoryDTO)
                .brand(brandDTO)
                .images(imageDTOs)
                .specifications(product.getSpecifications())
                .active(product.getActive())
                .featured(product.getFeatured())
                .viewCount(product.getViewCount())
                .ratingAverage(product.getRatingAverage())
                .ratingCount(product.getRatingCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}