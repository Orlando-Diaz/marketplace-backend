package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoId(Long productoId);
    boolean existsByProductoIdAndUsuarioId(Long productoId, Long usuarioId);
}