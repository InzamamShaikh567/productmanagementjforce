package com.jforce.productmanagmentBackend.service;

import com.jforce.productmanagmentBackend.dto.request.LoginRequest;
import com.jforce.productmanagmentBackend.dto.request.RegisterRequest;
import com.jforce.productmanagmentBackend.dto.response.UserResponse;
import com.jforce.productmanagmentBackend.entity.Role;
import com.jforce.productmanagmentBackend.entity.User;
import com.jforce.productmanagmentBackend.exception.DuplicateResourceException;
import com.jforce.productmanagmentBackend.exception.ResourceNotFoundException;
import com.jforce.productmanagmentBackend.repository.RoleRepository;
import com.jforce.productmanagmentBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'USER' not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(userRole);

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        return UserResponse.from(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
