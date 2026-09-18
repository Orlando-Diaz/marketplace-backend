package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.entity.EstadoProducto;
import com.marketplace.marketplace_backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByUsuarioId(Long usuarioId);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByEstado(EstadoProducto estado);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}