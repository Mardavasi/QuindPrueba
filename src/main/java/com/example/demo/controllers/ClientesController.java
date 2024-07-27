package com.example.demo.controllers;

import com.example.demo.entities.Clientes;
import com.example.demo.services.ClientesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api")
public class ClientesController {
    @Autowired
    private ClientesService service;

    @PostMapping("/clientes")
    public void createCliente(@RequestBody Clientes cliente) {
        service.createCliente(cliente);
    }
    @PutMapping("/clientes/{id}")
    public void updateCliente(@PathVariable Long id, @RequestBody Clientes clienteDetails) {
        service.updateCliente(id, clienteDetails);
    }
    @DeleteMapping("/clientes/{id}")
    public void deleteCliente(@PathVariable Long id) {
        service.deleteCliente(id);
    }
    @GetMapping("/clientes/{id}")
    public Clientes getClienteById(@PathVariable Long id) {
        return service.getClienteById(id);
    }

}
