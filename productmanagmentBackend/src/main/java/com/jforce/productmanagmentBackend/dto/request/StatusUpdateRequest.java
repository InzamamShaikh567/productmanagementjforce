package com.jforce.productmanagmentBackend.dto.request;

import com.jforce.productmanagmentBackend.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}
