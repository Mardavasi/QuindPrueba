package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransaccionDTO {
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
}
