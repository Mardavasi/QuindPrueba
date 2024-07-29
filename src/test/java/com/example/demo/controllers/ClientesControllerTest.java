package com.example.demo.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.entities.Clientes;
import com.example.demo.services.ClientesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClientesController.class)
public class ClientesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientesService service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateCliente() throws Exception {
        Clientes cliente = new Clientes();
        when(service.createCliente(any(Clientes.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk());

        verify(service, times(1)).createCliente(any(Clientes.class));
    }

    @Test
    void testUpdateCliente() throws Exception {
        Long clienteId = 1L;
        Clientes clienteDetails = new Clientes();
        Clientes updatedCliente = new Clientes();

        when(service.updateCliente(eq(clienteId), any(Clientes.class))).thenReturn(updatedCliente);

        mockMvc.perform(put("/api/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDetails)))
                .andExpect(status().isOk());

        verify(service, times(1)).updateCliente(eq(clienteId), any(Clientes.class));
    }
    @Test
    void testDeleteCliente() throws Exception {
        Long clienteId = 1L;

        doNothing().when(service).deleteCliente(clienteId);

        mockMvc.perform(delete("/api/clientes/{id}", clienteId))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteCliente(clienteId);
    }
    @Test
    void testGetClienteById() throws Exception {
        Long clienteId = 1L;
        Clientes cliente = new Clientes();
        when(service.getClienteById(clienteId)).thenReturn(cliente);

        mockMvc.perform(get("/api/clientes/{id}", clienteId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(cliente.getId()))
        ;

        verify(service, times(1)).getClienteById(clienteId);
    }


}

