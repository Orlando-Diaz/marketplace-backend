package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.AuthResponse;
import com.marketplace.marketplace_backend.dto.RegistroRequest;
import com.marketplace.marketplace_backend.entity.Rol;
import com.marketplace.marketplace_backend.entity.Usuario;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import com.marketplace.marketplace_backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrar_conEmailNuevo_creaUsuarioYDevuelveToken() {
        // Arrange (preparamos los datos y el comportamiento simulado)
        RegistroRequest request = new RegistroRequest();
        request.setNombre("Orlando");
        request.setEmail("orlando@test.com");
        request.setPassword("123456");
        request.setTelefono("3001234567");

        when(usuarioRepository.existsByEmail("orlando@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash_simulado");
        when(jwtUtil.generarToken("orlando@test.com")).thenReturn("token_simulado");

        // Act (ejecutamos el método que estamos probando)
        AuthResponse response = authService.registrar(request);

        // Assert (verificamos que el resultado sea el esperado)
        assertEquals("token_simulado", response.getToken());
        assertEquals("orlando@test.com", response.getEmail());
        assertEquals("Orlando", response.getNombre());
        assertEquals(Rol.USUARIO.name(), response.getRol());

        // Verificamos que se haya guardado el usuario y enviado el correo
        verify(usuarioRepository).save(any(Usuario.class));
        verify(emailService).enviarBienvenida("orlando@test.com", "Orlando");
    }

    @Test
    void registrar_conEmailYaRegistrado_lanzaExcepcion() {
        RegistroRequest request = new RegistroRequest();
        request.setEmail("existente@test.com");
        request.setPassword("123456");
        request.setNombre("Alguien");

        when(usuarioRepository.existsByEmail("existente@test.com")).thenReturn(true);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            authService.registrar(request);
        });

        assertEquals("El email ya está registrado", excepcion.getMessage());

        // Confirmamos que NUNCA se llegó a guardar nada ni a enviar correo
        verify(usuarioRepository, never()).save(any());
        verify(emailService, never()).enviarBienvenida(any(), any());
    }
}