package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.CategoriaRequest;
import com.marketplace.marketplace_backend.dto.CategoriaResponse;
import com.marketplace.marketplace_backend.entity.Categoria;
import com.marketplace.marketplace_backend.exception.OperacionNoPermitidaException;
import com.marketplace.marketplace_backend.exception.RecursoNoEncontradoException;
import com.marketplace.marketplace_backend.repository.CategoriaRepository;
import com.marketplace.marketplace_backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setCategoriaPadre(resolverPadre(null, request.getCategoriaPadreId()));

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

    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setCategoriaPadre(resolverPadre(id, request.getCategoriaPadreId()));

        categoriaRepository.save(categoria);
        return toResponse(categoria);
    }

    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        if (categoriaRepository.existsByCategoriaPadreId(id)) {
            throw new OperacionNoPermitidaException("No se puede eliminar: la categoría tiene subcategorías");
        }
        if (productoRepository.existsByCategoriaId(id)) {
            throw new OperacionNoPermitidaException("No se puede eliminar: hay productos publicados en esta categoría");
        }

        categoriaRepository.delete(categoria);
    }

    private Categoria resolverPadre(Long idActual, Long padreId) {
        if (padreId == null) {
            return null;
        }
        if (padreId.equals(idActual)) {
            throw new OperacionNoPermitidaException("Una categoría no puede ser su propia categoría padre");
        }

        Categoria padre = categoriaRepository.findById(padreId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría padre no encontrada"));

        if (padre.getCategoriaPadre() != null) {
            throw new OperacionNoPermitidaException("Solo se permiten dos niveles: el padre debe ser una categoría principal");
        }
        if (idActual != null && categoriaRepository.existsByCategoriaPadreId(idActual)) {
            throw new OperacionNoPermitidaException("Esta categoría tiene subcategorías, no puede convertirse en subcategoría");
        }
        return padre;
    }

    private CategoriaResponse toResponse(Categoria c) {
        Long padreId = c.getCategoriaPadre() != null ? c.getCategoriaPadre().getId() : null;
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion(), padreId);
    }
}