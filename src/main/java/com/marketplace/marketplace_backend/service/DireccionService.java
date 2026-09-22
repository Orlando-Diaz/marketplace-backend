package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.DireccionRequest;
import com.marketplace.marketplace_backend.dto.DireccionResponse;
import com.marketplace.marketplace_backend.entity.Direccion;
import com.marketplace.marketplace_backend.entity.Usuario;
import com.marketplace.marketplace_backend.exception.RecursoNoEncontradoException;
import com.marketplace.marketplace_backend.repository.DireccionRepository;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public DireccionService(DireccionRepository direccionRepository, UsuarioRepository usuarioRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public DireccionResponse crear(String emailUsuario, DireccionRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
        direccion.setCalle(request.getCalle());
        direccion.setCiudad(request.getCiudad());
        direccion.setDepartamento(request.getDepartamento());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setEsPrincipal(request.isEsPrincipal());

        direccionRepository.save(direccion);
        return toResponse(direccion);
    }

    public List<DireccionResponse> listarMisDirecciones(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        return direccionRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DireccionResponse toResponse(Direccion d) {
        return new DireccionResponse(d.getId(), d.getCalle(), d.getCiudad(),
                d.getDepartamento(), d.getCodigoPostal(), d.isEsPrincipal());
    }
}