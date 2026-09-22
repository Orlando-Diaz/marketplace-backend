package com.marketplace.marketplace_backend.dto;

import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull(message = "La dirección de envío es obligatoria")
    private Long direccionEnvioId;

    public Long getDireccionEnvioId() { return direccionEnvioId; }
    public void setDireccionEnvioId(Long direccionEnvioId) { this.direccionEnvioId = direccionEnvioId; }
}