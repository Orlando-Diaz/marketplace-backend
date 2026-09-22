package com.marketplace.marketplace_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String estado;
    private LocalDateTime fechaPublicacion;
    private Long usuarioId;
    private String usuarioNombre;
    private Long categoriaId;
    private String categoriaNombre;
    private Double calificacionPromedio;
    private Integer totalResenas;

    public ProductoResponse(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock,
                            String estado, LocalDateTime fechaPublicacion, Long usuarioId, String usuarioNombre,
                            Long categoriaId, String categoriaNombre, Double calificacionPromedio, Integer totalResenas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
        this.fechaPublicacion = fechaPublicacion;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.calificacionPromedio = calificacionPromedio;
        this.totalResenas = totalResenas;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public Integer getStock() { return stock; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public Long getUsuarioId() { return usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public Long getCategoriaId() { return categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public Double getCalificacionPromedio() { return calificacionPromedio; }
    public Integer getTotalResenas() { return totalResenas; }
}