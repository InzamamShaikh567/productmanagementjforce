package com.jforce.productmanagmentBackend.controller;

import com.jforce.productmanagmentBackend.dto.request.ProductRequest;
import com.jforce.productmanagmentBackend.dto.response.ProductResponse;
import com.jforce.productmanagmentBackend.security.SecurityService;
import com.jforce.productmanagmentBackend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        var user = userId != null ? securityService.getCurrentUser(userId) : null;
        return ResponseEntity.ok(productService.getAllProducts(category, search, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ProductRequest request) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireAnyRole(user, "ADMIN", "SUPER_ADMIN");
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireAnyRole(user, "ADMIN", "SUPER_ADMIN");
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> enableProduct(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireAnyRole(user, "ADMIN", "SUPER_ADMIN");
        productService.setEnabled(id, true);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableProduct(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireAnyRole(user, "ADMIN", "SUPER_ADMIN");
        productService.setEnabled(id, false);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireAnyRole(user, "ADMIN", "SUPER_ADMIN");
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
