package br.com.ecommerce.catalog.domain.repository;

import br.com.ecommerce.catalog.domain.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    Optional<Brand> findBySlug(String slug);

    Page<Brand> findByActiveTrue(Pageable pageable);

    List<Brand> findByActiveTrueOrderByName();

    boolean existsBySlug(String slug);
}
