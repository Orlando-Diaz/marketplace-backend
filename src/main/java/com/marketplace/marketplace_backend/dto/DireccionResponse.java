package com.marketplace.marketplace_backend.dto;

public class DireccionResponse {
    private Long id;
    private String calle;
    private String ciudad;
    private String departamento;
    private String codigoPostal;
    private boolean esPrincipal;

    public DireccionResponse(Long id, String calle, String ciudad, String departamento,
                             String codigoPostal, boolean esPrincipal) {
        this.id = id;
        this.calle = calle;
        this.ciudad = ciudad;
        this.departamento = departamento;
        this.codigoPostal = codigoPostal;
        this.esPrincipal = esPrincipal;
    }

    public Long getId() { return id; }
    public String getCalle() { return calle; }
    public String getCiudad() { return ciudad; }
    public String getDepartamento() { return departamento; }
    public String getCodigoPostal() { return codigoPostal; }
    public boolean isEsPrincipal() { return esPrincipal; }
}