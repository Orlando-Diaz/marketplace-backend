package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.CategoriaRequest;
import com.marketplace.marketplace_backend.dto.CategoriaResponse;
import com.marketplace.marketplace_backend.entity.Categoria;
import com.marketplace.marketplace_backend.exception.RecursoNoEncontradoException;
import com.marketplace.marketplace_backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        if (request.getCategoriaPadreId() != null) {
            Categoria padre = categoriaRepository.findById(request.getCategoriaPadreId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría padre no encontrada"));
            categoria.setCategoriaPadre(padre);
        }

        categoriaRepository.save(categoria);
        return toResponse(categoria);
    }

    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CategoriaResponse> listarRaiz() {
        return categoriaRepository.findByCategoriaPadreIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoriaResponse toResponse(Categoria c) {
        Long padreId = c.getCategoriaPadre() != null ? c.getCategoriaPadre().getId() : null;
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion(), padreId);
    }
}