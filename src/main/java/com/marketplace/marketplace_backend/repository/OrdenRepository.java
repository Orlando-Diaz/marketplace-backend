package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByCompradorId(Long compradorId);
}