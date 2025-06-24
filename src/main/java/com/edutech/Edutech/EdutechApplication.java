package com.edutech.Edutech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Anotación que indica que esta clase es la aplicación principal de Spring Boot
@SpringBootApplication
public class EdutechApplication {

    // Método principal que se ejecuta al iniciar la aplicación
    public static void main(String[] args) {
        // Arranca el contexto de Spring Boot y lanza la aplicación
        SpringApplication.run(EdutechApplication.class, args);
    }
}
