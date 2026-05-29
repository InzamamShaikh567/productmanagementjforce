package com.jforce.productmanagmentBackend.dto.response;

import com.jforce.productmanagmentBackend.entity.Cart;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;

    public static CartResponse from(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setItems(cart.getItems().stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList()));
        return response;
    }
}
