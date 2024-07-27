package com.example.demo.services;

import com.example.demo.entities.Transaccion;

import java.math.BigDecimal;

public interface TransaccionService {
    Transaccion realizarTransaccion(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto);
    Transaccion realizarRetiro(Long cuentaOrigenId, BigDecimal monto);
    Transaccion realizarDeposito(Long cuentaDestinoId, BigDecimal monto);
}
