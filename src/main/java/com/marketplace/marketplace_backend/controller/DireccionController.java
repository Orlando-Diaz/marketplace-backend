package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.DireccionRequest;
import com.marketplace.marketplace_backend.dto.DireccionResponse;
import com.marketplace.marketplace_backend.service.DireccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
@Tag(name = "Direcciones", description = "Gestión de direcciones de envío del usuario autenticado")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @Operation(summary = "Agregar dirección", description = "Registra una nueva dirección de envío para el usuario autenticado.")
    @PostMapping
    public DireccionResponse crear(@RequestBody DireccionRequest request, Authentication authentication) {
        return direccionService.crear(authentication.getName(), request);
    }

    @Operation(summary = "Mis direcciones", description = "Lista las direcciones guardadas del usuario autenticado.")
    @GetMapping
    public List<DireccionResponse> misDirecciones(Authentication authentication) {
        return direccionService.listarMisDirecciones(authentication.getName());
    }
}