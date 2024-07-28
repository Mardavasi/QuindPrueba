package com.example.demo.validators;

import com.example.demo.entities.Clientes;
import com.example.demo.exceptions.InvalidAgeException;
import com.example.demo.exceptions.InvalidEmailException;
import com.example.demo.exceptions.InvalidNameException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;
@Component
public class ClientesValidator {
    public static void validateCliente(Clientes cliente) {
        // Validación de la edad basada en la fecha de nacimiento
        if (cliente.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula.");
        }

        int edad = calcularEdad(cliente.getFechaNacimiento());
        validateAge(edad);

        // Validación del correo electrónico
        validateEmail(cliente.getCorreoElectronico());

        // Validación del nombre y apellidos
        validateName(cliente.getNombres(), cliente.getApellidos());
    }

    public static void validateAge(int edad) {
        if (edad < 18) {
            throw new InvalidAgeException("El cliente debe ser mayor de edad.");
        }
    }
    // Método para calcular la edad a partir de la fecha de nacimiento
    public static int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
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
