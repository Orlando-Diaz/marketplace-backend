package com.marketplace.marketplace_backend.dto;

import java.math.BigDecimal;

public class ItemCarritoResponse {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private BigDecimal productoPrecio;
    private Integer cantidad;
    private BigDecimal subtotal;
    private Integer productoStock;


    public ItemCarritoResponse(Long id, Long productoId, String productoNombre,
                               BigDecimal productoPrecio, Integer cantidad, BigDecimal subtotal, Integer productoStock) {
        this.id = id;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoPrecio = productoPrecio;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.productoStock = productoStock;
    }

    public Long getId() { return id; }
    public Long getProductoId() { return productoId; }
    public String getProductoNombre() { return productoNombre; }
    public BigDecimal getProductoPrecio() { return productoPrecio; }
    public Integer getCantidad() { return cantidad; }
    public BigDecimal getSubtotal() { return subtotal; }
    public Integer getProductoStock() { return productoStock; }
}