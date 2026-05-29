package com.jforce.productmanagmentBackend.controller;

import com.jforce.productmanagmentBackend.dto.request.InventoryUpdateRequest;
import com.jforce.productmanagmentBackend.dto.request.UserRoleUpdateRequest;
import com.jforce.productmanagmentBackend.dto.response.InventoryResponse;
import com.jforce.productmanagmentBackend.dto.response.UserResponse;
import com.jforce.productmanagmentBackend.security.SecurityService;
import com.jforce.productmanagmentBackend.service.InventoryService;
import com.jforce.productmanagmentBackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final InventoryService inventoryService;
    private final SecurityService securityService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestHeader("X-User-Id") Long userId) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireRole(user, "SUPER_ADMIN");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireRole(user, "SUPER_ADMIN");
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateRequest request) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireRole(user, "SUPER_ADMIN");
        return ResponseEntity.ok(userService.updateUserRoles(id, request));
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryResponse>> getAllInventory(@RequestHeader("X-User-Id") Long userId) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireAnyRole(user, "ADMIN", "SUPER_ADMIN");
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @PutMapping("/inventory/{productId}")
    public ResponseEntity<Void> updateInventory(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody InventoryUpdateRequest request) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireAnyRole(user, "ADMIN", "SUPER_ADMIN");
        inventoryService.updateInventory(productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }
}
