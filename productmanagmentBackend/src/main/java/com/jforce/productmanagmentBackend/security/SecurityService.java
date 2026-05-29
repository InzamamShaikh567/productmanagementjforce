package com.jforce.productmanagmentBackend.security;

import com.jforce.productmanagmentBackend.entity.User;
import com.jforce.productmanagmentBackend.exception.UnauthorizedException;
import com.jforce.productmanagmentBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public User getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    public void requireRole(User user, String roleName) {
        boolean hasRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
        if (!hasRole) {
            throw new UnauthorizedException("Access denied. Required role: " + roleName);
        }
    }

    public void requireAnyRole(User user, String... roleNames) {
        boolean hasAnyRole = user.getRoles().stream()
                .anyMatch(role -> {
                    for (String roleName : roleNames) {
                        if (role.getName().equals(roleName)) return true;
                    }
                    return false;
                });
        if (!hasAnyRole) {
            throw new UnauthorizedException("Access denied. Required one of roles: " + String.join(", ", roleNames));
        }
    }
}
