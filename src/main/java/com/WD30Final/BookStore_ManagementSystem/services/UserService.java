package com.WD30Final.BookStore_ManagementSystem.services;


import com.WD30Final.BookStore_ManagementSystem.entity.Admin;
import com.WD30Final.BookStore_ManagementSystem.entity.User;
import com.WD30Final.BookStore_ManagementSystem.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String role = (user instanceof Admin) ? "ROLE_ADMIN" : "ROLE_CUSTOMER";

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(() -> role)
        );
    }

    // Create / Register Operation
    public User saveUser(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }

    // Read Operations
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // Get user by ID (needed for admin view details)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Update Operation
    public User updateProfile(Long id, String name, String email) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    // Delete Operation
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}