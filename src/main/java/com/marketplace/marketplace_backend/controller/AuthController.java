package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.AuthResponse;
import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.RegistroRequest;
import com.marketplace.marketplace_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Registro" , description = "Crear usuario y loguearse")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Crear" , description = "Crear un usuario en la app")
    @PostMapping("/registro")
    public AuthResponse registrar(@RequestBody RegistroRequest request) {
        return authService.registrar(request);
    }

    @Operation(summary = "Login" , description = "Logearse en la app ")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}