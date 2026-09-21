package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.ResenaRequest;
import com.marketplace.marketplace_backend.dto.ResenaResponse;
import com.marketplace.marketplace_backend.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@Tag(name = "Reseñas", description = "Calificaciones y comentarios de productos comprados")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @Operation(summary = "Reseñar un producto", description = "Crea una reseña. Solo permitido si el usuario compró el producto y no lo ha reseñado antes.")
    @PostMapping
    public ResenaResponse crear(@RequestBody ResenaRequest request, Authentication authentication) {
        return resenaService.crear(authentication.getName(), request);
    }

    @Operation(summary = "Ver reseñas de un producto", description = "Lista todas las reseñas de un producto específico. No requiere autenticación.")
    @GetMapping("/producto/{productoId}")
    public List<ResenaResponse> listarPorProducto(@PathVariable Long productoId) {
        return resenaService.listarPorProducto(productoId);
    }
}