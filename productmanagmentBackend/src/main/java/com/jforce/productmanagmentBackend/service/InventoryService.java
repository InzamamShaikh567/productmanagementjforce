package com.jforce.productmanagmentBackend.service;

import com.jforce.productmanagmentBackend.entity.CartItem;
import com.jforce.productmanagmentBackend.entity.Inventory;
import com.jforce.productmanagmentBackend.exception.InsufficientInventoryException;
import com.jforce.productmanagmentBackend.exception.ResourceNotFoundException;
import com.jforce.productmanagmentBackend.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.jforce.productmanagmentBackend.dto.response.InventoryResponse;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Inventory getInventory(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", productId));
    }

    public void updateInventory(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", productId));
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    public void validateAndDeduct(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            Inventory inventory = inventoryRepository.findByProduct(item.getProduct())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", item.getProduct().getId()));

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        inventory.getQuantity()
                );
            }
        }

        for (CartItem item : cartItems) {
            Inventory inventory = inventoryRepository.findByProduct(item.getProduct()).get();
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }
}
