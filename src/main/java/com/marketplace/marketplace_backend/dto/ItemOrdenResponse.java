package com.marketplace.marketplace_backend.dto;

import java.math.BigDecimal;

public class ItemOrdenResponse {
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public ItemOrdenResponse(Long productoId, String productoNombre, Integer cantidad,
                             BigDecimal precioUnitario, BigDecimal subtotal) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public Long getProductoId() { return productoId; }
    public String getProductoNombre() { return productoNombre; }
    public Integer getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
}