package com.example.demo.validators;

import org.springframework.stereotype.Component;

@Component
public class TipoCuentaValidator {
    public void validate(String tipoCuenta) {
        if (!tipoCuenta.equals("cuenta corriente") && !tipoCuenta.equals("cuenta de ahorros")) {
            throw new IllegalArgumentException("El tipo de cuenta debe ser 'cuenta corriente' o 'cuenta de ahorros'.");
        }
    }
}