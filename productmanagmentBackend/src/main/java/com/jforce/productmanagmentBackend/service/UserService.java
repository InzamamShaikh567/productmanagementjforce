package com.jforce.productmanagmentBackend.service;

import com.jforce.productmanagmentBackend.dto.request.UserRoleUpdateRequest;
import com.jforce.productmanagmentBackend.dto.response.UserResponse;
import com.jforce.productmanagmentBackend.entity.Role;
import com.jforce.productmanagmentBackend.entity.User;
import com.jforce.productmanagmentBackend.exception.ResourceNotFoundException;
import com.jforce.productmanagmentBackend.repository.RoleRepository;
import com.jforce.productmanagmentBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return UserResponse.from(user);
    }

    public UserResponse updateUserRoles(Long userId, UserRoleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Set<Role> roles = request.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", roleId)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }
}
