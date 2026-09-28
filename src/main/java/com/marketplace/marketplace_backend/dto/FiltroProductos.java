package com.marketplace.marketplace_backend.dto;

import java.math.BigDecimal;

public record FiltroProductos(
        String q,
        Long categoriaId,
        BigDecimal precioMin,
        BigDecimal precioMax,
        Boolean soloDisponibles,
        String orden
) {}