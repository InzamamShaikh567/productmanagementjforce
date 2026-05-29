package com.jforce.productmanagmentBackend.controller;

import com.jforce.productmanagmentBackend.dto.request.AddressRequest;
import com.jforce.productmanagmentBackend.dto.response.AddressResponse;
import com.jforce.productmanagmentBackend.security.SecurityService;
import com.jforce.productmanagmentBackend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(@RequestHeader("X-User-Id") Long userId) {
        var user = securityService.getCurrentUser(userId);
        return ResponseEntity.ok(addressService.getAddresses(user));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddressRequest request) {
        var user = securityService.getCurrentUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        var user = securityService.getCurrentUser(userId);
        return ResponseEntity.ok(addressService.updateAddress(id, user, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        var user = securityService.getCurrentUser(userId);
        addressService.deleteAddress(id, user);
        return ResponseEntity.noContent().build();
    }
}
