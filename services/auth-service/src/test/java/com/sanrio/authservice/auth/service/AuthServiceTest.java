package com.sanrio.authservice.auth.service;

import com.sanrio.authservice.auth.dto.LoginRequest;
import com.sanrio.authservice.auth.dto.LoginResponse;
import com.sanrio.authservice.auth.entity.Role;
import com.sanrio.authservice.security.AuthenticatedUser;
import com.sanrio.authservice.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsJwtAndUserDetails() {
        LoginRequest request = new LoginRequest("admin@campusbus.com", "Admin123!");
        AuthenticatedUser user = new AuthenticatedUser(1L, "Admin User", request.email(), "hashed", Role.ADMIN);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("admin@campusbus.com");
        assertThat(response.role()).isEqualTo("ADMIN");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void loginPropagatesBadCredentials() {
        LoginRequest request = new LoginRequest("driver@campusbus.com", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
