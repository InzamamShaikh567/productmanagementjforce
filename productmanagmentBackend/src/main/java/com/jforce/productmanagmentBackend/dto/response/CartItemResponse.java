package com.jforce.productmanagmentBackend.dto.response;

import com.jforce.productmanagmentBackend.entity.CartItem;
import lombok.Data;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Double productPrice;
    private String imageUrl;
    private int quantity;

    public static CartItemResponse from(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setProductPrice(item.getProduct().getPrice());
        response.setImageUrl(item.getProduct().getImageUrl());
        response.setQuantity(item.getQuantity());
        return response;
    }
}
