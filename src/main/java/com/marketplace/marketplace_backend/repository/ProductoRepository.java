package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.entity.EstadoProducto;
import com.marketplace.marketplace_backend.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    List<Producto> findByUsuarioId(Long usuarioId);
    Page<Producto> findByEstado(EstadoProducto estado, Pageable pageable);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByCategoriaId(Long categoriaId);
}