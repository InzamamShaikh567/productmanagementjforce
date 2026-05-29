package com.jforce.productmanagmentBackend.controller;

import com.jforce.productmanagmentBackend.dto.response.OrderResponse;
import com.jforce.productmanagmentBackend.entity.OrderStatus;
import com.jforce.productmanagmentBackend.security.SecurityService;
import com.jforce.productmanagmentBackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityService securityService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long addressId) {
        var user = securityService.getCurrentUser(userId);
        return ResponseEntity.ok(orderService.checkout(user, addressId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@RequestHeader("X-User-Id") Long userId) {
        var user = securityService.getCurrentUser(userId);
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestHeader("X-User-Id") Long userId) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireRole(user, "SUPER_ADMIN");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestParam String status) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireAnyRole(user, "ADMIN", "SUPER_ADMIN");
        return ResponseEntity.ok(orderService.updateStatus(id, OrderStatus.valueOf(status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        var user = securityService.getCurrentUser(userId);
        securityService.requireRole(user, "SUPER_ADMIN");
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
