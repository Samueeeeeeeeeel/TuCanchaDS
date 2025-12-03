package com.example.Login.config;

import com.example.Login.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    
    private final UsuarioService usuarioService;
    
    @PostConstruct
    public void init() {
        usuarioService.inicializarAdmin();
    }
}

