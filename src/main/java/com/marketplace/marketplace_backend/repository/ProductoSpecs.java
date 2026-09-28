package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.entity.EstadoProducto;
import com.marketplace.marketplace_backend.entity.Producto;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public final class ProductoSpecs {

    private ProductoSpecs() {}

    public static Specification<Producto> conEstado(EstadoProducto estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    public static Specification<Producto> nombreContiene(String texto) {
        String patron = "%" + texto.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("nombre")), patron);
    }

    public static Specification<Producto> enCategorias(List<Long> categoriaIds) {
        return (root, query, cb) -> root.get("categoria").get("id").in(categoriaIds);
    }

    public static Specification<Producto> precioMinimo(BigDecimal minimo) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("precio"), minimo);
    }

    public static Specification<Producto> precioMaximo(BigDecimal maximo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("precio"), maximo);
    }

    public static Specification<Producto> conStock() {
        return (root, query, cb) -> cb.greaterThan(root.get("stock"), 0);
    }
}