package com.jforce.productmanagmentBackend.dto.response;

import com.jforce.productmanagmentBackend.entity.Order;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long id;
    private Double totalAmount;
    private String orderDate;
    private String status;
    private String username;
    private AddressResponse address;
    private List<OrderItemResponse> items;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderDate(order.getOrderDate().toString());
        response.setStatus(order.getStatus().name());
        response.setUsername(order.getUser().getUsername());
        if (order.getAddress() != null) {
            response.setAddress(AddressResponse.from(order.getAddress()));
        }
        response.setItems(order.getItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList()));
        return response;
    }
}
