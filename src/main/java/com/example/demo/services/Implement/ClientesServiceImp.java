package com.example.demo.services.Implement;

import com.example.demo.Utils.ValidationUtils;
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

    @Override
    public Clientes getClienteById(Long id) {
        return repository.findById(id)
                .orElseThrow();
    }

    @Override
    public Clientes createCliente(Clientes cliente) {
        // Validar el cliente
        ValidationUtils.validateCliente(cliente);

        // Si las validaciones son correctas, crear el cliente
        return repository.save(cliente);
    }



    @Override
    public Clientes updateCliente(Long id, Clientes clienteDetails) {
        // Obtener el cliente existente por su ID
        Clientes clienteExistente = repository.findById(id)
                .orElseThrow();

        // Validar los detalles del cliente antes de actualizar
        ValidationUtils.validateCliente(clienteDetails);

        // Actualizar los campos necesarios
        clienteExistente.setNombres(clienteDetails.getNombres());
        clienteExistente.setApellidos(clienteDetails.getApellidos());
        clienteExistente.setCorreoElectronico(clienteDetails.getCorreoElectronico());
        clienteExistente.setEdad(clienteDetails.getEdad());
        clienteExistente.setFechaNacimiento(clienteDetails.getFechaNacimiento());
        clienteExistente.setFechaModificacion(LocalDateTime.now());

        // Guardar y devolver el cliente actualizado
        return repository.save(clienteExistente);
    }


    @Override
    public void deleteCliente(Long id) {
        Clientes cliente = repository.findById(id)
                .orElseThrow();

        // Verificar si el cliente tiene productos vinculados
        if (!cliente.getProductos().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el cliente porque tiene productos vinculados.");
        }

        repository.delete(cliente);
    }
}
