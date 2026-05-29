package com.jforce.productmanagmentBackend.dto.response;

import com.jforce.productmanagmentBackend.entity.Inventory;
import lombok.Data;

@Data
public class InventoryResponse {
    private Long productId;
    private String productName;
    private int quantity;

    public static InventoryResponse from(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.setProductId(inventory.getProduct().getId());
        response.setProductName(inventory.getProduct().getName());
        response.setQuantity(inventory.getQuantity());
        return response;
    }
}
