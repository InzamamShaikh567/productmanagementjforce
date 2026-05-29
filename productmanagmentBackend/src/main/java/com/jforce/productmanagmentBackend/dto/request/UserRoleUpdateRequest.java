package com.jforce.productmanagmentBackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class UserRoleUpdateRequest {
    @NotNull(message = "Role IDs are required")
    private Set<Long> roleIds;
}
