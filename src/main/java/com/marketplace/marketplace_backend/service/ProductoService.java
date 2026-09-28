package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.FiltroProductos;
import com.marketplace.marketplace_backend.dto.ProductoRequest;
import com.marketplace.marketplace_backend.dto.ProductoResponse;
import com.marketplace.marketplace_backend.entity.*;
import com.marketplace.marketplace_backend.exception.AccesoNoAutorizadoException;
import com.marketplace.marketplace_backend.exception.RecursoNoEncontradoException;
import com.marketplace.marketplace_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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
        List<String> urls = request.getImagenes() == null ? List.of() : request.getImagenes();

        agregarImagenes(producto, request.getImagenes());

        productoRepository.save(producto);
        return toResponse(producto);
    }

    public Page<ProductoResponse> listarTodos(FiltroProductos filtro, int pagina, int tamano) {
        List<Specification<Producto>> condiciones = new ArrayList<>();
        condiciones.add(ProductoSpecs.conEstado(EstadoProducto.DISPONIBLE));

        if (filtro.q() != null && !filtro.q().isBlank()) {
            condiciones.add(ProductoSpecs.nombreContiene(filtro.q()));
        }
        if (filtro.categoriaId() != null) {
            condiciones.add(ProductoSpecs.enCategorias(idsCategoriaConSubcategorias(filtro.categoriaId())));
        }
        if (filtro.precioMin() != null) {
            condiciones.add(ProductoSpecs.precioMinimo(filtro.precioMin()));
        }
        if (filtro.precioMax() != null) {
            condiciones.add(ProductoSpecs.precioMaximo(filtro.precioMax()));
        }
        if (Boolean.TRUE.equals(filtro.soloDisponibles())) {
            condiciones.add(ProductoSpecs.conStock());
        }

        Pageable pageable = PageRequest.of(pagina, tamano, ordenar(filtro.orden()));
        return productoRepository.findAll(Specification.allOf(condiciones), pageable)
                .map(this::toResponse);
    }

    private Sort ordenar(String orden) {
        return switch (orden == null ? "recientes" : orden) {
            case "precio_asc" -> Sort.by("precio").ascending();
            case "precio_desc" -> Sort.by("precio").descending();
            default -> Sort.by("fechaPublicacion").descending();
        };
    }

    private List<Long> idsCategoriaConSubcategorias(Long categoriaId) {
        List<Long> ids = new ArrayList<>();
        ids.add(categoriaId);
        categoriaRepository.findByCategoriaPadreId(categoriaId)
                .forEach(sub -> ids.add(sub.getId()));
        return ids;
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

        List<String> imagenes = p.getImagenes().stream()
                .sorted(Comparator.comparing(ImagenProducto::getOrden))
                .map(ImagenProducto::getUrl)
                .toList();

        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock(),
                p.getEstado().name(), p.getFechaPublicacion(),
                p.getUsuario().getId(), p.getUsuario().getNombre(),
                p.getCategoria().getId(), p.getCategoria().getNombre(),
                promedio, resenas.size(),
                imagenes
        );
    }

    @Transactional
    public ProductoResponse actualizar(String emailUsuario, Long id, ProductoRequest request) {
        Producto producto = obtenerPropio(emailUsuario, id);

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);

        producto.getImagenes().clear();
        agregarImagenes(producto, request.getImagenes());

        productoRepository.save(producto);
        return toResponse(producto);
    }

    public ProductoResponse cambiarEstado(String emailUsuario, Long id, boolean activo) {
        Producto producto = obtenerPropio(emailUsuario, id);
        producto.setEstado(activo ? EstadoProducto.DISPONIBLE : EstadoProducto.INACTIVO);
        productoRepository.save(producto);
        return toResponse(producto);
    }

    private Producto obtenerPropio(String emailUsuario, Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        if (!producto.getUsuario().getEmail().equals(emailUsuario)) {
            throw new AccesoNoAutorizadoException("Solo puedes modificar tus propios productos");
        }
        return producto;
    }

    private void agregarImagenes(Producto producto, List<String> urls) {
        if (urls == null) return;
        for (int i = 0; i < urls.size(); i++) {
            ImagenProducto imagen = new ImagenProducto();
            imagen.setProducto(producto);
            imagen.setUrl(urls.get(i));
            imagen.setOrden(i);
            producto.getImagenes().add(imagen);
        }
    }
}