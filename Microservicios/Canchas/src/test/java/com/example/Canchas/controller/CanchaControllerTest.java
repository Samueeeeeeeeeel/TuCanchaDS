package com.example.Canchas.controller;

import com.example.Canchas.model.Cancha;
import com.example.Canchas.service.CanchaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CanchaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CanchaService canchaService;

    @InjectMocks
    private CanchaController canchaController;

    private ObjectMapper objectMapper;

    private Cancha cancha;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(canchaController).build();
        
        cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNombre("Cancha de Fútbol 1");
        cancha.setDescripcion("Cancha de fútbol 11");
        cancha.setTipo("Fútbol");
        cancha.setPrecioPorHora(50000);
        cancha.setDireccion("Calle 123");
        cancha.setCiudad("Bogotá");
        cancha.setActiva(true);
    }

    @Test
    void testObtenerTodasLasCanchas() throws Exception {
        // Arrange
        List<Cancha> canchas = Arrays.asList(cancha);
        when(canchaService.obtenerTodasLasCanchas()).thenReturn(canchas);

        // Act & Assert
        mockMvc.perform(get("/api/canchas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Cancha de Fútbol 1"));

        verify(canchaService, times(1)).obtenerTodasLasCanchas();
    }

    @Test
    void testObtenerCanchasActivas() throws Exception {
        // Arrange
        List<Cancha> canchas = Arrays.asList(cancha);
        when(canchaService.obtenerCanchasActivas()).thenReturn(canchas);

        // Act & Assert
        mockMvc.perform(get("/api/canchas/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].activa").value(true));

        verify(canchaService, times(1)).obtenerCanchasActivas();
    }

    @Test
    void testObtenerCanchaPorId_Existe() throws Exception {
        // Arrange
        when(canchaService.obtenerCanchaPorId(1L)).thenReturn(Optional.of(cancha));

        // Act & Assert
        mockMvc.perform(get("/api/canchas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Cancha de Fútbol 1"));

        verify(canchaService, times(1)).obtenerCanchaPorId(1L);
    }

    @Test
    void testObtenerCanchaPorId_NoExiste() throws Exception {
        // Arrange
        when(canchaService.obtenerCanchaPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/canchas/999"))
                .andExpect(status().isNotFound());

        verify(canchaService, times(1)).obtenerCanchaPorId(999L);
    }

    @Test
    void testObtenerCanchasPorTipo() throws Exception {
        // Arrange
        List<Cancha> canchas = Arrays.asList(cancha);
        when(canchaService.obtenerCanchasPorTipo("Fútbol")).thenReturn(canchas);

        // Act & Assert
        mockMvc.perform(get("/api/canchas/tipo/Fútbol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tipo").value("Fútbol"));

        verify(canchaService, times(1)).obtenerCanchasPorTipo("Fútbol");
    }

    @Test
    void testCrearCancha() throws Exception {
        // Arrange
        Cancha nuevaCancha = new Cancha();
        nuevaCancha.setNombre("Nueva Cancha");
        nuevaCancha.setTipo("Básquet");
        nuevaCancha.setPrecioPorHora(40000);
        nuevaCancha.setDireccion("Calle Nueva");
        nuevaCancha.setCiudad("Cali");
        nuevaCancha.setActiva(true);

        when(canchaService.crearCancha(any(Cancha.class))).thenAnswer(invocation -> {
            Cancha c = invocation.getArgument(0);
            c.setId(2L);
            return c;
        });

        // Act & Assert
        mockMvc.perform(post("/api/canchas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaCancha)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nueva Cancha"));

        verify(canchaService, times(1)).crearCancha(any(Cancha.class));
    }

    @Test
    void testActualizarCancha_Exitoso() throws Exception {
        // Arrange
        Cancha canchaActualizada = new Cancha();
        canchaActualizada.setNombre("Cancha Actualizada");
        canchaActualizada.setTipo("Fútbol");
        canchaActualizada.setPrecioPorHora(60000);
        canchaActualizada.setDireccion("Nueva Dirección");
        canchaActualizada.setCiudad("Bogotá");
        canchaActualizada.setActiva(true);

        when(canchaService.actualizarCancha(eq(1L), any(Cancha.class))).thenReturn(cancha);

        // Act & Assert
        mockMvc.perform(put("/api/canchas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(canchaActualizada)))
                .andExpect(status().isOk());

        verify(canchaService, times(1)).actualizarCancha(eq(1L), any(Cancha.class));
    }

    @Test
    void testActualizarCancha_NoExiste() throws Exception {
        // Arrange
        Cancha canchaActualizada = new Cancha();
        when(canchaService.actualizarCancha(eq(999L), any(Cancha.class)))
                .thenThrow(new RuntimeException("Cancha no encontrada"));

        // Act & Assert
        mockMvc.perform(put("/api/canchas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(canchaActualizada)))
                .andExpect(status().isNotFound());

        verify(canchaService, times(1)).actualizarCancha(eq(999L), any(Cancha.class));
    }

    @Test
    void testEliminarCancha() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/canchas/1"))
                .andExpect(status().isNoContent());

        verify(canchaService, times(1)).eliminarCancha(1L);
    }

    @Test
    void testDesactivarCancha() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/canchas/1/desactivar"))
                .andExpect(status().isOk());

        verify(canchaService, times(1)).desactivarCancha(1L);
    }
}

