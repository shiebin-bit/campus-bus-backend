package com.sanrio.authservice.auth.controller;

import com.sanrio.authservice.auth.dto.LoginRequest;
import com.sanrio.authservice.auth.dto.LoginResponse;
import com.sanrio.authservice.auth.service.AuthService;
import com.sanrio.authservice.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Login successful", authService.login(request)));
    }
}
