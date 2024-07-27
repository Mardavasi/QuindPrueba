package com.example.demo.validators;

import com.example.demo.entities.Clientes;
import com.example.demo.exceptions.InvalidAgeException;
import com.example.demo.exceptions.InvalidEmailException;
import com.example.demo.exceptions.InvalidNameException;

import java.util.regex.Pattern;

public class ClientesValidator {
    public static void validateCliente(Clientes cliente) {
        validateAge(cliente.getEdad());
        validateEmail(cliente.getCorreoElectronico());
        validateName(cliente.getNombres(), cliente.getApellidos());
    }

    public static void validateAge(int edad) {
        if (edad < 18) {
            throw new InvalidAgeException("El cliente debe ser mayor de edad.");
        }
    }

    public static void validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null || !pat.matcher(email).matches()) {
            throw new InvalidEmailException("El correo electrónico no es válido.");
        }
    }

    public static void validateName(String nombres, String apellidos) {
        if (nombres == null || nombres.length() < 2 || apellidos == null || apellidos.length() < 2) {
            throw new InvalidNameException("El nombre y el apellido deben tener al menos 2 caracteres.");
        }
    }
}
