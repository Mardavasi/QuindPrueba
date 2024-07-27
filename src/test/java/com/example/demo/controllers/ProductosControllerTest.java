package com.example.demo.controllers;

import com.example.demo.entities.Clientes;
import com.example.demo.entities.Productos;
import com.example.demo.repository.ClientesRepository;
import com.example.demo.services.ProductosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductosController.class)
public class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductosService productosService;

    @MockBean
    private ClientesRepository clientesRepository;

    private Clientes cliente;
    private Productos producto;

    @BeforeEach
    void setUp() {
        cliente = new Clientes();
        cliente.setId(1L);
        cliente.setNombre("Juan Perez");
        cliente.setEmail("juan.perez@example.com");

        producto = new Productos();
        producto.setId(1L);
        producto.setCliente(cliente);
        producto.setTipoCuenta("cuenta de ahorros");
        producto.setSaldo(new BigDecimal("1000"));
    }

    @Test
    void testCreateProducto() throws Exception {
        when(clientesRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(productosService.createProducto(any(Long.class), any(Productos.class))).thenReturn(producto);

        mockMvc.perform(post("/api/crear/{clienteId}", cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"tipoCuenta\": \"cuenta de ahorros\", \"saldo\": 1000 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.tipoCuenta").value(producto.getTipoCuenta()))
                .andExpect(jsonPath("$.saldo").value(producto.getSaldo()));
    }

    @Test
    void testUpdateProducto() throws Exception {
        Productos updatedProducto = new Productos();
        updatedProducto.setTipoCuenta("cuenta corriente");

        mockMvc.perform(put("/api/productos/{id}", producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"tipoCuenta\": \"cuenta corriente\" }"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/{id}", producto.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProductoById() throws Exception {
        when(productosService.getProductoById(producto.getId())).thenReturn(producto);

        mockMvc.perform(get("/api/productos/{id}", producto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.tipoCuenta").value(producto.getTipoCuenta()));
    }

    @Test
    void testActivarProducto() throws Exception {
        when(productosService.activarProducto(producto.getId())).thenReturn(producto);

        mockMvc.perform(put("/api/productos/{id}/activar", producto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.tipoCuenta").value(producto.getTipoCuenta()));
    }

    @Test
    void testDesactivarProducto() throws Exception {
        when(productosService.desactivarProducto(producto.getId())).thenReturn(producto);

        mockMvc.perform(put("/api/productos/{id}/desactivar", producto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.tipoCuenta").value(producto.getTipoCuenta()));
    }

    @Test
    void testGetEstadoProductoById() throws Exception {
        String estado = "Activo";
        when(productosService.getEstadoProductoById(producto.getId())).thenReturn(estado);

        mockMvc.perform(get("/api/{id}/estado", producto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(estado));
    }
}