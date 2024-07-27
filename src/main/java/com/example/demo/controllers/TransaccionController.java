package com.example.demo.controllers;

import com.example.demo.dto.TransaccionDTO;
import com.example.demo.entities.Transaccion;
import com.example.demo.services.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaccion")
public class TransaccionController {
    @Autowired
    private TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
    }

    @PostMapping("/realizar")
    public Transaccion realizarTransaccion(@RequestBody TransaccionDTO transaccionDTO) {
        return transaccionService.realizarTransaccion(
                transaccionDTO.getCuentaOrigenId(),
                transaccionDTO.getCuentaDestinoId(),
                transaccionDTO.getMonto());
    }

    @PostMapping("/retirar")
    public Transaccion realizarRetiro(@RequestBody TransaccionDTO transaccionDTO) {
        return transaccionService.realizarRetiro(
                transaccionDTO.getCuentaOrigenId(),
                transaccionDTO.getMonto());
    }

    @PostMapping("/depositar")
    public Transaccion realizarDeposito(@RequestBody TransaccionDTO transaccionDTO) {
        return transaccionService.realizarDeposito(
                transaccionDTO.getCuentaDestinoId(),
                transaccionDTO.getMonto());
    }
}
