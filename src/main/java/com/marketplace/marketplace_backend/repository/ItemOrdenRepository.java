package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.entity.ItemOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemOrdenRepository extends JpaRepository<ItemOrden, Long> {
    boolean existsByOrdenCompradorIdAndProductoId(Long compradorId, Long productoId);
}