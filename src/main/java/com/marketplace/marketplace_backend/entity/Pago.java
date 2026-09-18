package com.marketplace.marketplace_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false, unique = true)
    private Orden orden;

    @Column(nullable = false, length = 30)
    private String metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(name = "referencia_transaccion", nullable = false, unique = true, length = 100)
    private String referenciaTransaccion;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPago.PENDIENTE;
        }
    }
}