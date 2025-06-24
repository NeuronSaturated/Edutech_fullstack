package com.edutech.Edutech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 🔓 Permite todas las rutas sin autenticación
            )
            .httpBasic(httpBasic -> httpBasic.disable()) // ❌ Desactiva autenticación básica
            .formLogin(form -> form.disable()); // ❌ Desactiva el formulario de login

        return http.build();
    }
}

