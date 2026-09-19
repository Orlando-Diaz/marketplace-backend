package com.marketplace.marketplace_backend.dto;

public class CategoriaRequest {
    private String nombre;
    private String descripcion;
    private Long categoriaPadreId;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getCategoriaPadreId() { return categoriaPadreId; }
    public void setCategoriaPadreId(Long categoriaPadreId) { this.categoriaPadreId = categoriaPadreId; }
}