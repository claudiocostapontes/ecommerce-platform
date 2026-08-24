package br.com.ecommerce.catalog.application.service;

import br.com.ecommerce.catalog.application.dto.BrandDTO;
import br.com.ecommerce.catalog.application.dto.BrandRequest;
import br.com.ecommerce.catalog.domain.entity.Brand;
import br.com.ecommerce.catalog.domain.repository.BrandRepository;
import br.com.ecommerce.shared.dto.PageResponse;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import br.com.ecommerce.shared.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public PageResponse<BrandDTO> findAll(Pageable pageable) {
        Page<Brand> page = brandRepository.findByActiveTrue(pageable);
        Page<BrandDTO> dtoPage = page.map(this::toDTO);
        return PageResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public List<BrandDTO> findAllActive() {
        return brandRepository.findByActiveTrueOrderByName()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BrandDTO findById(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
        return toDTO(brand);
    }

    @Transactional(readOnly = true)
    public BrandDTO findBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with slug: " + slug));
        return toDTO(brand);
    }

    @Transactional
    public BrandDTO create(BrandRequest request) {
        String slug = SlugGenerator.toSlug(request.name());

        if (brandRepository.existsBySlug(slug)) {
            throw new BusinessException("Brand with this name already exists");
        }

        Brand brand = Brand.builder()
                .name(request.name())
                .slug(slug)
                .description(request.description())
                .logoUrl(request.logoUrl())
                .websiteUrl(request.websiteUrl())
                .active(true)
                .build();

        brand = brandRepository.save(brand);
        log.info("Brand created: {}", brand.getName());

        return toDTO(brand);
    }

    @Transactional
    public BrandDTO update(UUID id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));

        if (!brand.getName().equals(request.name())) {
            String slug = SlugGenerator.toSlug(request.name());
            if (brandRepository.existsBySlug(slug)) {
                throw new BusinessException("Brand with this name already exists");
            }
            brand.setSlug(slug);
        }

        brand.setName(request.name());
        brand.setDescription(request.description());
        brand.setLogoUrl(request.logoUrl());
        brand.setWebsiteUrl(request.websiteUrl());

        brand = brandRepository.save(brand);
        log.info("Brand updated: {}", brand.getName());

        return toDTO(brand);
    }

    @Transactional
    public void delete(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));

        brand.setActive(false);
        brandRepository.save(brand);
        log.info("Brand deactivated: {}", brand.getName());
    }

    private BrandDTO toDTO(Brand brand) {
        return BrandDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .websiteUrl(brand.getWebsiteUrl())
                .active(brand.getActive())
                .build();
    }
}