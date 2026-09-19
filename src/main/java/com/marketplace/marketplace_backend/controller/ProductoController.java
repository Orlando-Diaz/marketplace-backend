package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.ProductoRequest;
import com.marketplace.marketplace_backend.dto.ProductoResponse;
import com.marketplace.marketplace_backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Publicación y consulta de productos del marketplace")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Listar catálogo", description = "Devuelve todos los productos disponibles. No requiere autenticación.")
    @GetMapping
    public List<ProductoResponse> listarTodos() {
        return productoService.listarTodos();
    }

    @Operation(summary = "Ver detalle de un producto", description = "Devuelve la información completa de un producto por su ID.")
    @GetMapping("/{id}")
    public ProductoResponse obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    @Operation(summary = "Publicar un producto", description = "Crea un nuevo producto asociado al usuario autenticado.")
    @PostMapping
    public ProductoResponse crear(@RequestBody ProductoRequest request, Authentication authentication) {
        String email = authentication.getName();
        return productoService.crear(email, request);
    }

    @Operation(summary = "Mis productos", description = "Lista los productos publicados por el usuario autenticado.")
    @GetMapping("/mis-productos")
    public List<ProductoResponse> misProductos(Authentication authentication) {
        String email = authentication.getName();
        return productoService.listarPorUsuario(email);
    }
}