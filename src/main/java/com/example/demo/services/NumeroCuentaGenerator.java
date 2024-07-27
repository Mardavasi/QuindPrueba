package com.example.demo.services;

import com.example.demo.entities.Productos;
import com.example.demo.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NumeroCuentaGenerator {
    @Autowired
    private ProductosRepository repository;

    public void generarYAsignarNumeroCuenta(Productos producto) {
        String prefijo = "cuenta corriente".equals(producto.getTipoCuenta()) ? "33" : "53";
        producto.setNumeroCuenta(generarNumeroCuenta(prefijo));
    }

    private String generarNumeroCuenta(String prefijo) {
        String numeroCuenta;
        boolean existe;

        do {
            numeroCuenta = prefijo + generateRandomDigits(8);
            existe = repository.existsByNumeroCuenta(numeroCuenta);
        } while (existe);

        return numeroCuenta;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}
