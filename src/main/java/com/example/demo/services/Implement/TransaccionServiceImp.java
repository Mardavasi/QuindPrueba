package com.example.demo.services.Implement;

import com.example.demo.entities.Productos;
import com.example.demo.entities.Transaccion;
import com.example.demo.repository.ProductosRepository;
import com.example.demo.repository.TransaccionRepository;
import com.example.demo.services.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransaccionServiceImp implements TransaccionService {
    @Autowired
    private TransaccionRepository transaccionRepository;
    @Autowired
    private ProductosRepository productosRepository;


    @Override
    public Transaccion realizarTransaccion(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto) {
        Productos cuentaOrigen = productosRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de origen no encontrada con id: " + cuentaOrigenId));
        Productos cuentaDestino = productosRepository.findById(cuentaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada con id: " + cuentaDestinoId));

        // Verificar que la cuenta de origen tenga suficiente saldo
        if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente en la cuenta de origen.");
        }

        // Actualizar los saldos
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));

        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigen(cuentaOrigen);
        transaccion.setCuentaDestino(cuentaDestino);
        transaccion.setMonto(monto);
        transaccion.setTipo("TRANSFERENCIA");
        transaccion.setFechaHoraTransaccion(LocalDateTime.now());


        // Guardar las cuentas actualizadas y la transacción
        productosRepository.save(cuentaOrigen);
        productosRepository.save(cuentaDestino);
        return transaccionRepository.save(transaccion);
    }

    @Override
    public Transaccion realizarRetiro(Long cuentaOrigenId, BigDecimal monto) {
        Productos cuentaOrigen = productosRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de origen no encontrada con id: " + cuentaOrigenId));

        if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el retiro.");
        }

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));

        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigen(cuentaOrigen);
        transaccion.setCuentaDestino(null);
        transaccion.setMonto(monto);
        transaccion.setFechaHoraTransaccion(LocalDateTime.now());
        transaccion.setTipo("RETIRO");

        productosRepository.save(cuentaOrigen);
        return transaccionRepository.save(transaccion);
    }

    @Override
    public Transaccion realizarDeposito(Long cuentaDestinoId, BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser positivo.");
        }
        Productos cuentaDestino = productosRepository.findById(cuentaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada con id: " + cuentaDestinoId));

        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));

        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigen(null); //
        transaccion.setCuentaDestino(cuentaDestino);
        transaccion.setMonto(monto);
        transaccion.setFechaHoraTransaccion(LocalDateTime.now());
        transaccion.setTipo("DEPOSITO");

        productosRepository.save(cuentaDestino);
        return transaccionRepository.save(transaccion);
    }
}
