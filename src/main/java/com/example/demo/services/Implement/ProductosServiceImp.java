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

    /**
     * Crea un nuevo producto asociándolo con un cliente y realiza validaciones.
     */
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

    /**
     * Actualiza un producto existente con los nuevos datos proporcionados.
     */
    @Override
    public Productos updateProducto(Long id, Productos producto) {
        Productos productoExistente = obtenerProducto(id);
        tipoCuentaValidator.validate(producto.getTipoCuenta());
        saldoMinimoValidator.validate(producto);

        actualizarDatosProducto(productoExistente, producto);

        return repository.save(productoExistente);
    }

    /**
     * Elimina un producto si su saldo es cero.
     */
    @Override
    public void deleteProducto(Long id) {
        Productos producto = obtenerProducto(id);
        verificarSaldoCero(producto);
        repository.delete(producto);
    }

    /**
     * Obtiene un producto por su ID.
     */
    @Override
    public Productos getProductoById(Long id) {
        return obtenerProducto(id);
    }

    /**
     * Activa un producto cambiando su estado a "activa".
     */
    @Override
    public Productos activarProducto(Long id) {
        Productos cuenta = obtenerProducto(id);
        cuenta.setEstado("activa");
        return repository.save(cuenta);
    }

    /**
     * Desactiva un producto cambiando su estado a "inactiva".
     */
    @Override
    public Productos desactivarProducto(Long id) {
        Productos cuenta = obtenerProducto(id);
        cuenta.setEstado("inactiva");
        return repository.save(cuenta);
    }

    /**
     * Obtiene el estado de un producto por su ID.
     */
    @Override
    public String getEstadoProductoById(Long id) {
        Productos producto = obtenerProducto(id);
        return producto.getEstado();
    }

    /**
     * Obtiene un cliente por su ID o lanza una excepción si no se encuentra, para evitar la repetición de código al
     * crear, actualizar o eliminar productos.
     */
    private Clientes obtenerCliente(Long clienteId) {
        return clientesRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + clienteId));
    }

    /**
     * Obtiene un producto por su ID para pasarlo a metodos como getproductoById que es usado en el controlador.
     */
    private Productos obtenerProducto(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
    }

    /**
     * Verifica que el saldo de un producto sea cero antes de eliminarlo.
     */
    private void verificarSaldoCero(Productos producto) {
        if (producto.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("No se puede cancelar la cuenta porque tiene un saldo diferente de $0.");
        }
    }

    /**
     * Actualiza los datos de un producto existente con los datos nuevos.
     */
    private void actualizarDatosProducto(Productos productoExistente, Productos productoNuevo) {
        productoExistente.setTipoCuenta(productoNuevo.getTipoCuenta());
        productoExistente.setEstado(productoNuevo.getEstado());
        productoExistente.setSaldo(productoNuevo.getSaldo());
        productoExistente.setExentaGmf(productoNuevo.isExentaGmf());
        productoExistente.setFechaModificacion(LocalDateTime.now());
    }

    /**
     * Establece el estado predeterminado del producto basado en el tipo de cuenta.
     */
    private void establecerEstadoPredeterminado(Productos producto) {
        if (producto.getTipoCuenta().equals("cuenta de ahorros") || producto.getTipoCuenta().equals("cuenta corriente")) {
            producto.setEstado("activa");
        }
    }
}


