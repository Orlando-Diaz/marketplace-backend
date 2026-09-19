package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.CarritoResponse;
import com.marketplace.marketplace_backend.dto.ItemCarritoRequest;
import com.marketplace.marketplace_backend.dto.ItemCarritoResponse;
import com.marketplace.marketplace_backend.entity.Carrito;
import com.marketplace.marketplace_backend.entity.ItemCarrito;
import com.marketplace.marketplace_backend.entity.Producto;
import com.marketplace.marketplace_backend.entity.Usuario;
import com.marketplace.marketplace_backend.repository.CarritoRepository;
import com.marketplace.marketplace_backend.repository.ItemCarritoRepository;
import com.marketplace.marketplace_backend.repository.ProductoRepository;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoService(CarritoRepository carritoRepository, ItemCarritoRepository itemCarritoRepository,
                          ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public CarritoResponse obtenerCarrito(String emailUsuario) {
        Carrito carrito = obtenerOCrearCarrito(emailUsuario);
        return toResponse(carrito);
    }

    public CarritoResponse agregarProducto(String emailUsuario, ItemCarritoRequest request) {
        Carrito carrito = obtenerOCrearCarrito(emailUsuario);

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (request.getCantidad() > producto.getStock()) {
            throw new RuntimeException("Cantidad solicitada supera el stock disponible");
        }

        ItemCarrito item = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
                .orElse(null);

        if (item != null) {
            item.setCantidad(item.getCantidad() + request.getCantidad());
        } else {
            item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(request.getCantidad());
        }

        itemCarritoRepository.save(item);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return toResponse(actualizado);
    }

    public CarritoResponse quitarProducto(String emailUsuario, Long itemId) {
        Carrito carrito = obtenerOCrearCarrito(emailUsuario);

        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new RuntimeException("Este item no pertenece a tu carrito");
        }

        itemCarritoRepository.delete(item);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return toResponse(actualizado);
    }

    private Carrito obtenerOCrearCarrito(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return carritoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    return carritoRepository.save(nuevo);
                });
    }

    private CarritoResponse toResponse(Carrito carrito) {
        List<ItemCarritoResponse> items = carrito.getItems().stream()
                .map(i -> {
                    BigDecimal subtotal = i.getProducto().getPrecio().multiply(BigDecimal.valueOf(i.getCantidad()));
                    return new ItemCarritoResponse(
                            i.getId(), i.getProducto().getId(), i.getProducto().getNombre(),
                            i.getProducto().getPrecio(), i.getCantidad(), subtotal
                    );
                })
                .toList();

        BigDecimal total = items.stream()
                .map(ItemCarritoResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarritoResponse(carrito.getId(), items, total);
    }
}