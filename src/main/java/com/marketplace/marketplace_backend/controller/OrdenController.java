package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.CheckoutRequest;
import com.marketplace.marketplace_backend.dto.OrdenResponse;
import com.marketplace.marketplace_backend.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Órdenes", description = "Checkout y consulta de órdenes de compra")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @Operation(summary = "Checkout", description = "Convierte el carrito actual en una orden: valida stock, congela precios, simula el pago y vacía el carrito.")
    @PostMapping("/checkout")
    public OrdenResponse checkout(@RequestBody CheckoutRequest request, Authentication authentication) {
        return ordenService.checkout(authentication.getName(), request);
    }

    @Operation(summary = "Mis órdenes", description = "Lista el historial de compras del usuario autenticado.")
    @GetMapping
    public List<OrdenResponse> misOrdenes(Authentication authentication) {
        return ordenService.listarMisOrdenes(authentication.getName());
    }
}