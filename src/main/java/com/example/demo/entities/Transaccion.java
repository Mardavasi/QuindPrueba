package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "transaccion")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private BigDecimal monto;


    @ManyToOne
    @JoinColumn(name = "cuenta_origen_id")
    private Productos cuentaOrigen;

    @ManyToOne
    @JoinColumn(name = "cuenta_destino_id")
    private Productos cuentaDestino;

    private LocalDateTime fechaHoraTransaccion;


}
