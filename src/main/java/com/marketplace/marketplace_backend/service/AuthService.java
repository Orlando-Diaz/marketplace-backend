package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.AuthResponse;
import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.RegistroRequest;
import com.marketplace.marketplace_backend.entity.Rol;
import com.marketplace.marketplace_backend.entity.Usuario;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import com.marketplace.marketplace_backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(Rol.USUARIO);

        usuarioRepository.save(usuario);

        String token = jwtUtil.generarToken(usuario.getEmail());
        return new AuthResponse(token, usuario.getEmail(), usuario.getNombre(), usuario.getRol().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generarToken(usuario.getEmail());
        return new AuthResponse(token, usuario.getEmail(), usuario.getNombre(), usuario.getRol().name());
    }
}