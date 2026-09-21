package com.marketplace.marketplace_backend.dto;

import java.time.LocalDateTime;

public class ResenaResponse {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fecha;

    public ResenaResponse(Long id, Long usuarioId, String usuarioNombre, Integer calificacion,
                          String comentario, LocalDateTime fecha) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public Integer getCalificacion() { return calificacion; }
    public String getComentario() { return comentario; }
    public LocalDateTime getFecha() { return fecha; }
}