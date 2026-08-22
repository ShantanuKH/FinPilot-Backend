package com.shantanu.FinPilot.auth.controller;

import com.shantanu.FinPilot.auth.dto.AuthResponse;
import com.shantanu.FinPilot.auth.dto.LoginRequest;
import com.shantanu.FinPilot.auth.dto.RegisterRequest;
import com.shantanu.FinPilot.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest registerRequest
    ) {

        AuthResponse response =
                authService.register(registerRequest);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest
    ) {

        AuthResponse response =
                authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }
}