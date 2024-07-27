package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransaccionDTO {
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;


}
