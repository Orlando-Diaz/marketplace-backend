package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.FiltroProductos;
import com.marketplace.marketplace_backend.dto.ProductoRequest;
import com.marketplace.marketplace_backend.dto.ProductoResponse;
import com.marketplace.marketplace_backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
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

    @Operation(summary = "Listar catálogo",
            description = "Productos disponibles, paginados y con filtros opcionales: texto (q), categoría (incluye subcategorías), rango de precio, solo con stock y orden (recientes, precio_asc, precio_desc).")
    @GetMapping
    public Page<ProductoResponse> listarTodos(
            @ParameterObject FiltroProductos filtro,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano) {
        return productoService.listarTodos(filtro, pagina, tamano);
    }

    @Operation(summary = "Ver detalle de un producto", description = "Devuelve la información completa de un producto por su ID.")
    @GetMapping("/{id}")
    public ProductoResponse obtenerPorId(@Valid @PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    @Operation(summary = "Publicar un producto", description = "Crea un nuevo producto asociado al usuario autenticado.")
    @PostMapping
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request, Authentication authentication) {
        String email = authentication.getName();
        return productoService.crear(email, request);
    }

    @Operation(summary = "Mis productos", description = "Lista los productos publicados por el usuario autenticado.")
    @GetMapping("/mis-productos")
    public List<ProductoResponse> misProductos(Authentication authentication) {
        String email = authentication.getName();
        return productoService.listarPorUsuario(email);
    }

    @Operation(summary = "Editar producto", description = "Actualiza un producto propio, incluyendo sus imágenes.")
    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id,
                                       @Valid @RequestBody ProductoRequest request,
                                       Authentication authentication) {
        return productoService.actualizar(authentication.getName(), id, request);
    }

    @Operation(summary = "Pausar o activar producto", description = "activo=false lo saca del catálogo sin borrarlo; activo=true lo vuelve a publicar.")
    @PatchMapping("/{id}/estado")
    public ProductoResponse cambiarEstado(@PathVariable Long id,
                                          @RequestParam boolean activo,
                                          Authentication authentication) {
        return productoService.cambiarEstado(authentication.getName(), id, activo);
    }
}