package com.example.demo.service.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.entities.Productos;
import com.example.demo.entities.Transaccion;
import com.example.demo.services.Implement.TransaccionServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;


import com.example.demo.repository.ProductosRepository;
import com.example.demo.repository.TransaccionRepository;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TransaccionServiceImpTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private ProductosRepository productosRepository;

    @InjectMocks
    private TransaccionServiceImp transaccionServiceImp;

    @Test
    void testRealizarDepositoExitoso() {

        Long cuentaDestinoId = 1L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaDestino = new Productos();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setSaldo(new BigDecimal("600.00"));

        Transaccion transaccionEsperada = new Transaccion();
        transaccionEsperada.setCuentaOrigen(null);
        transaccionEsperada.setCuentaDestino(cuentaDestino);
        transaccionEsperada.setMonto(monto);
        transaccionEsperada.setTipo("DEPOSITO");
        transaccionEsperada.setFechaHoraTransaccion(null);


        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion transaccion = invocation.getArgument(0);
            transaccion.setFechaHoraTransaccion(LocalDateTime.now());
            return transaccion;
        });
        when(productosRepository.save(any(Productos.class))).thenReturn(cuentaDestino);
        Transaccion resultado = transaccionServiceImp.realizarDeposito(cuentaDestinoId, monto);
        assertEquals(cuentaDestinoId, resultado.getCuentaDestino().getId());
        assertEquals(monto, resultado.getMonto());
        assertEquals("DEPOSITO", resultado.getTipo());
        assertEquals(new BigDecimal("700.00"), resultado.getCuentaDestino().getSaldo()); // saldo esperado después del depósito


        verify(transaccionRepository).save(argThat(transaccion ->
                transaccion.getTipo().equals("DEPOSITO") &&
                        transaccion.getMonto().equals(monto) &&
                        transaccion.getCuentaDestino().equals(cuentaDestino) &&
                        transaccion.getFechaHoraTransaccion() != null
        ));
        verify(productosRepository).save(cuentaDestino);
    }


    @Test
    void testRealizarDepositoMontoNegativo() {
        Long cuentaDestinoId = 1L;
        BigDecimal monto = new BigDecimal("-100.00");
        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarDeposito(cuentaDestinoId, monto);
        });
    }


    @Test
    void testRealizarDepositoCuentaNoEncontrada() {
        // Arrange
        Long cuentaDestinoId = 1L;
        BigDecimal monto = new BigDecimal("100.00");
        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarDeposito(cuentaDestinoId, monto);
        });
    }


    @Test
    void testRealizarRetiroExitoso() {

        Long cuentaOrigenId = 1L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("700.00"));

        Transaccion transaccionEsperada = new Transaccion();
        transaccionEsperada.setCuentaOrigen(cuentaOrigen);
        transaccionEsperada.setCuentaDestino(null);
        transaccionEsperada.setMonto(monto);
        transaccionEsperada.setTipo("RETIRO");
        transaccionEsperada.setFechaHoraTransaccion(null);
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion transaccion = invocation.getArgument(0);
            transaccion.setFechaHoraTransaccion(LocalDateTime.now());
            return transaccion;
        });
        when(productosRepository.save(any(Productos.class))).thenReturn(cuentaOrigen);

        Transaccion resultado = transaccionServiceImp.realizarRetiro(cuentaOrigenId, monto);
        assertEquals(cuentaOrigenId, resultado.getCuentaOrigen().getId());
        assertEquals(monto, resultado.getMonto());
        assertEquals("RETIRO", resultado.getTipo());
        assertEquals(new BigDecimal("600.00"), resultado.getCuentaOrigen().getSaldo());


        verify(transaccionRepository).save(argThat(transaccion ->
                transaccion.getTipo().equals("RETIRO") &&
                        transaccion.getMonto().equals(monto) &&
                        transaccion.getCuentaOrigen().equals(cuentaOrigen) &&
                        transaccion.getFechaHoraTransaccion() != null
        ));
        verify(productosRepository).save(cuentaOrigen);
    }


    @Test
    void testRealizarRetiroSaldoInsuficiente() {

        Long cuentaOrigenId = 1L;
        BigDecimal monto = new BigDecimal("100.00");
        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("50.00"));
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));

        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarRetiro(cuentaOrigenId, monto);
        });
    }


    @Test
    void testRealizarRetiroCuentaNoEncontrada() {

        Long cuentaOrigenId = 1L;
        BigDecimal monto = new BigDecimal("100.00");
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarRetiro(cuentaOrigenId, monto);
        });
    }


    @Test
    void testRealizarTransaccionExitosa() {

        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("700.00"));

        Productos cuentaDestino = new Productos();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setSaldo(new BigDecimal("300.00"));
        Transaccion transaccionEsperada = new Transaccion();
        transaccionEsperada.setCuentaOrigen(cuentaOrigen);
        transaccionEsperada.setCuentaDestino(cuentaDestino);
        transaccionEsperada.setMonto(monto);
        transaccionEsperada.setTipo("TRANSFERENCIA");
        transaccionEsperada.setFechaHoraTransaccion(null);

        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion transaccion = invocation.getArgument(0);
            transaccion.setFechaHoraTransaccion(LocalDateTime.now());
            return transaccion;
        });
        when(productosRepository.save(any(Productos.class))).thenAnswer(invocation -> {
            Productos producto = invocation.getArgument(0);
            return producto;
        });


        Transaccion resultado = transaccionServiceImp.realizarTransaccion(cuentaOrigenId, cuentaDestinoId, monto);


        assertEquals(cuentaOrigenId, resultado.getCuentaOrigen().getId());
        assertEquals(cuentaDestinoId, resultado.getCuentaDestino().getId());
        assertEquals(monto, resultado.getMonto());
        assertEquals("TRANSFERENCIA", resultado.getTipo());
        assertEquals(new BigDecimal("600.00"), resultado.getCuentaOrigen().getSaldo());
        assertEquals(new BigDecimal("400.00"), resultado.getCuentaDestino().getSaldo());

        verify(transaccionRepository).save(argThat(transaccion ->
                transaccion.getTipo().equals("TRANSFERENCIA") &&
                        transaccion.getMonto().equals(monto) &&
                        transaccion.getCuentaOrigen().equals(cuentaOrigen) &&
                        transaccion.getCuentaDestino().equals(cuentaDestino) &&
                        transaccion.getFechaHoraTransaccion() != null
        ));
        verify(productosRepository).save(cuentaOrigen);
        verify(productosRepository).save(cuentaDestino);
    }


    @Test
    void testRealizarTransaccionSaldoInsuficiente() {

        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("50.00"));

        Productos cuentaDestino = new Productos();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setSaldo(new BigDecimal("300.00"));


        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.of(cuentaDestino));


        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarTransaccion(cuentaOrigenId, cuentaDestinoId, monto);
        });
    }


    @Test
    void testRealizarTransaccionCuentaDestinoNoEncontrada() {

        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("700.00"));

        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarTransaccion(cuentaOrigenId, cuentaDestinoId, monto);
        });
    }


}
