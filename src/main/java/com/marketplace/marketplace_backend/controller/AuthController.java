package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.AuthResponse;
import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.RegistroRequest;
import com.marketplace.marketplace_backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public AuthResponse registrar(@RequestBody RegistroRequest request) {
        return authService.registrar(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}