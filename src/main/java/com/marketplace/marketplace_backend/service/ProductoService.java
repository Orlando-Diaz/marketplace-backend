package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.ProductoRequest;
import com.marketplace.marketplace_backend.dto.ProductoResponse;
import com.marketplace.marketplace_backend.entity.*;
import com.marketplace.marketplace_backend.repository.CategoriaRepository;
import com.marketplace.marketplace_backend.repository.ProductoRepository;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, UsuarioRepository usuarioRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public ProductoResponse crear(String emailUsuario, ProductoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Producto producto = new Producto();
        producto.setUsuario(usuario);
        producto.setCategoria(categoria);
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setEstado(EstadoProducto.DISPONIBLE);

        productoRepository.save(producto);
        return toResponse(producto);
    }

    public List<ProductoResponse> listarTodos() {
        return productoRepository.findByEstado(EstadoProducto.DISPONIBLE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return toResponse(producto);
    }

    public List<ProductoResponse> listarPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return productoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock(),
                p.getEstado().name(), p.getFechaPublicacion(),
                p.getUsuario().getId(), p.getUsuario().getNombre(),
                p.getCategoria().getId(), p.getCategoria().getNombre()
        );
    }
}