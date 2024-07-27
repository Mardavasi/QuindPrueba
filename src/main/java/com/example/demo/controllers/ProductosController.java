package com.example.demo.controllers;

import com.example.demo.entities.Clientes;
import com.example.demo.entities.Productos;
import com.example.demo.repository.ClientesRepository;
import com.example.demo.services.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductosController {
    @Autowired
    private ProductosService service;
    @Autowired
    private ClientesRepository clientesRepository;

    @PostMapping("/crear/{clienteId}")
    public Productos createProducto(@PathVariable Long clienteId, @RequestBody Productos producto) {
        // Buscar el cliente por su ID
        Clientes cliente = clientesRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));
        // Asignar el cliente al producto
        producto.setCliente(cliente);
        // Llamar al servicio para crear el producto
        return service.createProducto(clienteId, producto);
    }

    @PutMapping("/productos/{id}")
    public void updateProducto(@PathVariable Long id, @RequestBody Productos productoDetails) {
        service.updateProducto(id, productoDetails);
    }
    @DeleteMapping("/productos/{id}")
    public void deleteProducto(@PathVariable Long id) {
        service.deleteProducto(id);
    }
    @GetMapping("/productos/{id}")
    public Productos getProductoById(@PathVariable Long id) {
        return service.getProductoById(id);
    }

    @PutMapping("/productos/{id}/activar")
    public Productos activarProducto(@PathVariable Long id) {

        return   service.activarProducto(id);
    }
    @PutMapping("/productos/{id}/desactivar")
    public Productos desactivarProducto(@PathVariable Long id) {
        return service.desactivarProducto(id);
    }

    @GetMapping("/{id}/estado")
    public String getEstadoProductoById(@PathVariable Long id) {
        return service.getEstadoProductoById(id);
    }
}
