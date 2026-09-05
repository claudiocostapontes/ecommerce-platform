package br.com.ecommerce.cart.application.service;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.auth.domain.repository.UserRepository;
import br.com.ecommerce.cart.application.dto.AddToCartRequest;
import br.com.ecommerce.cart.application.dto.CartResponse;
import br.com.ecommerce.cart.application.dto.UpdateCartItemRequest;
import br.com.ecommerce.cart.domain.entity.Cart;
import br.com.ecommerce.cart.domain.entity.CartItem;
import br.com.ecommerce.cart.domain.repository.CartRepository;
import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.catalog.domain.repository.ProductRepository;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.inventory.application.service.InventoryService;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    /*
     * Este método pode criar o carrinho quando o usuário ainda
     * não possui um. Portanto não deve ser readOnly.
     */
    @Transactional
    public CartResponse getCart(String username) {
        User user = getUser(username);

        Cart cart = cartRepository
                .findByUserIdWithItems(user.getId())
                .orElseGet(() -> createCart(user));

        return toResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCartBySession(String sessionId) {
        Cart cart = cartRepository
                .findBySessionId(sessionId)
                .orElse(null);

        if (cart == null) {
            return emptyCartResponse();
        }

        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(
            String username,
            AddToCartRequest request
    ) {
        User user = getUser(username);

        Product product = productRepository
                .findById(request.productId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Product",
                                request.productId()
                        )
                );

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BusinessException(
                    "This product is not available for purchase"
            );
        }

        var inventory =
                inventoryService.findByProductId(
                        request.productId()
                );

        if (inventory.getQuantityAvailable()
                < request.quantity()) {

            throw new BusinessException(
                    "Insufficient stock for this product. Available: "
                            + inventory.getQuantityAvailable()
            );
        }

        Cart cart = cartRepository
                .findByUserIdWithItems(user.getId())
                .orElseGet(() -> createCart(user));

        var existingItem = cart.getItems()
                .stream()
                .filter(
                        item -> item.getProduct()
                                .getId()
                                .equals(request.productId())
                )
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();

            int newQuantity =
                    item.getQuantity()
                            + request.quantity();

            if (inventory.getQuantityAvailable()
                    < newQuantity) {

                throw new BusinessException(
                        "Insufficient stock for this product. Available: "
                                + inventory.getQuantityAvailable()
                );
            }

            item.incrementQuantity(
                    request.quantity()
            );

        } else {
            Money unitPrice =
                    product.getCurrentPrice();

            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(request.quantity())
                    .unitPrice(unitPrice)
                    .build();

            cart.addItem(newItem);
        }

        cart = cartRepository.save(cart);

        log.info(
                "Added product {} to cart for user {}",
                request.productId(),
                username
        );

        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(
            String username,
            UpdateCartItemRequest request
    ) {
        User user = getUser(username);

        Cart cart = cartRepository
                .findByUserIdWithItems(user.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Cart not found for user: "
                                        + username
                        )
                );

        CartItem item = cart.getItems()
                .stream()
                .filter(
                        cartItem ->
                                cartItem.getId()
                                        .equals(request.itemId())
                )
                .findFirst()
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Item not found in cart: "
                                        + request.itemId()
                        )
                );

        Product product = item.getProduct();

        var inventory =
                inventoryService.findByProductId(
                        product.getId()
                );

        if (inventory.getQuantityAvailable()
                < request.quantity()) {

            throw new BusinessException(
                    "Insufficient stock for this product. Available: "
                            + inventory.getQuantityAvailable()
            );
        }

        item.setQuantity(request.quantity());
        cart.updateLastActivity();

        cart = cartRepository.save(cart);

        log.info(
                "Updated cart item {} for user {}",
                request.itemId(),
                username
        );

        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(
            String username,
            UUID itemId
    ) {
        User user = getUser(username);

        Cart cart = cartRepository
                .findByUserIdWithItems(user.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Cart not found for user: "
                                        + username
                        )
                );

        CartItem item = cart.getItems()
                .stream()
                .filter(
                        cartItem ->
                                cartItem.getId()
                                        .equals(itemId)
                )
                .findFirst()
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Item not found in cart: "
                                        + itemId
                        )
                );

        cart.removeItem(item);

        cart = cartRepository.save(cart);

        log.info(
                "Removed item {} from cart for user {}",
                itemId,
                username
        );

        return toResponse(cart);
    }

    @Transactional
    public void clearCart(String username) {
        User user = getUser(username);

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Cart not found for user: "
                                        + username
                        )
                );

        cart.clear();

        cartRepository.save(cart);

        log.info(
                "Cleared cart for user {}",
                username
        );
    }

    @Transactional
    public void validateCart(UUID cartId) {
        Cart cart = cartRepository
                .findByIdWithItems(cartId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Cart",
                                cartId
                        )
                );

        for (CartItem item : cart.getItems()) {

            if (!Boolean.TRUE.equals(
                    item.getProduct().getActive()
            )) {
                throw new BusinessException(
                        "Product "
                                + item.getProduct().getName()
                                + " is no longer available"
                );
            }

            var inventory =
                    inventoryService.findByProductId(
                            item.getProduct().getId()
                    );

            if (inventory.getQuantityAvailable()
                    < item.getQuantity()) {

                throw new BusinessException(
                        "Product "
                                + item.getProduct().getName()
                                + " has insufficient stock. Available: "
                                + inventory.getQuantityAvailable()
                );
            }

            Money currentPrice =
                    item.getProduct().getCurrentPrice();

            if (!item.getUnitPrice()
                    .equals(currentPrice)) {

                item.setUnitPrice(currentPrice);
            }
        }

        cartRepository.save(cart);
    }

    private User getUser(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User",
                                username
                        )
                );
    }

    private Cart createCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .build();

        cart.updateLastActivity();

        cart = cartRepository.save(cart);

        log.info(
                "Cart created for user {}",
                user.getUsername()
        );

        return cart;
    }

    private CartResponse emptyCartResponse() {
        return CartResponse.builder()
                .items(Collections.emptyList())
                .subtotal(java.math.BigDecimal.ZERO)
                .totalItems(0)
                .build();
    }

    private CartResponse toResponse(Cart cart) {
        var items = cart.getItems()
                .stream()
                .map(item -> {
                    String primaryImage =
                            item.getProduct()
                                    .getImages()
                                    .stream()
                                    .filter(
                                            image ->
                                                    Boolean.TRUE.equals(
                                                            image.getIsPrimary()
                                                    )
                                    )
                                    .findFirst()
                                    .map(
                                            image ->
                                                    image.getImageUrl()
                                    )
                                    .orElse(null);

                    return CartResponse.CartItemResponse
                            .builder()
                            .id(item.getId())
                            .productId(
                                    item.getProduct().getId()
                            )
                            .productName(
                                    item.getProduct().getName()
                            )
                            .productSku(
                                    item.getProduct()
                                            .getSku()
                                            .getCode()
                            )
                            .productImage(primaryImage)
                            .quantity(item.getQuantity())
                            .unitPrice(
                                    item.getUnitPrice()
                                            .getAmount()
                            )
                            .subtotal(
                                    item.getSubtotal()
                                            .getAmount()
                            )
                            .available(
                                    Boolean.TRUE.equals(
                                            item.getProduct()
                                                    .getActive()
                                    )
                            )
                            .build();
                })
                .collect(Collectors.toList());

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .subtotal(
                        cart.getSubtotal()
                                .getAmount()
                )
                .totalItems(
                        cart.getTotalItems()
                )
                .build();
    }
}