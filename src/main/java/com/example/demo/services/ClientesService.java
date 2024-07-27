package com.example.demo.services;

import com.example.demo.entities.Clientes;

public interface ClientesService {
    Clientes getClienteById(Long id);
    Clientes createCliente(Clientes cliente);
    Clientes updateCliente(Long id, Clientes clienteDetails);
    void deleteCliente(Long id);

}
