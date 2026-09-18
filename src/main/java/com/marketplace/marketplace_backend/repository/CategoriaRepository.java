package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByCategoriaPadreIsNull();
    List<Categoria> findByCategoriaPadreId(Long categoriaPadreId);
}