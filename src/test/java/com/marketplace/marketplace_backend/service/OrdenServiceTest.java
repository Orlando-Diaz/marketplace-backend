package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.CheckoutRequest;
import com.marketplace.marketplace_backend.dto.OrdenResponse;
import com.marketplace.marketplace_backend.entity.*;
import com.marketplace.marketplace_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock private CarritoRepository carritoRepository;
    @Mock private ItemCarritoRepository itemCarritoRepository;
    @Mock private DireccionRepository direccionRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private OrdenRepository ordenRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private PagoRepository pagoRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private OrdenService ordenService;

    private Usuario usuario;
    private Producto producto;
    private Direccion direccion;
    private Carrito carrito;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("orlando@test.com");
        usuario.setNombre("Orlando");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Bicicleta");
        producto.setPrecio(new BigDecimal("850000"));
        producto.setStock(5);

        direccion = new Direccion();
        direccion.setId(1L);
        direccion.setUsuario(usuario);
        direccion.setCalle("Calle 10");
        direccion.setCiudad("Armenia");
        direccion.setDepartamento("Quindío");

        ItemCarrito item = new ItemCarrito();
        item.setId(1L);
        item.setProducto(producto);
        item.setCantidad(2);

        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        List<ItemCarrito> items = new ArrayList<>();
        items.add(item);
        carrito.setItems(items);
    }

    @Test
    void checkout_conStockSuficiente_creaOrdenYDescuentaStock() {
        CheckoutRequest request = new CheckoutRequest();
        request.setDireccionEnvioId(1L);

        when(usuarioRepository.findByEmail("orlando@test.com")).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));

        OrdenResponse response = ordenService.checkout("orlando@test.com", request);

        // El total debe ser precio * cantidad = 850000 * 2 = 1700000
        assertEquals(new BigDecimal("1700000"), response.getTotal());
        assertEquals("PAGADA", response.getEstado());

        // El stock del producto debe haberse descontado (5 - 2 = 3)
        assertEquals(3, producto.getStock());

        // Se debe haber guardado la orden, el pago, y enviado el correo
        verify(ordenRepository, atLeastOnce()).save(any(Orden.class));
        verify(pagoRepository).save(any(Pago.class));
        verify(emailService).enviarConfirmacionCompra(eq("orlando@test.com"), eq("Orlando"), any(), any());
    }

    @Test
    void checkout_conStockInsuficiente_lanzaExcepcionYNoModificaNada() {
        producto.setStock(1); // Solo hay 1, pero el carrito pide 2

        CheckoutRequest request = new CheckoutRequest();
        request.setDireccionEnvioId(1L);

        when(usuarioRepository.findByEmail("orlando@test.com")).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));

        assertThrows(RuntimeException.class, () -> {
            ordenService.checkout("orlando@test.com", request);
        });

        // El stock NO debe haberse tocado
        assertEquals(1, producto.getStock());

        // Nunca se debe haber guardado ninguna orden ni pago
        verify(ordenRepository, never()).save(any());
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void checkout_conCarritoVacio_lanzaExcepcion() {
        carrito.setItems(new ArrayList<>());

        CheckoutRequest request = new CheckoutRequest();
        request.setDireccionEnvioId(1L);

        when(usuarioRepository.findByEmail("orlando@test.com")).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            ordenService.checkout("orlando@test.com", request);
        });

        assertEquals("El carrito está vacío", excepcion.getMessage());
    }

    @Test
    void checkout_conDireccionDeOtroUsuario_lanzaExcepcion() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        direccion.setUsuario(otroUsuario); // La dirección pertenece a otro usuario

        CheckoutRequest request = new CheckoutRequest();
        request.setDireccionEnvioId(1L);

        when(usuarioRepository.findByEmail("orlando@test.com")).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            ordenService.checkout("orlando@test.com", request);
        });

        assertEquals("Esta dirección no te pertenece", excepcion.getMessage());
    }
}