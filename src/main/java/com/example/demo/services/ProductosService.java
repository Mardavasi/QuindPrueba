package com.example.demo.services;

import com.example.demo.entities.Productos;

public interface ProductosService {
    Productos createProducto(Long clienteId, Productos producto);;
    Productos updateProducto( Long id, Productos productoDetails);
    void deleteProducto( Long id);
    Productos getProductoById( Long id);
    Productos activarProducto(Long id);
    Productos desactivarProducto(Long id);
    String getEstadoProductoById(Long id);

}
