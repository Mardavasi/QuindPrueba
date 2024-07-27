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

    /**
     * Este test verifica el comportamiento del método realizarDeposito cuando se realiza un depósito exitoso en una cuenta existente.
     * Se configura el saldo inicial de la cuenta destino en 600.00 y se intenta depositar 100.00.
     * Después de la operación, el saldo esperado en la cuenta destino debe ser 700.00.
     * La prueba también asegura que:
     * La transacción se guarda correctamente en el repositorio.
     * Los valores del objeto Transaccion guardado coinciden con los valores esperados,
     * excepto la fecha de la transacción, que se establece en el momento de la prueba.
     * Se configura el comportamiento de los mocks para simular la recuperación de la cuenta
     * y el guardado tanto de la cuenta como de la transacción.
     */
    @Test
    void testRealizarDepositoExitoso() {
        // Arrange
        Long cuentaDestinoId = 1L;
        BigDecimal monto = new BigDecimal("100.00");
        // Configurar el saldo inicial de la cuenta destino para que el saldo final sea el esperado
        Productos cuentaDestino = new Productos();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setSaldo(new BigDecimal("600.00")); // saldo inicial para que después del depósito sea 700.00

        Transaccion transaccionEsperada = new Transaccion();
        transaccionEsperada.setCuentaOrigen(null);
        transaccionEsperada.setCuentaDestino(cuentaDestino);
        transaccionEsperada.setMonto(monto);
        transaccionEsperada.setTipo("DEPOSITO");
        transaccionEsperada.setFechaHoraTransaccion(null); // no verificar este campo

        // Configurar los mocks
        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion transaccion = invocation.getArgument(0);
            transaccion.setFechaHoraTransaccion(LocalDateTime.now()); // establecer la fecha actual aquí
            return transaccion;
        });
        when(productosRepository.save(any(Productos.class))).thenReturn(cuentaDestino);
        Transaccion resultado = transaccionServiceImp.realizarDeposito(cuentaDestinoId, monto);
        assertEquals(cuentaDestinoId, resultado.getCuentaDestino().getId());
        assertEquals(monto, resultado.getMonto());
        assertEquals("DEPOSITO", resultado.getTipo());
        assertEquals(new BigDecimal("700.00"), resultado.getCuentaDestino().getSaldo()); // saldo esperado después del depósito

        // Verificar que se guardó la transacción
        verify(transaccionRepository).save(argThat(transaccion ->
                transaccion.getTipo().equals("DEPOSITO") &&
                        transaccion.getMonto().equals(monto) &&
                        transaccion.getCuentaDestino().equals(cuentaDestino) &&
                        transaccion.getFechaHoraTransaccion() != null // solo verificar que no sea null
        ));
        verify(productosRepository).save(cuentaDestino);
    }

    /**
     * Este test verifica que el método realizarDeposito lance una excepción
     * cuando se intenta realizar un depósito con un monto negativo.
     * Se espera que el método lance una IllegalArgumentException en este caso, ya que el monto del depósito no puede ser negativo.
     */
    @Test
    void testRealizarDepositoMontoNegativo() {
        Long cuentaDestinoId = 1L;
        BigDecimal monto = new BigDecimal("-100.00");
        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarDeposito(cuentaDestinoId, monto);
        });
    }

    /**
     * Este test verifica que el método realizarDeposito lance una excepción
     * cuando se intenta realizar un depósito en una cuenta que no existe.
     * Se configura el mock para que findById devuelva un Optional vacío, simulando que la cuenta no se encuentra.
     * Se espera que el método lance una IllegalArgumentException en este caso, ya que no se puede realizar un depósito en una cuenta inexistente.
     */
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

    /**
     * Este test verifica el comportamiento del método realizarRetiro cuando se realiza un retiro exitoso de una cuenta existente.
     * Se configura el saldo inicial de la cuenta origen en 700.00 y se intenta retirar 100.00.
     * Después del retiro, el saldo esperado en la cuenta origen debe ser 600.00.
     * La prueba también asegura que:
     * - La transacción se guarda correctamente en el repositorio.
     * - Los valores del objeto Transaccion guardado coinciden con los valores esperados, excepto la fecha de la transacción, que se establece en el momento de la prueba.
     * Se configura el comportamiento de los mocks para simular la recuperación de la cuenta y el guardado tanto de la cuenta como de la transacción.
     */
    @Test
    void testRealizarRetiroExitoso() {

        Long cuentaOrigenId = 1L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("700.00")); // saldo inicial para que después del retiro sea 600.00

        Transaccion transaccionEsperada = new Transaccion();
        transaccionEsperada.setCuentaOrigen(cuentaOrigen);
        transaccionEsperada.setCuentaDestino(null);
        transaccionEsperada.setMonto(monto);
        transaccionEsperada.setTipo("RETIRO");
        transaccionEsperada.setFechaHoraTransaccion(null); // no verificar este campo
        // Configura los mocks para simular la recuperación y el guardado de los objetos.
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion transaccion = invocation.getArgument(0);
            transaccion.setFechaHoraTransaccion(LocalDateTime.now()); // establecer la fecha actual aquí
            return transaccion;
        });
        when(productosRepository.save(any(Productos.class))).thenReturn(cuentaOrigen);
        // Llama al método realizarRetiro con los parámetros configurados.
        Transaccion resultado = transaccionServiceImp.realizarRetiro(cuentaOrigenId, monto);
        //  Verifica que el resultado sea el esperado, incluyendo el saldo actualizado y la transacción guardada.
        assertEquals(cuentaOrigenId, resultado.getCuentaOrigen().getId());
        assertEquals(monto, resultado.getMonto());
        assertEquals("RETIRO", resultado.getTipo());
        assertEquals(new BigDecimal("600.00"), resultado.getCuentaOrigen().getSaldo()); // saldo esperado después del retiro

        // Verifica que el método save de transaccionRepository haya sido llamado con los valores correctos.
        verify(transaccionRepository).save(argThat(transaccion ->
                transaccion.getTipo().equals("RETIRO") &&
                        transaccion.getMonto().equals(monto) &&
                        transaccion.getCuentaOrigen().equals(cuentaOrigen) &&
                        transaccion.getFechaHoraTransaccion() != null // solo verificar que no sea null
        ));
        verify(productosRepository).save(cuentaOrigen);
    }

    /**
     * Este test verifica que el método realizarRetiro lance una excepción
     * cuando se intenta realizar un retiro que excede el saldo disponible.
     * Se configura el saldo de la cuenta origen en 50.00 y se intenta retirar 100.00.
     * Se espera que el método lance una IllegalArgumentException en este caso, ya que el saldo es insuficiente para el retiro.
     */
    @Test
    void testRealizarRetiroSaldoInsuficiente() {

        Long cuentaOrigenId = 1L;
        BigDecimal monto = new BigDecimal("100.00");
        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("50.00")); // saldo insuficiente
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        //  Verifica que se lance una IllegalArgumentException al intentar realizar el retiro con saldo insuficiente.
        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarRetiro(cuentaOrigenId, monto);
        });
    }

    /**
     * Este test verifica que el método realizarRetiro lance una excepción
     * cuando se intenta realizar un retiro en una cuenta que no existe.
     * Se configura el mock para que findById devuelva un Optional vacío, simulando que la cuenta no se encuentra.
     * Se espera que el método lance una IllegalArgumentException en este caso, ya que no se puede realizar un retiro en una cuenta inexistente.
     */
    @Test
    void testRealizarRetiroCuentaNoEncontrada() {

        Long cuentaOrigenId = 1L;
        BigDecimal monto = new BigDecimal("100.00");

        // Configura el mock para que findById devuelva un Optional vacío, simulando que la cuenta no se encuentra.
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.empty());

        //  Verifica que se lance una IllegalArgumentException al intentar realizar el retiro cuando la cuenta no se encuentra.
        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarRetiro(cuentaOrigenId, monto);
        });
    }

    /**
     * Este test verifica el comportamiento del método realizarTransaccion cuando se realiza una transferencia exitosa entre dos cuentas existentes.
     * Se configuran los saldos iniciales de las cuentas origen y destino, y se realiza una transferencia.
     * Después de la transferencia, el saldo de la cuenta origen debe disminuir en el monto de la transferencia, y el saldo de la cuenta destino debe aumentar en el mismo monto.
     * La prueba también asegura que:
     * - La transacción se guarda correctamente en el repositorio.
     * - Los valores del objeto Transaccion guardado coinciden con los valores esperados, excepto la fecha de la transacción, que se establece en el momento de la prueba.
     * Se configura el comportamiento de los mocks para simular la recuperación de las cuentas y el guardado tanto de las cuentas como de la transacción.
     */
    @Test
    void testRealizarTransaccionExitosa() {

        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("700.00")); // saldo inicial para que después de la transferencia sea 600.00

        Productos cuentaDestino = new Productos();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setSaldo(new BigDecimal("300.00")); // saldo inicial para que después de la transferencia sea 400.00

        Transaccion transaccionEsperada = new Transaccion();
        transaccionEsperada.setCuentaOrigen(cuentaOrigen);
        transaccionEsperada.setCuentaDestino(cuentaDestino);
        transaccionEsperada.setMonto(monto);
        transaccionEsperada.setTipo("TRANSFERENCIA");
        transaccionEsperada.setFechaHoraTransaccion(null); // no verificar este campo

        // Configura los mocks para simular la recuperación y el guardado de los objetos.
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion transaccion = invocation.getArgument(0);
            transaccion.setFechaHoraTransaccion(LocalDateTime.now()); // establecer la fecha actual aquí
            return transaccion;
        });
        when(productosRepository.save(any(Productos.class))).thenAnswer(invocation -> {
            Productos producto = invocation.getArgument(0);
            return producto; // devolver el mismo objeto que se guarda
        });

        // Llama al método realizarTransaccion con los parámetros configurados.
        Transaccion resultado = transaccionServiceImp.realizarTransaccion(cuentaOrigenId, cuentaDestinoId, monto);

        //  Verifica que el resultado sea el esperado, incluyendo los saldos actualizados y la transacción guardada.
        assertEquals(cuentaOrigenId, resultado.getCuentaOrigen().getId());
        assertEquals(cuentaDestinoId, resultado.getCuentaDestino().getId());
        assertEquals(monto, resultado.getMonto());
        assertEquals("TRANSFERENCIA", resultado.getTipo());
        assertEquals(new BigDecimal("600.00"), resultado.getCuentaOrigen().getSaldo()); // saldo esperado después de la transferencia
        assertEquals(new BigDecimal("400.00"), resultado.getCuentaDestino().getSaldo()); // saldo esperado después de la transferencia

        // Verifica que el método save de transaccionRepository haya sido llamado con los valores correctos.
        verify(transaccionRepository).save(argThat(transaccion ->
                transaccion.getTipo().equals("TRANSFERENCIA") &&
                        transaccion.getMonto().equals(monto) &&
                        transaccion.getCuentaOrigen().equals(cuentaOrigen) &&
                        transaccion.getCuentaDestino().equals(cuentaDestino) &&
                        transaccion.getFechaHoraTransaccion() != null // solo verificar que no sea null
        ));
        verify(productosRepository).save(cuentaOrigen);
        verify(productosRepository).save(cuentaDestino);
    }

    /**
     * Este test verifica que el método realizarTransaccion lance una excepción
     * cuando se intenta realizar una transferencia que excede el saldo disponible en la cuenta de origen.
     * Se configura el saldo de la cuenta origen en 50.00 y se intenta transferir 100.00 a la cuenta destino.
     * Se espera que el método lance una IllegalArgumentException en este caso, ya que el saldo es insuficiente para la transferencia.
     */
    @Test
    void testRealizarTransaccionSaldoInsuficiente() {

        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("50.00")); // saldo insuficiente

        Productos cuentaDestino = new Productos();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setSaldo(new BigDecimal("300.00")); // saldo inicial

        // Configura el mock para simular la recuperación de las cuentas.
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.of(cuentaDestino));

        // Verifica que se lance una IllegalArgumentException al intentar realizar la transferencia con saldo insuficiente.
        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarTransaccion(cuentaOrigenId, cuentaDestinoId, monto);
        });
    }

    /**
     * Este test verifica que el método realizarTransaccion lance una excepción
     * cuando se intenta realizar una transferencia a una cuenta que no existe.
     * Se configura el mock para que findById devuelva un Optional vacío para la cuenta destino,
     * simulando que la cuenta destino no se encuentra.
     * Se espera que el método lance una IllegalArgumentException en este caso, ya que no se puede realizar la transferencia a una cuenta inexistente.
     */
    @Test
    void testRealizarTransaccionCuentaDestinoNoEncontrada() {

        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("100.00");

        Productos cuentaOrigen = new Productos();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setSaldo(new BigDecimal("700.00")); // saldo inicial

        // Configura el mock para simular la recuperación de la cuenta origen y un Optional vacío para la cuenta destino.
        when(productosRepository.findById(cuentaOrigenId)).thenReturn(Optional.of(cuentaOrigen));
        when(productosRepository.findById(cuentaDestinoId)).thenReturn(Optional.empty());

        // Act & Assert: Verifica que se lance una IllegalArgumentException al intentar realizar la transferencia cuando la cuenta destino no se encuentra.
        assertThrows(IllegalArgumentException.class, () -> {
            transaccionServiceImp.realizarTransaccion(cuentaOrigenId, cuentaDestinoId, monto);
        });
    }


}
