package com.dmf.marketplace.compartilhado.seguranca;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserAuthenticationControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserAuthenticationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/auth");
        when(request.getMethod()).thenReturn("POST");
    }

    @Test
    void shouldAuthenticateSuccessfully() throws JOSEException {
        // Arrange
        LoginInputDto loginInfo = new LoginInputDto();
        loginInfo.setEmail("user@example.com");
        loginInfo.setPassword("password");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@example.com");
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenManager.generateToken(auth)).thenReturn("jwt-token");

        // Act
        ResponseEntity<String> response = controller.authenticate(loginInfo, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenManager).generateToken(auth);
    }

    @Test
    void shouldReturnBadRequestWhenAuthenticationFails() throws JOSEException {
        // Arrange
        LoginInputDto loginInfo = new LoginInputDto();
        loginInfo.setEmail("user@example.com");
        loginInfo.setPassword("wrong-password");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        ResponseEntity<String> response = controller.authenticate(loginInfo, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenManager, never()).generateToken(any());
    }
}