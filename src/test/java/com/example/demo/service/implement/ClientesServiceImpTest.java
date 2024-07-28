package com.example.demo.service.implement;

import com.example.demo.entities.Clientes;
import com.example.demo.entities.Productos;
import com.example.demo.repository.ClientesRepository;
import com.example.demo.validators.ClientesValidator;
import com.example.demo.services.Implement.ClientesServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientesServiceImpTest {

    @InjectMocks
    private ClientesServiceImp clientesServiceImp;

    @Mock
    private ClientesRepository clientesRepository;

    @Mock
    private ClientesValidator clientesValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Prueba para verificar la creación de un cliente.
     * Se asegura que el cliente se cree correctamente y que se apliquen las validaciones necesarias.
     */
    @Test
    void testCreateCliente() {
        // Arrange
        Clientes cliente = new Clientes();
        cliente.setNombres("Juan");
        cliente.setApellidos("Perez");
        cliente.setEdad(31);
        cliente.setCorreoElectronico("juan.perez@newdomain.com");
        cliente.setFechaNacimiento(LocalDate.of(1992, 7, 27));
        cliente.setFechaCreacion(LocalDateTime.now());
        cliente.setFechaModificacion(LocalDateTime.now());

        // Act
        clientesServiceImp.createCliente(cliente);

        // Assert
        verify(clientesValidator).validateCliente(cliente);
    }


    /**
     * Prueba para verificar la eliminación de un cliente.
     * Se asegura que el cliente se elimine correctamente si no tiene productos vinculados.
     */
    @Test
    void testDeleteCliente() {
        Long clienteId = 1L;
        Clientes cliente = new Clientes();
        cliente.setProductos(java.util.Collections.emptyList());

        when(clientesRepository.findById(clienteId)).thenReturn(java.util.Optional.of(cliente));
        doNothing().when(clientesRepository).delete(cliente);

        assertDoesNotThrow(() -> clientesServiceImp.deleteCliente(clienteId));

        verify(clientesRepository).findById(clienteId);
        verify(clientesRepository).delete(cliente);
    }

    /**
     * Prueba para verificar que no se puede eliminar un cliente con productos vinculados.
     * Se asegura que se lance una excepción con el mensaje adecuado si se intenta eliminar un cliente con productos vinculados.
     */
    @Test
    void testDeleteClienteConProductos() {
        Long clienteId = 1L;
        Clientes cliente = new Clientes();
        cliente.setProductos(java.util.Collections.singletonList(new Productos()));

        when(clientesRepository.findById(clienteId)).thenReturn(java.util.Optional.of(cliente));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> clientesServiceImp.deleteCliente(clienteId));
        assertEquals("No se puede eliminar el cliente porque tiene productos vinculados.", thrown.getMessage());
    }

    /**
     * Prueba para verificar la obtención de un cliente por su ID.
     * Se asegura que se retorne el cliente correcto con el ID dado.
     */
    @Test
    void testGetClienteById() {
        Long clienteId = 1L;
        Clientes cliente = new Clientes();
        cliente.setId(clienteId);

        when(clientesRepository.findById(clienteId)).thenReturn(java.util.Optional.of(cliente));

        Clientes result = clientesServiceImp.getClienteById(clienteId);

        assertNotNull(result);
        assertEquals(clienteId, result.getId());
    }
    @Test
    void testActualizarClienteExitoso() {
        // Arrange
        Long clienteId = 1L;
        Clientes clienteExistente = new Clientes();
        clienteExistente.setId(clienteId);
        clienteExistente.setNombres("Juan");
        clienteExistente.setApellidos("Pérez");
        clienteExistente.setCorreoElectronico("juan.perez@example.com");
        clienteExistente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        clienteExistente.setEdad(32);
        clienteExistente.setFechaCreacion(LocalDateTime.now());

        Clientes clienteDetails = new Clientes();
        clienteDetails.setNombres("Carlos");
        clienteDetails.setApellidos("Lopez");
        clienteDetails.setCorreoElectronico("carlos.lopez@example.com");
        clienteDetails.setFechaNacimiento(LocalDate.of(1992, 5, 20));

        when(clientesRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(clientesRepository.save(any(Clientes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Clientes updatedCliente = clientesServiceImp.updateCliente(clienteId, clienteDetails);

        // Assert
        assertNotNull(updatedCliente);
        assertEquals("Carlos", updatedCliente.getNombres());
        assertEquals("Lopez", updatedCliente.getApellidos());
        assertEquals("carlos.lopez@example.com", updatedCliente.getCorreoElectronico());
        assertEquals(LocalDate.of(1992, 5, 20), updatedCliente.getFechaNacimiento());
        assertEquals(ClientesValidator.calcularEdad(LocalDate.of(1992, 5, 20)), updatedCliente.getEdad());
        assertNotNull(updatedCliente.getFechaModificacion());

        verify(clientesRepository).findById(clienteId);
        verify(clientesRepository).save(clienteExistente);
    }

    @Test
    void testActualizarClienteNoCambiaEdadSiNoCambiaFechaNacimiento() {
        // Arrange
        Long clienteId = 1L;
        Clientes clienteExistente = new Clientes();
        clienteExistente.setId(clienteId);
        clienteExistente.setNombres("Juan");
        clienteExistente.setApellidos("Pérez");
        clienteExistente.setCorreoElectronico("juan.perez@example.com");
        clienteExistente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        clienteExistente.setEdad(32);
        clienteExistente.setFechaCreacion(LocalDateTime.now());

        Clientes clienteDetails = new Clientes();
        clienteDetails.setNombres("Carlos");
        clienteDetails.setApellidos("Lopez");
        clienteDetails.setCorreoElectronico("carlos.lopez@example.com");
        clienteDetails.setFechaNacimiento(LocalDate.of(1990, 1, 1)); // Misma fecha de nacimiento

        when(clientesRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(clientesRepository.save(any(Clientes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Clientes updatedCliente = clientesServiceImp.updateCliente(clienteId, clienteDetails);

        // Assert
        assertNotNull(updatedCliente);
        assertEquals(clienteExistente.getEdad(), updatedCliente.getEdad()); // La edad no cambia
        assertNotNull(updatedCliente.getFechaModificacion());

        verify(clientesRepository).findById(clienteId);
        verify(clientesRepository).save(clienteExistente);
    }

    @Test
    void testActualizarClienteNoEncontrado() {
        // Arrange
        Long clienteId = 1L;
        Clientes clienteDetails = new Clientes();

        when(clientesRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> clientesServiceImp.updateCliente(clienteId, clienteDetails));

        verify(clientesRepository).findById(clienteId);
        verify(clientesRepository, never()).save(any(Clientes.class));
    }

}
