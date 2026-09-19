package com.marketplace.marketplace_backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoResponse {
    private Long id;
    private List<ItemCarritoResponse> items;
    private BigDecimal total;

    public CarritoResponse(Long id, List<ItemCarritoResponse> items, BigDecimal total) {
        this.id = id;
        this.items = items;
        this.total = total;
    }

    public Long getId() { return id; }
    public List<ItemCarritoResponse> getItems() { return items; }
    public BigDecimal getTotal() { return total; }
}