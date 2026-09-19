package com.marketplace.marketplace_backend.dto;

public class CategoriaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long categoriaPadreId;

    public CategoriaResponse(Long id, String nombre, String descripcion, Long categoriaPadreId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaPadreId = categoriaPadreId;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Long getCategoriaPadreId() { return categoriaPadreId; }
}