package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.CarritoResponse;
import com.marketplace.marketplace_backend.dto.ItemCarritoRequest;
import com.marketplace.marketplace_backend.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Gestión del carrito de compras del usuario autenticado")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @Operation(summary = "Ver mi carrito", description = "Devuelve el carrito del usuario autenticado, creándolo si aún no existe.")
    @GetMapping
    public CarritoResponse obtenerCarrito(Authentication authentication) {
        return carritoService.obtenerCarrito(authentication.getName());
    }

    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto, o suma cantidad si ya está en el carrito. Valida stock disponible.")
    @PostMapping("/items")
    public CarritoResponse agregarProducto(@Valid @RequestBody ItemCarritoRequest request, Authentication authentication) {
        return carritoService.agregarProducto(authentication.getName(), request);
    }

    @Operation(summary = "Quitar producto del carrito", description = "Elimina un item específico del carrito por su ID.")
    @DeleteMapping("/items/{itemId}")
    public CarritoResponse quitarProducto(@PathVariable Long itemId, Authentication authentication) {
        return carritoService.quitarProducto(authentication.getName(), itemId);
    }
}