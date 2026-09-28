package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.CategoriaRequest;
import com.marketplace.marketplace_backend.dto.CategoriaResponse;
import com.marketplace.marketplace_backend.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Gestión de categorías y subcategorías de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Listar todas las categorías", description = "Devuelve todas las categorías, incluyendo raíz y subcategorías, en una sola lista plana.")
    @GetMapping
    public List<CategoriaResponse> listarTodas() {
        return categoriaService.listarTodas();
    }

    @Operation(summary = "Listar categorías raíz", description = "Devuelve solo las categorías principales (sin categoría padre), útil para construir el menú de navegación del catálogo.")
    @GetMapping("/raiz")
    public List<CategoriaResponse> listarRaiz() {
        return categoriaService.listarRaiz();
    }

    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría. Solo administradores.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest request) {
        return categoriaService.crear(request);
    }

    @Operation(summary = "Editar categoría", description = "Actualiza nombre, descripción y categoría padre. Solo administradores.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CategoriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return categoriaService.actualizar(id, request);
    }

    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría sin productos ni subcategorías. Solo administradores.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
    }
}