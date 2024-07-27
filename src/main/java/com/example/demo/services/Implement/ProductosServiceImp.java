package com.example.demo.services.Implement;

import com.example.demo.entities.Clientes;
import com.example.demo.entities.Productos;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.ClientesRepository;
import com.example.demo.repository.ProductosRepository;
import com.example.demo.services.ProductosService;
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

    @Override
    public Productos createProducto(Long clienteId, Productos producto) {
        // Buscar el cliente por ID
        Clientes cliente = clientesRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + clienteId));

        // Asignar el cliente al producto
        producto.setCliente(cliente);

        // Validar que el tipo de cuenta sea válido
        validarTipoCuenta(producto.getTipoCuenta());

        // Generar automáticamente el número de cuenta y asegurar que sea único
        generarYAsignarNumeroCuenta(producto);

        // Validar saldo mínimo para cuenta de ahorros
        validarSaldoMinimo(producto);

        // Asignar fechas de creación
        producto.setFechaCreacion(LocalDateTime.now());


        // Asignar estado predeterminado para cuenta de ahorros
        if (producto.getTipoCuenta().equals("cuenta de ahorros") || producto.getTipoCuenta().equals("cuenta corriente")) {
            producto.setEstado("activa");
        }

        // Guardar el producto en la base de datos
        return repository.save(producto);
    }

    @Override
    public Productos updateProducto(Long id, Productos producto) {
        // Obtener el producto existente
        Productos productoExistente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));

        // Validar que el tipo de cuenta sea válido
        validarTipoCuenta(producto.getTipoCuenta());

        // Validar saldo mínimo para cuenta de ahorros
        validarSaldoMinimo(producto);

        // Actualizar los atributos del producto existente con los nuevos detalles
        productoExistente.setTipoCuenta(producto.getTipoCuenta());
        productoExistente.setEstado(producto.getEstado());
        productoExistente.setSaldo(producto.getSaldo());
        productoExistente.setExentaGmf(producto.isExentaGmf());
        productoExistente.setFechaModificacion(LocalDateTime.now());

        return repository.save(productoExistente);
    }

    @Override
    public void deleteProducto(Long id) {
        Productos producto = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        // Verificar que el saldo sea igual a $0 para poder eliminar la cuenta
        if (producto.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("No se puede cancelar la cuenta porque tiene un saldo diferente de $0.");
        }

        repository.delete(producto);
    }

    @Override
    public Productos getProductoById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El producto con el id " + id + " no existe."));
    }

    @Override
    public Productos activarProducto(Long id) {
        Productos cuenta = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));

        cuenta.setEstado("activa"); // O cualquier estado que necesites
        return repository.save(cuenta);
    }

    @Override
    public Productos desactivarProducto(Long id) {
        Productos cuenta = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));

        cuenta.setEstado("inactiva"); // O cualquier estado que necesites
        return repository.save(cuenta);
    }

    public void validarTipoCuenta(String tipoCuenta) {
        if (!tipoCuenta.equals("cuenta corriente") && !tipoCuenta.equals("cuenta de ahorros")) {
            throw new IllegalArgumentException("El tipo de cuenta debe ser 'cuenta corriente' o 'cuenta de ahorros'.");
        }
    }

    private void generarYAsignarNumeroCuenta(Productos producto) {
        String prefijo = "cuenta corriente".equals(producto.getTipoCuenta()) ? "33" : "53";
        producto.setNumeroCuenta(generarNumeroCuenta(prefijo));
    }

    private String generarNumeroCuenta(String prefijo) {
        String numeroCuenta;
        boolean existe;

        // Generar y validar que el número de cuenta sea único
        do {
            numeroCuenta = prefijo + generateRandomDigits(8);
            existe = repository.existsByNumeroCuenta(numeroCuenta);
        } while (existe);

        return numeroCuenta;
    }

    private String generateRandomDigits(int length) {
        // Método para generar dígitos aleatorios de la longitud especificada
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    private void validarSaldoMinimo(Productos producto) {
        if ("cuenta de ahorros".equals(producto.getTipoCuenta()) && producto.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cuenta de ahorros no puede tener un saldo menor a $0.");
        }
    }
    @Override
    public String getEstadoProductoById(Long id) {
        Productos producto = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
        return producto.getEstado();
    }
}

