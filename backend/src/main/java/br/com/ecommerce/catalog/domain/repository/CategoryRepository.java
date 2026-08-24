package br.com.ecommerce.catalog.domain.repository;

import br.com.ecommerce.catalog.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findBySlug(String slug);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true")
    List<Category> findByParentIsNullAndActiveTrue();

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.active = true")
    List<Category> findByParentIdAndActiveTrue(UUID parentId);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true ORDER BY c.displayOrder")
    List<Category> findRootCategories();

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.id = :id")
    Optional<Category> findByIdWithSubcategories(UUID id);

    boolean existsBySlug(String slug);
}