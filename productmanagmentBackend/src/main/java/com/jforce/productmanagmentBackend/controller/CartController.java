package com.jforce.productmanagmentBackend.controller;

import com.jforce.productmanagmentBackend.dto.request.CartItemRequest;
import com.jforce.productmanagmentBackend.dto.response.CartItemResponse;
import com.jforce.productmanagmentBackend.dto.response.CartResponse;
import com.jforce.productmanagmentBackend.security.SecurityService;
import com.jforce.productmanagmentBackend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") Long userId) {
        var user = securityService.getCurrentUser(userId);
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CartItemRequest request) {
        var user = securityService.getCurrentUser(userId);
        return ResponseEntity.ok(cartService.addToCart(user, request));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @PathVariable Long id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(id, quantity));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        var user = securityService.getCurrentUser(userId);
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
