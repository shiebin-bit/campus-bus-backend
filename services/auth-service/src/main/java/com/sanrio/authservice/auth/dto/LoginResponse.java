package com.sanrio.authservice.auth.dto;

public record LoginResponse(String accessToken, String tokenType, Long userId, String name, String email, String role) {
}
