package com.jforce.productmanagmentBackend.dto.response;

import com.jforce.productmanagmentBackend.entity.OrderItem;
import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Double priceAtPurchase;
    private int quantity;
    private String imageUrl;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setPriceAtPurchase(item.getPriceAtPurchase());
        response.setQuantity(item.getQuantity());
        response.setImageUrl(item.getProduct().getImageUrl());
        return response;
    }
}
