package com.example.demo.service.implement;

import com.example.demo.entities.Clientes;
import com.example.demo.entities.Productos;
import com.example.demo.repository.ClientesRepository;
import com.example.demo.repository.ProductosRepository;
import com.example.demo.services.Implement.ProductosServiceImp;
import com.example.demo.services.NumeroCuentaGenerator;
import com.example.demo.validators.SaldoMinimoValidator;
import com.example.demo.validators.TipoCuentaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductosServiceImpTest {

    @InjectMocks
    private ProductosServiceImp productosServiceImp;

    @Mock
    private ProductosRepository productosRepository;

    @Mock
    private ClientesRepository clientesRepository;

    @Mock
    private TipoCuentaValidator tipoCuentaValidator;

    @Mock
    private SaldoMinimoValidator saldoMinimoValidator;

    @Mock
    private NumeroCuentaGenerator numeroCuentaGenerator;

    // Inicializa los mocks antes de cada prueba
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCreateProducto_CuentaAhorros() {
        Long clienteId = 1L;
        Clientes cliente = new Clientes();
        cliente.setId(clienteId);

        Productos producto = new Productos();
        producto.setTipoCuenta("cuenta de ahorros");
        producto.setSaldo(BigDecimal.TEN);
        producto.setFechaCreacion(LocalDateTime.now());

        when(clientesRepository.findById(clienteId)).thenReturn(java.util.Optional.of(cliente));
        when(productosRepository.save(any(Productos.class))).thenReturn(producto);

        Productos result = productosServiceImp.createProducto(clienteId, producto);

        assertNotNull(result);
        assertEquals(cliente, result.getCliente());
        assertEquals("cuenta de ahorros", result.getTipoCuenta());
        assertEquals(BigDecimal.TEN, result.getSaldo());
        assertEquals("activa", result.getEstado());

        verify(tipoCuentaValidator).validate("cuenta de ahorros");
        verify(numeroCuentaGenerator).generarYAsignarNumeroCuenta(producto);
        verify(saldoMinimoValidator).validate(producto);
        verify(productosRepository).save(producto);
    }

    @Test
    void testCreateProducto_CuentaCorriente() {
        Long clienteId = 1L;
        Clientes cliente = new Clientes();
        cliente.setId(clienteId);

        Productos producto = new Productos();
        producto.setTipoCuenta("cuenta corriente");
        producto.setSaldo(BigDecimal.TEN);
        producto.setFechaCreacion(LocalDateTime.now());

        when(clientesRepository.findById(clienteId)).thenReturn(java.util.Optional.of(cliente));
        when(productosRepository.save(any(Productos.class))).thenReturn(producto);

        Productos result = productosServiceImp.createProducto(clienteId, producto);

        assertNotNull(result);
        assertEquals(cliente, result.getCliente());
        assertEquals("cuenta corriente", result.getTipoCuenta());
        assertEquals(BigDecimal.TEN, result.getSaldo());
        assertEquals("activa", result.getEstado());

        verify(tipoCuentaValidator).validate("cuenta corriente");
        verify(numeroCuentaGenerator).generarYAsignarNumeroCuenta(producto);
        verify(saldoMinimoValidator).validate(producto);
        verify(productosRepository).save(producto);
    }

    @Test
    void testCreateProducto_SaldoNegativo() {
        Productos producto = new Productos();
        producto.setSaldo(new BigDecimal("-100"));
        producto.setTipoCuenta("cuenta de ahorros");
        SaldoMinimoValidator saldoMinimoValidator = new SaldoMinimoValidator();
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> saldoMinimoValidator.validate(producto),
                "Se esperaba que se lanzara una IllegalArgumentException debido a saldo negativo."
        );

        assertEquals("La cuenta de ahorros no puede tener un saldo menor a 0.", thrown.getMessage());
    }




    @Test
    void testUpdateProducto() {

        Long productoId = 1L;
        Productos productoExistente = new Productos();
        productoExistente.setTipoCuenta("cuenta corriente");
        productoExistente.setEstado("inactiva");
        productoExistente.setSaldo(BigDecimal.ZERO);

        Productos productoNuevo = new Productos();
        productoNuevo.setTipoCuenta("cuenta de ahorros");
        productoNuevo.setEstado("activa");
        productoNuevo.setSaldo(BigDecimal.TEN);

        when(productosRepository.findById(productoId)).thenReturn(java.util.Optional.of(productoExistente));
        when(productosRepository.save(any(Productos.class))).thenReturn(productoExistente);


        Productos result = productosServiceImp.updateProducto(productoId, productoNuevo);


        assertNotNull(result);
        assertEquals(productoNuevo.getTipoCuenta(), result.getTipoCuenta());
        assertEquals(productoNuevo.getEstado(), result.getEstado());
        assertEquals(productoNuevo.getSaldo(), result.getSaldo());
        verify(tipoCuentaValidator).validate("cuenta de ahorros");
        verify(saldoMinimoValidator).validate(productoNuevo);
        verify(productosRepository).save(productoExistente);
    }


    @Test
    void testDeleteProducto() {

        Long productoId = 1L;
        Productos producto = new Productos();
        producto.setSaldo(BigDecimal.ZERO);
        when(productosRepository.findById(productoId)).thenReturn(java.util.Optional.of(producto));
        doNothing().when(productosRepository).delete(producto);
        assertDoesNotThrow(() -> productosServiceImp.deleteProducto(productoId));
        verify(productosRepository).delete(producto);
    }


    @Test
    void testDeleteProductoSaldoNoCero() {

        Long productoId = 1L;
        Productos producto = new Productos();
        producto.setSaldo(BigDecimal.TEN);

        when(productosRepository.findById(productoId)).thenReturn(java.util.Optional.of(producto));


        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> productosServiceImp.deleteProducto(productoId));
        assertEquals("No se puede cancelar la cuenta porque tiene un saldo diferente de $0.", thrown.getMessage());
    }


    @Test
    void testGetProductoById() {
        Long productoId = 1L;
        Productos producto = new Productos();
        producto.setId(productoId);
        when(productosRepository.findById(productoId)).thenReturn(java.util.Optional.of(producto));
        Productos result = productosServiceImp.getProductoById(productoId);
        assertNotNull(result);
        assertEquals(productoId, result.getId());
    }


    @Test
    void testActivarProducto() {
        Long productoId = 1L;
        Productos producto = new Productos();
        producto.setEstado("inactiva");
        when(productosRepository.findById(productoId)).thenReturn(java.util.Optional.of(producto));
        when(productosRepository.save(any(Productos.class))).thenReturn(producto);
        Productos result = productosServiceImp.activarProducto(productoId);
        assertNotNull(result);
        assertEquals("activa", result.getEstado());
        verify(productosRepository).save(producto);
    }


    @Test
    void testDesactivarProducto() {
        Long productoId = 1L;
        Productos producto = new Productos();
        producto.setEstado("activa");
        when(productosRepository.findById(productoId)).thenReturn(java.util.Optional.of(producto));
        when(productosRepository.save(any(Productos.class))).thenReturn(producto);
        Productos result = productosServiceImp.desactivarProducto(productoId);
        assertNotNull(result);
        assertEquals("inactiva", result.getEstado());
        verify(productosRepository).save(producto);
    }


    @Test
    void testGetEstadoProductoById() {
        Long productoId = 1L;
        Productos producto = new Productos();
        producto.setEstado("activa");
        when(productosRepository.findById(productoId)).thenReturn(java.util.Optional.of(producto));
        String estado = productosServiceImp.getEstadoProductoById(productoId);
        assertEquals("activa", estado);
    }
}
