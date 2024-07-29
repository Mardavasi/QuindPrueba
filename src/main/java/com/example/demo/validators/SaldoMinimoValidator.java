package com.example.demo.validators;

import com.example.demo.entities.Productos;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SaldoMinimoValidator {
    private static final BigDecimal SALDO_MINIMO = BigDecimal.ZERO;

    public void validate(Productos producto) {
        BigDecimal saldo = producto.getSaldo();

        if (saldo.compareTo(SALDO_MINIMO) == 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser cero.");
        }

        if ("cuenta de ahorros".equals(producto.getTipoCuenta()) && saldo.compareTo(SALDO_MINIMO) < 0) {
            throw new IllegalArgumentException("La cuenta de ahorros no puede tener un saldo menor a " + SALDO_MINIMO + ".");
        }
        if ("cuenta corriente".equals(producto.getTipoCuenta()) && saldo.compareTo(SALDO_MINIMO) < 0) {
            throw new IllegalArgumentException("La cuenta corriente no puede tener un saldo menor a " + SALDO_MINIMO + ".");
        }
    }
}

