package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.*;
import com.marketplace.marketplace_backend.entity.*;
import com.marketplace.marketplace_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrdenService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;

    public OrdenService(CarritoRepository carritoRepository, ItemCarritoRepository itemCarritoRepository,
                        DireccionRepository direccionRepository, UsuarioRepository usuarioRepository,
                        OrdenRepository ordenRepository, ProductoRepository productoRepository,
                        PagoRepository pagoRepository) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
        this.pagoRepository = pagoRepository;
    }

    @Transactional
    public OrdenResponse checkout(String emailUsuario, CheckoutRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No tienes un carrito"));

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Direccion direccion = direccionRepository.findById(request.getDireccionEnvioId())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        if (!direccion.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Esta dirección no te pertenece");
        }

        // Validar stock de TODOS los items antes de tocar nada
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            if (itemCarrito.getCantidad() > itemCarrito.getProducto().getStock()) {
                throw new RuntimeException("Stock insuficiente para: " + itemCarrito.getProducto().getNombre());
            }
        }

        Orden orden = new Orden();
        orden.setComprador(usuario);
        orden.setDireccionEnvio(direccion);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito itemCarrito : carrito.getItems()) {
            Producto producto = itemCarrito.getProducto();

            ItemOrden itemOrden = new ItemOrden();
            itemOrden.setOrden(orden);
            itemOrden.setProducto(producto);
            itemOrden.setCantidad(itemCarrito.getCantidad());
            itemOrden.setPrecioUnitario(producto.getPrecio()); // se "congela" aquí

            orden.getItems().add(itemOrden);

            total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(itemCarrito.getCantidad())));

            // Descontar stock
            producto.setStock(producto.getStock() - itemCarrito.getCantidad());
            productoRepository.save(producto);
        }

        orden.setTotal(total);
        ordenRepository.save(orden);

        // Simular pago aprobado
        Pago pago = new Pago();
        pago.setOrden(orden);
        pago.setMetodo("tarjeta");
        pago.setEstado(EstadoPago.APROBADO);
        pago.setReferenciaTransaccion(UUID.randomUUID().toString());
        pagoRepository.save(pago);

        orden.setEstado(EstadoOrden.PAGADA);
        ordenRepository.save(orden);

        // Vaciar el carrito
        itemCarritoRepository.deleteAll(carrito.getItems());
        carrito.getItems().clear();

        return toResponse(orden);
    }

    public List<OrdenResponse> listarMisOrdenes(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ordenRepository.findByCompradorId(usuario.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrdenResponse toResponse(Orden orden) {
        List<ItemOrdenResponse> items = orden.getItems().stream()
                .map(i -> new ItemOrdenResponse(
                        i.getProducto().getId(), i.getProducto().getNombre(), i.getCantidad(),
                        i.getPrecioUnitario(), i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()))
                ))
                .toList();

        Direccion d = orden.getDireccionEnvio();
        DireccionResponse direccionResponse = new DireccionResponse(
                d.getId(), d.getCalle(), d.getCiudad(), d.getDepartamento(), d.getCodigoPostal(), d.isEsPrincipal()
        );

        return new OrdenResponse(orden.getId(), orden.getEstado().name(), orden.getTotal(), orden.getFecha(), items, direccionResponse);
    }
}