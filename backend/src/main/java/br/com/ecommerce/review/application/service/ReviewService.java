package br.com.ecommerce.review.application.service;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.auth.domain.repository.UserRepository;
import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.catalog.domain.repository.ProductRepository;
import br.com.ecommerce.review.application.dto.CreateReviewRequest;
import br.com.ecommerce.review.application.dto.ReviewDTO;
import br.com.ecommerce.review.domain.entity.Review;
import br.com.ecommerce.review.domain.repository.ReviewRepository;
import br.com.ecommerce.shared.dto.PageResponse;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<ReviewDTO> getProductReviews(UUID productId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProductId(productId, pageable);
        Page<ReviewDTO> dtoPage = page.map(this::toDTO);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ReviewDTO> getUserReviews(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Page<Review> page = reviewRepository.findByUserId(user.getId(), pageable);
        Page<ReviewDTO> dtoPage = page.map(this::toDTO);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ReviewDTO> getPendingApproval(Pageable pageable) {
        Page<Review> page = reviewRepository.findPendingApproval(pageable);
        Page<ReviewDTO> dtoPage = page.map(this::toDTO);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional
    public ReviewDTO createReview(UUID productId, String username, CreateReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        // Check if user already reviewed this product
        var existingReview = reviewRepository.findByProductIdAndUserId(productId, user.getId());
        if (existingReview.isPresent()) {
            throw new BusinessException("You have already reviewed this product");
        }
        
        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.rating())
                .title(request.title())
                .content(request.content())
                .verified(false) // Will be verified after purchase check
                .active(false) // Pending moderation
                .build();
        
        review = reviewRepository.save(review);
        
        log.info("Review created for product {} by user {}", productId, username);
        
        return toDTO(review);
    }
    
    @Transactional
    public void approveReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        
        review.setActive(true);
        reviewRepository.save(review);
        
        // Update product rating
        Product product = review.getProduct();
        product.updateRating(review.getRating());
        productRepository.save(product);
        
        log.info("Review approved: {}", reviewId);
    }
    
    @Transactional
    public void rejectReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        
        reviewRepository.delete(review);
        log.info("Review rejected: {}", reviewId);
    }
    
    @Transactional
    public void deleteReview(UUID reviewId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        
        if (!review.getUser().getUsername().equals(username)) {
            throw new BusinessException("You can only delete your own reviews");
        }
        
        reviewRepository.delete(review);
        log.info("Review deleted: {}", reviewId);
    }
    
    @Transactional
    public void markAsHelpful(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        
        review.incrementHelpful();
        reviewRepository.save(review);
    }
    
    @Transactional
    public void markAsUnhelpful(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        
        review.incrementUnhelpful();
        reviewRepository.save(review);
    }
    
    private ReviewDTO toDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFirstName())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .helpfulCount(review.getHelpfulCount())
                .unhelpfulCount(review.getUnhelpfulCount())
                .verified(review.getVerified())
                .createdAt(review.getCreatedAt())
                .build();
    }
}