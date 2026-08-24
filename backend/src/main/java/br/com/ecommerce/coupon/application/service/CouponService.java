package br.com.ecommerce.coupon.application.service;

import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.coupon.application.dto.CouponDTO;
import br.com.ecommerce.coupon.application.dto.CouponRequest;
import br.com.ecommerce.coupon.domain.entity.Coupon;
import br.com.ecommerce.coupon.domain.repository.CouponRepository;
import br.com.ecommerce.shared.dto.PageResponse;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    
    private final CouponRepository couponRepository;
    
    @Transactional(readOnly = true)
    public CouponDTO validateCoupon(String code, Money purchaseAmount) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessException("Invalid coupon code"));
        
        if (!coupon.isValid()) {
            throw new BusinessException("Coupon is expired or inactive");
        }
        
        if (!coupon.isApplicable(purchaseAmount)) {
            throw new BusinessException("Coupon is not applicable for this purchase amount");
        }
        
        return toDTO(coupon);
    }
    
    @Transactional(readOnly = true)
    public CouponDTO findByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        return toDTO(coupon);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<CouponDTO> findActiveCoupons(Pageable pageable) {
        Page<Coupon> page = couponRepository.findActiveCoupons(pageable);
        Page<CouponDTO> dtoPage = page.map(this::toDTO);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<CouponDTO> findAll(Pageable pageable) {
        Page<Coupon> page = couponRepository.findByActiveTrue(pageable);
        Page<CouponDTO> dtoPage = page.map(this::toDTO);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional
    public CouponDTO create(CouponRequest request) {
        String code = request.code().toUpperCase();
        
        if (couponRepository.existsByCode(code)) {
            throw new BusinessException("Coupon with this code already exists");
        }
        
        if (request.validUntil().isBefore(request.validFrom())) {
            throw new BusinessException("Valid until date must be after valid from date");
        }
        
        Coupon coupon = Coupon.builder()
                .code(code)
                .description(request.description())
                .discountType(request.discountType())
                .discountValue(request.discountValue())
                .maxDiscount(request.maxDiscount())
                .minPurchase(request.minPurchase())
                .validFrom(request.validFrom())
                .validUntil(request.validUntil())
                .maxUses(request.maxUses())
                .maxUsesPerCustomer(request.maxUsesPerCustomer())
                .active(true)
                .build();
        
        coupon = couponRepository.save(coupon);
        log.info("Coupon created: {}", code);
        
        return toDTO(coupon);
    }
    
    @Transactional
    public CouponDTO update(UUID id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id));
        
        if (!coupon.getCode().equals(request.code().toUpperCase()) && 
            couponRepository.existsByCode(request.code().toUpperCase())) {
            throw new BusinessException("Coupon with this code already exists");
        }
        
        if (request.validUntil().isBefore(request.validFrom())) {
            throw new BusinessException("Valid until date must be after valid from date");
        }
        
        coupon.setCode(request.code().toUpperCase());
        coupon.setDescription(request.description());
        coupon.setDiscountType(request.discountType());
        coupon.setDiscountValue(request.discountValue());
        coupon.setMaxDiscount(request.maxDiscount());
        coupon.setMinPurchase(request.minPurchase());
        coupon.setValidFrom(request.validFrom());
        coupon.setValidUntil(request.validUntil());
        coupon.setMaxUses(request.maxUses());
        coupon.setMaxUsesPerCustomer(request.maxUsesPerCustomer());
        
        coupon = couponRepository.save(coupon);
        log.info("Coupon updated: {}", coupon.getCode());
        
        return toDTO(coupon);
    }
    
    @Transactional
    public void deactivate(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id));
        
        coupon.setActive(false);
        couponRepository.save(coupon);
        log.info("Coupon deactivated: {}", coupon.getCode());
    }
    
    @Transactional
    public void registerUse(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id));
        
        coupon.registerUse();
        couponRepository.save(coupon);
    }
    
    @Transactional
    public void deleteExpiredCoupons() {
        List<Coupon> expiredCoupons = couponRepository.findExpiredCoupons(LocalDateTime.now());
        
        for (Coupon coupon : expiredCoupons) {
            coupon.setActive(false);
        }
        
        couponRepository.saveAll(expiredCoupons);
        log.info("Deactivated {} expired coupons", expiredCoupons.size());
    }
    
    private CouponDTO toDTO(Coupon coupon) {
        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .maxDiscount(coupon.getMaxDiscount())
                .minPurchase(coupon.getMinPurchase())
                .validFrom(coupon.getValidFrom())
                .validUntil(coupon.getValidUntil())
                .maxUses(coupon.getMaxUses())
                .currentUses(coupon.getCurrentUses())
                .maxUsesPerCustomer(coupon.getMaxUsesPerCustomer())
                .active(coupon.getActive())
                .valid(coupon.isValid())
                .build();
    }
}