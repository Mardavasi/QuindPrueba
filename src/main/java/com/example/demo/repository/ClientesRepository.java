package com.example.demo.repository;

import com.example.demo.entities.Clientes;
import org.springframework.data.repository.CrudRepository;

public interface ClientesRepository extends CrudRepository<Clientes,Long> {
}
