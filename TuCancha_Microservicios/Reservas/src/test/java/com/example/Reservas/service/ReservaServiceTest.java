package com.example.Reservas.service;

import com.example.Reservas.model.Reserva;
import com.example.Reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @BeforeEach
    void setUp() {
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
    void testObtenerTodasLasReservas() {
        // Arrange
        Reserva reserva2 = new Reserva();
        reserva2.setId(2L);
        List<Reserva> reservas = Arrays.asList(reserva, reserva2);
        when(reservaRepository.findAll()).thenReturn(reservas);

        // Act
        List<Reserva> resultado = reservaService.obtenerTodasLasReservas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void testObtenerReservaPorId_Existe() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // Act
        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(reservaRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerReservaPorId_NoExiste() {
        // Arrange
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(reservaRepository, times(1)).findById(999L);
    }

    @Test
    void testObtenerReservasPorUsuario() {
        // Arrange
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaRepository.findByUsuarioId(1L)).thenReturn(reservas);

        // Act
        List<Reserva> resultado = reservaService.obtenerReservasPorUsuario(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getUsuarioId());
        verify(reservaRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void testObtenerReservasPorCancha() {
        // Arrange
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaRepository.findByCanchaId(1L)).thenReturn(reservas);

        // Act
        List<Reserva> resultado = reservaService.obtenerReservasPorCancha(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getCanchaId());
        verify(reservaRepository, times(1)).findByCanchaId(1L);
    }

    @Test
    void testObtenerReservasPorEstado() {
        // Arrange
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaRepository.findByEstado(Reserva.EstadoReserva.PENDIENTE)).thenReturn(reservas);

        // Act
        List<Reserva> resultado = reservaService.obtenerReservasPorEstado(Reserva.EstadoReserva.PENDIENTE);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(Reserva.EstadoReserva.PENDIENTE, resultado.get(0).getEstado());
        verify(reservaRepository, times(1)).findByEstado(Reserva.EstadoReserva.PENDIENTE);
    }

    @Test
    void testVerificarDisponibilidad_Disponible() {
        // Arrange
        when(reservaRepository.findReservasSolapadas(1L, fechaInicio, fechaFin))
                .thenReturn(Collections.emptyList());

        // Act
        boolean resultado = reservaService.verificarDisponibilidad(1L, fechaInicio, fechaFin);

        // Assert
        assertTrue(resultado);
        verify(reservaRepository, times(1)).findReservasSolapadas(1L, fechaInicio, fechaFin);
    }

    @Test
    void testVerificarDisponibilidad_NoDisponible() {
        // Arrange
        when(reservaRepository.findReservasSolapadas(1L, fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(reserva));

        // Act
        boolean resultado = reservaService.verificarDisponibilidad(1L, fechaInicio, fechaFin);

        // Assert
        assertFalse(resultado);
        verify(reservaRepository, times(1)).findReservasSolapadas(1L, fechaInicio, fechaFin);
    }

    @Test
    void testCrearReserva_Exitoso() {
        // Arrange
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setUsuarioId(2L);
        nuevaReserva.setCanchaId(1L);
        nuevaReserva.setFechaInicio(fechaInicio);
        nuevaReserva.setFechaFin(fechaFin);
        nuevaReserva.setPrecioTotal(new BigDecimal("100000"));
        nuevaReserva.setEstado(null); // Para probar que se establece en PENDIENTE

        when(reservaRepository.findReservasSolapadas(1L, fechaInicio, fechaFin))
                .thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(2L);
            return r;
        });

        // Act
        Reserva resultado = reservaService.crearReserva(nuevaReserva);

        // Assert
        assertNotNull(resultado);
        assertEquals(Reserva.EstadoReserva.PENDIENTE, resultado.getEstado());
        verify(reservaRepository, times(1)).findReservasSolapadas(1L, fechaInicio, fechaFin);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void testCrearReserva_NoDisponible() {
        // Arrange
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setCanchaId(1L);
        nuevaReserva.setFechaInicio(fechaInicio);
        nuevaReserva.setFechaFin(fechaFin);

        when(reservaRepository.findReservasSolapadas(1L, fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(reserva));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(nuevaReserva);
        });
        verify(reservaRepository, times(1)).findReservasSolapadas(1L, fechaInicio, fechaFin);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void testActualizarReserva_Exitoso() {
        // Arrange
        Reserva reservaActualizada = new Reserva();
        reservaActualizada.setUsuarioId(1L);
        reservaActualizada.setCanchaId(1L);
        reservaActualizada.setFechaInicio(fechaInicio);
        reservaActualizada.setFechaFin(fechaFin);
        reservaActualizada.setPrecioTotal(new BigDecimal("120000"));
        reservaActualizada.setEstado(Reserva.EstadoReserva.CONFIRMADA);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.findReservasSolapadas(1L, fechaInicio, fechaFin))
                .thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // Act
        Reserva resultado = reservaService.actualizarReserva(1L, reservaActualizada);

        // Assert
        assertNotNull(resultado);
        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void testActualizarReserva_NoExiste() {
        // Arrange
        Reserva reservaActualizada = new Reserva();
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            reservaService.actualizarReserva(999L, reservaActualizada);
        });
        verify(reservaRepository, times(1)).findById(999L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void testEliminarReserva() {
        // Act
        reservaService.eliminarReserva(1L);

        // Assert
        verify(reservaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testConfirmarReserva() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // Act
        Reserva resultado = reservaService.confirmarReserva(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(Reserva.EstadoReserva.CONFIRMADA, reserva.getEstado());
        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void testConfirmarReserva_NoExiste() {
        // Arrange
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            reservaService.confirmarReserva(999L);
        });
        verify(reservaRepository, times(1)).findById(999L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void testCancelarReserva() {
        // Arrange
        String motivo = "Cancelación por el usuario";
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // Act
        Reserva resultado = reservaService.cancelarReserva(1L, motivo);

        // Assert
        assertNotNull(resultado);
        assertEquals(Reserva.EstadoReserva.CANCELADA, reserva.getEstado());
        assertEquals(motivo, reserva.getObservaciones());
        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void testCompletarReserva() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // Act
        Reserva resultado = reservaService.completarReserva(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(Reserva.EstadoReserva.COMPLETADA, reserva.getEstado());
        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }
}

