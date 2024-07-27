package com.example.demo.validators;

import com.example.demo.entities.Productos;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SaldoMinimoValidator {
    private static final BigDecimal SALDO_MINIMO = BigDecimal.ZERO;

    public void validate(Productos producto) {
        BigDecimal saldo = producto.getSaldo();

        // Verificación de que el saldo no sea cero
        if (saldo.compareTo(SALDO_MINIMO) == 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser cero.");
        }

        // Verificación específica para cuentas de ahorros
        if ("cuenta de ahorros".equals(producto.getTipoCuenta()) && saldo.compareTo(SALDO_MINIMO) < 0) {
            throw new IllegalArgumentException("La cuenta de ahorros no puede tener un saldo menor a " + SALDO_MINIMO + ".");
        }
    }
}

