package com.example.demo.services.Implement;

import com.example.demo.entities.Clientes;
import com.example.demo.entities.Productos;
import com.example.demo.repository.ClientesRepository;
import com.example.demo.repository.ProductosRepository;
import com.example.demo.services.NumeroCuentaGenerator;
import com.example.demo.services.ProductosService;
import com.example.demo.validators.SaldoMinimoValidator;
import com.example.demo.validators.TipoCuentaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ProductosServiceImp implements ProductosService {
    @Autowired
    private ProductosRepository repository;
    @Autowired
    private ClientesRepository clientesRepository;
    @Autowired
    private TipoCuentaValidator tipoCuentaValidator;
    @Autowired
    private SaldoMinimoValidator saldoMinimoValidator;
    @Autowired
    private NumeroCuentaGenerator numeroCuentaGenerator;


    @Override
    public Productos createProducto(Long clienteId, Productos producto) {
        Clientes cliente = obtenerCliente(clienteId);
        producto.setCliente(cliente);

        tipoCuentaValidator.validate(producto.getTipoCuenta());
        numeroCuentaGenerator.generarYAsignarNumeroCuenta(producto);
        saldoMinimoValidator.validate(producto);
        producto.setFechaCreacion(LocalDateTime.now());
        establecerEstadoPredeterminado(producto);

        return repository.save(producto);
    }

    @Override
    public Productos updateProducto(Long id, Productos producto) {
        Productos productoExistente = obtenerProducto(id);
        tipoCuentaValidator.validate(producto.getTipoCuenta());
        saldoMinimoValidator.validate(producto);

        actualizarDatosProducto(productoExistente, producto);

        return repository.save(productoExistente);
    }


    @Override
    public void deleteProducto(Long id) {
        Productos producto = obtenerProducto(id);
        verificarSaldoCero(producto);
        repository.delete(producto);
    }


    @Override
    public Productos getProductoById(Long id) {
        return obtenerProducto(id);
    }


    @Override
    public Productos activarProducto(Long id) {
        Productos cuenta = obtenerProducto(id);

        cuenta.setEstado("activa");
        return repository.save(cuenta);
    }


    @Override
    public Productos desactivarProducto(Long id) {
        Productos cuenta = obtenerProducto(id);
        cuenta.setEstado("inactiva");
        return repository.save(cuenta);
    }


    @Override
    public String getEstadoProductoById(Long id) {
        Productos producto = obtenerProducto(id);
        return producto.getEstado();
    }


    private Clientes obtenerCliente(Long clienteId) {
        return clientesRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + clienteId));
    }


    private Productos obtenerProducto(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
    }


    private void verificarSaldoCero(Productos producto) {
        if (producto.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("No se puede cancelar la cuenta porque tiene un saldo diferente de $0.");
        }
    }


    private void actualizarDatosProducto(Productos productoExistente, Productos productoNuevo) {
        productoExistente.setTipoCuenta(productoNuevo.getTipoCuenta());
        productoExistente.setEstado(productoNuevo.getEstado());
        productoExistente.setSaldo(productoNuevo.getSaldo());
        productoExistente.setExentaGmf(productoNuevo.isExentaGmf());
        productoExistente.setFechaModificacion(LocalDateTime.now());
    }


    private void establecerEstadoPredeterminado(Productos producto) {
        if (producto.getTipoCuenta().equals("cuenta de ahorros") || producto.getTipoCuenta().equals("cuenta corriente")) {
            producto.setEstado("activa");
        }
    }
}


