package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.*;
import com.marketplace.marketplace_backend.entity.*;
import com.marketplace.marketplace_backend.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class OrdenServiceIntegrationTest {

    @Autowired private OrdenService ordenService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ItemCarritoRepository itemCarritoRepository;
    @Autowired private DireccionRepository direccionRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Simulamos el envío de correos para no intentar conectarnos a un SMTP real durante el test
    @MockitoBean
    private EmailService emailService;

    @Test
    void checkoutCompleto_flujoReal_creaOrdenDescuentaStockYVaciaCarrito() {
        // Arrange: creamos datos reales en la base H2, tal como los crearía la app
        Usuario usuario = new Usuario();
        usuario.setNombre("Orlando");
        usuario.setEmail("integracion@test.com");
        usuario.setPassword(passwordEncoder.encode("123456"));
        usuario.setRol(Rol.USUARIO);
        usuario = usuarioRepository.save(usuario);

        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setUsuario(usuario);
        producto.setCategoria(categoria);
        producto.setNombre("Bicicleta");
        producto.setDescripcion("Test");
        producto.setPrecio(new BigDecimal("850000"));
        producto.setStock(5);
        producto.setEstado(EstadoProducto.DISPONIBLE);
        producto = productoRepository.save(producto);

        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
        direccion.setCalle("Calle 10");
        direccion.setCiudad("Armenia");
        direccion.setDepartamento("Quindío");
        direccion = direccionRepository.save(direccion);

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito = carritoRepository.save(carrito);

        ItemCarrito item = new ItemCarrito();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(2);
        itemCarritoRepository.save(item);

        CheckoutRequest request = new CheckoutRequest();
        request.setDireccionEnvioId(direccion.getId());

        // Act: ejecutamos el checkout real, contra la base H2 real
        OrdenResponse response = ordenService.checkout(usuario.getEmail(), request);

        // Assert: verificamos en la base que todo quedó consistente
        assertEquals("PAGADA", response.getEstado());
        assertEquals(0, new BigDecimal("1700000").compareTo(response.getTotal()));

        Producto productoActualizado = productoRepository.findById(producto.getId()).orElseThrow();
        assertEquals(3, productoActualizado.getStock());

        var itemRestante = itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), producto.getId());
        assertTrue(itemRestante.isEmpty());

        verify(emailService).enviarConfirmacionCompra(eq("integracion@test.com"), eq("Orlando"), any(), any());
    }
}