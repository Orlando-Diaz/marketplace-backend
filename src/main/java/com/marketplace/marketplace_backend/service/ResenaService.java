package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.ResenaRequest;
import com.marketplace.marketplace_backend.dto.ResenaResponse;
import com.marketplace.marketplace_backend.entity.Producto;
import com.marketplace.marketplace_backend.entity.Resena;
import com.marketplace.marketplace_backend.entity.Usuario;
import com.marketplace.marketplace_backend.repository.ItemOrdenRepository;
import com.marketplace.marketplace_backend.repository.ProductoRepository;
import com.marketplace.marketplace_backend.repository.ResenaRepository;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemOrdenRepository itemOrdenRepository;

    public ResenaService(ResenaRepository resenaRepository, ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository, ItemOrdenRepository itemOrdenRepository) {
        this.resenaRepository = resenaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemOrdenRepository = itemOrdenRepository;
    }

    public ResenaResponse crear(String emailUsuario, ResenaRequest request) {
        if (request.getCalificacion() == null || request.getCalificacion() < 1 || request.getCalificacion() > 5) {
            throw new RuntimeException("La calificación debe estar entre 1 y 5");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(request.getProductoId())

                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        boolean compro = itemOrdenRepository.existsByOrdenCompradorIdAndProductoId(usuario.getId(), producto.getId());
        if (!compro) {
            throw new RuntimeException("Solo puedes reseñar productos que hayas comprado");
        }

        boolean yaReseno = resenaRepository.existsByProductoIdAndUsuarioId(producto.getId(), usuario.getId());
        if (yaReseno) {
            throw new RuntimeException("Ya reseñaste este producto");
        }

        Resena resena = new Resena();
        resena.setProducto(producto);
        resena.setUsuario(usuario);
        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());

        resenaRepository.save(resena);
        return toResponse(resena);
    }

    public List<ResenaResponse> listarPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ResenaResponse toResponse(Resena r) {
        return new ResenaResponse(r.getId(), r.getUsuario().getId(), r.getUsuario().getNombre(),
                r.getCalificacion(), r.getComentario(), r.getFecha());
    }
}