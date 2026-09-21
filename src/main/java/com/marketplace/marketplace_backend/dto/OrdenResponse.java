package com.marketplace.marketplace_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrdenResponse {
    private Long id;
    private String estado;
    private BigDecimal total;
    private LocalDateTime fecha;
    private List<ItemOrdenResponse> items;

    public OrdenResponse(Long id, String estado, BigDecimal total, LocalDateTime fecha, List<ItemOrdenResponse> items) {
        this.id = id;
        this.estado = estado;
        this.total = total;
        this.fecha = fecha;
        this.items = items;
    }

    public Long getId() { return id; }
    public String getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getFecha() { return fecha; }
    public List<ItemOrdenResponse> getItems() { return items; }
}