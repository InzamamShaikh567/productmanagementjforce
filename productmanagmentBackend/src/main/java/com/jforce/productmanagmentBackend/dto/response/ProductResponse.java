package com.jforce.productmanagmentBackend.dto.response;

import com.jforce.productmanagmentBackend.entity.Product;
import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String categoryName;
    private Long categoryId;
    private String imageUrl;
    private boolean enabled;
    private Integer availableQuantity;

    public static ProductResponse from(Product product) {
        return from(product, null);
    }

    public static ProductResponse from(Product product, Integer availableQuantity) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCategoryName(product.getCategory().getName());
        response.setCategoryId(product.getCategory().getId());
        response.setImageUrl(product.getImageUrl());
        response.setEnabled(product.isEnabled());
        response.setAvailableQuantity(availableQuantity);
        return response;
    }
}
