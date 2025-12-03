package com.example.Reservas.controller;

import com.example.Reservas.model.Reserva;
import com.example.Reservas.service.ReservaService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReservaController reservaController;

    private ObjectMapper objectMapper;

    private Reserva reserva;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(reservaController).build();
        
        fechaInicio = LocalDateTime.of(2024, 1, 15, 10, 0);
        fechaFin = LocalDateTime.of(2024, 1, 15, 12, 0);

        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setUsuarioId(1L);
        reserva.setCanchaId(1L);
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setPrecioTotal(new BigDecimal("100000"));
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
    }

    @Test
    void testObtenerTodasLasReservas() throws Exception {
        // Arrange
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaService.obtenerTodasLasReservas()).thenReturn(reservas);

        // Act & Assert
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(reservaService, times(1)).obtenerTodasLasReservas();
    }

    @Test
    void testObtenerReservaPorId_Existe() throws Exception {
        // Arrange
        when(reservaService.obtenerReservaPorId(1L)).thenReturn(Optional.of(reserva));

        // Act & Assert
        mockMvc.perform(get("/api/reservas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(reservaService, times(1)).obtenerReservaPorId(1L);
    }

    @Test
    void testObtenerReservaPorId_NoExiste() throws Exception {
        // Arrange
        when(reservaService.obtenerReservaPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/reservas/999"))
                .andExpect(status().isNotFound());

        verify(reservaService, times(1)).obtenerReservaPorId(999L);
    }

    @Test
    void testObtenerReservasPorUsuario() throws Exception {
        // Arrange
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaService.obtenerReservasPorUsuario(1L)).thenReturn(reservas);

        // Act & Assert
        mockMvc.perform(get("/api/reservas/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(reservaService, times(1)).obtenerReservasPorUsuario(1L);
    }

    @Test
    void testCrearReserva_Exitoso() throws Exception {
        // Arrange
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setUsuarioId(2L);
        nuevaReserva.setCanchaId(1L);
        nuevaReserva.setFechaInicio(fechaInicio);
        nuevaReserva.setFechaFin(fechaFin);
        nuevaReserva.setPrecioTotal(new BigDecimal("100000"));

        when(reservaService.crearReserva(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(2L);
            r.setEstado(Reserva.EstadoReserva.PENDIENTE);
            return r;
        });

        // Act & Assert
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaReserva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(2));

        verify(reservaService, times(1)).crearReserva(any(Reserva.class));
    }

    @Test
    void testCrearReserva_NoDisponible() throws Exception {
        // Arrange
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setCanchaId(1L);
        nuevaReserva.setFechaInicio(fechaInicio);
        nuevaReserva.setFechaFin(fechaFin);

        when(reservaService.crearReserva(any(Reserva.class)))
                .thenThrow(new RuntimeException("La cancha no está disponible en el horario seleccionado"));

        // Act & Assert
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaReserva)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(reservaService, times(1)).crearReserva(any(Reserva.class));
    }

    @Test
    void testVerificarDisponibilidad() throws Exception {
        // Arrange
        when(reservaService.verificarDisponibilidad(1L, fechaInicio, fechaFin)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/reservas/verificar")
                        .param("canchaId", "1")
                        .param("fechaInicio", "2024-01-15T10:00:00")
                        .param("fechaFin", "2024-01-15T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(reservaService, times(1)).verificarDisponibilidad(1L, fechaInicio, fechaFin);
    }

    @Test
    void testConfirmarReserva() throws Exception {
        // Arrange
        when(reservaService.confirmarReserva(1L)).thenReturn(reserva);

        // Act & Assert
        mockMvc.perform(patch("/api/reservas/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservaService, times(1)).confirmarReserva(1L);
    }

    @Test
    void testCancelarReserva() throws Exception {
        // Arrange
        when(reservaService.cancelarReserva(eq(1L), anyString())).thenReturn(reserva);

        // Act & Assert
        mockMvc.perform(patch("/api/reservas/1/cancelar")
                        .param("motivo", "Cancelación por el usuario"))
                .andExpect(status().isOk());

        verify(reservaService, times(1)).cancelarReserva(eq(1L), anyString());
    }

    @Test
    void testCompletarReserva() throws Exception {
        // Arrange
        when(reservaService.completarReserva(1L)).thenReturn(reserva);

        // Act & Assert
        mockMvc.perform(patch("/api/reservas/1/completar"))
                .andExpect(status().isOk());

        verify(reservaService, times(1)).completarReserva(1L);
    }

    @Test
    void testEliminarReserva() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/reservas/1"))
                .andExpect(status().isNoContent());

        verify(reservaService, times(1)).eliminarReserva(1L);
    }
}

