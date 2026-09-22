package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.entity.EstadoProducto;
import com.marketplace.marketplace_backend.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByUsuarioId(Long usuarioId);
    Page<Producto> findByEstado(EstadoProducto estado, Pageable pageable);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}