package com.jforce.productmanagmentBackend.service;

import com.jforce.productmanagmentBackend.dto.request.CartItemRequest;
import com.jforce.productmanagmentBackend.dto.response.CartResponse;
import com.jforce.productmanagmentBackend.dto.response.CartItemResponse;
import com.jforce.productmanagmentBackend.entity.Cart;
import com.jforce.productmanagmentBackend.entity.CartItem;
import com.jforce.productmanagmentBackend.entity.Product;
import com.jforce.productmanagmentBackend.entity.User;
import com.jforce.productmanagmentBackend.entity.*;
import com.jforce.productmanagmentBackend.exception.ResourceNotFoundException;
import com.jforce.productmanagmentBackend.repository.CartItemRepository;
import com.jforce.productmanagmentBackend.repository.CartRepository;
import com.jforce.productmanagmentBackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setCreatedAt(LocalDateTime.now());
                    return cartRepository.save(cart);
                });
    }

    public CartResponse getCart(User user) {
        Cart cart = getOrCreateCart(user);
        return CartResponse.from(cart);
    }

    public CartItemResponse addToCart(User user, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        Cart cart = getOrCreateCart(user);

        var existing = cartItemRepository.findByCartAndProductId(cart, product.getId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            return CartItemResponse.from(cartItemRepository.save(item));
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        cart.getItems().add(item);
        CartItem saved = cartItemRepository.save(item);
        return CartItemResponse.from(saved);
    }

    public CartItemResponse updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", cartItemId));
        item.setQuantity(quantity);
        return CartItemResponse.from(cartItemRepository.save(item));
    }

    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCart(cart);
    }

    public List<CartItem> getCartItems(User user) {
        Cart cart = getOrCreateCart(user);
        return cartItemRepository.findByCart(cart);
    }
}
