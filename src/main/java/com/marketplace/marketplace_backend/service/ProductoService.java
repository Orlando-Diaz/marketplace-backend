package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.ProductoRequest;
import com.marketplace.marketplace_backend.dto.ProductoResponse;
import com.marketplace.marketplace_backend.entity.*;
import com.marketplace.marketplace_backend.exception.RecursoNoEncontradoException;
import com.marketplace.marketplace_backend.repository.CategoriaRepository;
import com.marketplace.marketplace_backend.repository.ProductoRepository;
import com.marketplace.marketplace_backend.repository.ResenaRepository;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ResenaRepository resenaRepository;


    public ProductoService(ProductoRepository productoRepository, UsuarioRepository usuarioRepository,
                           CategoriaRepository categoriaRepository, ResenaRepository resenaRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.resenaRepository = resenaRepository;
    }

    public ProductoResponse crear(String emailUsuario, ProductoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

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

    public Page<ProductoResponse> listarTodos(int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("fechaPublicacion").descending());
        return productoRepository.findByEstado(EstadoProducto.DISPONIBLE, pageable)
                .map(this::toResponse);
    }

    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        return toResponse(producto);
    }

    public List<ProductoResponse> listarPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return productoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductoResponse toResponse(Producto p) {
        List<Resena> resenas = resenaRepository.findByProductoId(p.getId());

        Double promedio = resenas.isEmpty() ? null :
                resenas.stream().mapToInt(Resena::getCalificacion).average().orElse(0);

        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock(),
                p.getEstado().name(), p.getFechaPublicacion(),
                p.getUsuario().getId(), p.getUsuario().getNombre(),
                p.getCategoria().getId(), p.getCategoria().getNombre(),
                promedio, resenas.size()
        );
    }
}