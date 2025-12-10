package com.example.Canchas.config;

import com.example.Canchas.service.CanchaService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos que se ejecuta automáticamente al iniciar el microservicio
 * Crea las 4 canchas de fútbol por defecto si no existen
 */
@Component
@RequiredArgsConstructor
public class DataInitializer {
    
    private final CanchaService canchaService;
    
    @PostConstruct
    public void init() {
        canchaService.inicializarCanchas();
    }
}
