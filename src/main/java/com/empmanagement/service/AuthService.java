package com.empmanagement.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.empmanagement.config.JwtUtil;
import com.empmanagement.dto.AuthResponse;
import com.empmanagement.dto.LoginRequest;
import com.empmanagement.dto.RegisterRequest;
import com.empmanagement.entity.User;
import com.empmanagement.exception.EmailAlreadyExistsException;
import com.empmanagement.repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        // ✅ Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered: " + request.getEmail());
        }

        // ✅ Strict role validation (NO auto default as per your requirement)
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        // ✅ Build user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        // ✅ Generate JWT
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        // ✅ Authenticate user (handles wrong password / user not found)
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Invalid email or password");
        }

        // ✅ Fetch user safely
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // ✅ Generate JWT
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}