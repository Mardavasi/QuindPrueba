package com.example.demo.repository;

import com.example.demo.entities.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductosRepository extends JpaRepository<Productos, Long> {
    boolean existsByNumeroCuenta(String numeroCuenta);

}
