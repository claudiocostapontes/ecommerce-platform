package br.com.ecommerce.catalog.domain.repository;

import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.catalog.domain.valueobject.SKU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(UUID id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand WHERE p.slug = :slug AND p.active = true")
    Optional<Product> findBySlugWithDetails(String slug);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(UUID categoryId, Pageable pageable);

    Page<Product> findByBrandIdAndActiveTrue(UUID brandId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.featured = true ORDER BY p.viewCount DESC")
    Page<Product> findFeaturedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "p.price.amount BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.viewCount DESC LIMIT 10")
    List<Product> findTop10ByActiveTrueOrderByViewCountDesc();

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.ratingAverage DESC LIMIT 10")
    List<Product> findTop10ByActiveTrueOrderByRatingAverageDesc();

    boolean existsBySlug(String slug);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.sku.code = :skuCode")
    boolean existsBySku(@Param("skuCode") String skuCode);
}