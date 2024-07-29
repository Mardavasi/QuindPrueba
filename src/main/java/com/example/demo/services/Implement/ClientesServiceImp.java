package com.example.demo.services.Implement;

import com.example.demo.validators.ClientesValidator;
import com.example.demo.entities.Clientes;
import com.example.demo.repository.ClientesRepository;
import com.example.demo.services.ClientesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClientesServiceImp implements ClientesService {
    @Autowired
    private ClientesRepository repository;
    @Autowired
    private ClientesValidator clientesValidator;
    @Override
    public Clientes getClienteById(Long id) {
        return repository.findById(id)
                .orElseThrow();
    }

    @Override
    public Clientes createCliente(Clientes cliente) {
        int edad = ClientesValidator.calcularEdad(cliente.getFechaNacimiento());
        cliente.setEdad(edad);
        ClientesValidator.validateCliente(cliente);
        return repository.save(cliente);
    }




    @Override
    public Clientes updateCliente(Long id, Clientes clienteDetails) {
        Clientes clienteExistente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + id));
        int edad = ClientesValidator.calcularEdad(clienteDetails.getFechaNacimiento());
        clienteExistente.setNombres(clienteDetails.getNombres());
        clienteExistente.setApellidos(clienteDetails.getApellidos());
        clienteExistente.setCorreoElectronico(clienteDetails.getCorreoElectronico());
        clienteExistente.setFechaNacimiento(clienteDetails.getFechaNacimiento());
        clienteExistente.setEdad(edad);
        clienteExistente.setFechaModificacion(LocalDateTime.now());

        ClientesValidator.validateCliente(clienteExistente);

        return repository.save(clienteExistente);
    }



    @Override
    public void deleteCliente(Long id) {
        Clientes cliente = repository.findById(id)
                .orElseThrow();
        if (!cliente.getProductos().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el cliente porque tiene productos vinculados.");
        }

        repository.delete(cliente);
    }
}
