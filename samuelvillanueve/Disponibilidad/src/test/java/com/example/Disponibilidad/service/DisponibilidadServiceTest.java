package com.example.Disponibilidad.service;

import com.example.Disponibilidad.model.Disponibilidad;
import com.example.Disponibilidad.repository.DisponibilidadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceTest {

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @InjectMocks
    private DisponibilidadService disponibilidadService;

    private Disponibilidad disponibilidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @BeforeEach
    void setUp() {
        fechaInicio = LocalDateTime.of(2024, 1, 15, 10, 0);
        fechaFin = LocalDateTime.of(2024, 1, 15, 12, 0);

        disponibilidad = new Disponibilidad();
        disponibilidad.setId(1L);
        disponibilidad.setCanchaId(1L);
        disponibilidad.setFechaInicio(fechaInicio);
        disponibilidad.setFechaFin(fechaFin);
        disponibilidad.setDisponible(true);
    }

    @Test
    void testObtenerTodasLasDisponibilidades() {
        // Arrange
        Disponibilidad disponibilidad2 = new Disponibilidad();
        disponibilidad2.setId(2L);
        List<Disponibilidad> disponibilidades = Arrays.asList(disponibilidad, disponibilidad2);
        when(disponibilidadRepository.findAll()).thenReturn(disponibilidades);

        // Act
        List<Disponibilidad> resultado = disponibilidadService.obtenerTodasLasDisponibilidades();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(disponibilidadRepository, times(1)).findAll();
    }

    @Test
    void testObtenerDisponibilidadesPorCancha() {
        // Arrange
        List<Disponibilidad> disponibilidades = Arrays.asList(disponibilidad);
        when(disponibilidadRepository.findByCanchaId(1L)).thenReturn(disponibilidades);

        // Act
        List<Disponibilidad> resultado = disponibilidadService.obtenerDisponibilidadesPorCancha(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getCanchaId());
        verify(disponibilidadRepository, times(1)).findByCanchaId(1L);
    }

    @Test
    void testObtenerDisponibilidadesActivasPorCancha() {
        // Arrange
        List<Disponibilidad> disponibilidades = Arrays.asList(disponibilidad);
        when(disponibilidadRepository.findByCanchaIdAndDisponibleTrue(1L)).thenReturn(disponibilidades);

        // Act
        List<Disponibilidad> resultado = disponibilidadService.obtenerDisponibilidadesActivasPorCancha(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDisponible());
        verify(disponibilidadRepository, times(1)).findByCanchaIdAndDisponibleTrue(1L);
    }

    @Test
    void testObtenerDisponibilidadesPorRango() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2024, 1, 15, 12, 0);
        List<Disponibilidad> disponibilidades = Arrays.asList(disponibilidad);
        when(disponibilidadRepository.findDisponibilidadesPorRango(1L, inicio, fin))
                .thenReturn(disponibilidades);

        // Act
        List<Disponibilidad> resultado = disponibilidadService.obtenerDisponibilidadesPorRango(1L, inicio, fin);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(disponibilidadRepository, times(1))
                .findDisponibilidadesPorRango(1L, inicio, fin);
    }

    @Test
    void testVerificarDisponibilidad_Disponible() {
        // Arrange
        when(disponibilidadRepository.findDisponibilidadesSolapadas(1L, fechaInicio, fechaFin))
                .thenReturn(Collections.emptyList());

        // Act
        boolean resultado = disponibilidadService.verificarDisponibilidad(1L, fechaInicio, fechaFin);

        // Assert
        assertTrue(resultado);
        verify(disponibilidadRepository, times(1))
                .findDisponibilidadesSolapadas(1L, fechaInicio, fechaFin);
    }

    @Test
    void testVerificarDisponibilidad_NoDisponible() {
        // Arrange
        when(disponibilidadRepository.findDisponibilidadesSolapadas(1L, fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(disponibilidad));

        // Act
        boolean resultado = disponibilidadService.verificarDisponibilidad(1L, fechaInicio, fechaFin);

        // Assert
        assertFalse(resultado);
        verify(disponibilidadRepository, times(1))
                .findDisponibilidadesSolapadas(1L, fechaInicio, fechaFin);
    }

    @Test
    void testCrearDisponibilidad() {
        // Arrange
        Disponibilidad nuevaDisponibilidad = new Disponibilidad();
        nuevaDisponibilidad.setCanchaId(1L);
        nuevaDisponibilidad.setFechaInicio(fechaInicio);
        nuevaDisponibilidad.setFechaFin(fechaFin);
        nuevaDisponibilidad.setDisponible(null); // Para probar que se establece en true

        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(invocation -> {
            Disponibilidad d = invocation.getArgument(0);
            d.setId(2L);
            return d;
        });

        // Act
        Disponibilidad resultado = disponibilidadService.crearDisponibilidad(nuevaDisponibilidad);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getDisponible()); // Debe establecerse en true
        verify(disponibilidadRepository, times(1)).save(any(Disponibilidad.class));
    }

    @Test
    void testActualizarDisponibilidad_Existe() {
        // Arrange
        Disponibilidad disponibilidadActualizada = new Disponibilidad();
        disponibilidadActualizada.setCanchaId(2L);
        disponibilidadActualizada.setFechaInicio(fechaInicio);
        disponibilidadActualizada.setFechaFin(fechaFin);
        disponibilidadActualizada.setDisponible(false);
        disponibilidadActualizada.setMotivoNoDisponible("Mantenimiento");

        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(disponibilidad);

        // Act
        Disponibilidad resultado = disponibilidadService.actualizarDisponibilidad(1L, disponibilidadActualizada);

        // Assert
        assertNotNull(resultado);
        verify(disponibilidadRepository, times(1)).findById(1L);
        verify(disponibilidadRepository, times(1)).save(any(Disponibilidad.class));
    }

    @Test
    void testActualizarDisponibilidad_NoExiste() {
        // Arrange
        Disponibilidad disponibilidadActualizada = new Disponibilidad();
        when(disponibilidadRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            disponibilidadService.actualizarDisponibilidad(999L, disponibilidadActualizada);
        });
        verify(disponibilidadRepository, times(1)).findById(999L);
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class));
    }

    @Test
    void testEliminarDisponibilidad() {
        // Act
        disponibilidadService.eliminarDisponibilidad(1L);

        // Assert
        verify(disponibilidadRepository, times(1)).deleteById(1L);
    }

    @Test
    void testMarcarComoNoDisponible() {
        // Arrange
        String motivo = "Mantenimiento programado";
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(disponibilidad);

        // Act
        disponibilidadService.marcarComoNoDisponible(1L, motivo);

        // Assert
        assertFalse(disponibilidad.getDisponible());
        assertEquals(motivo, disponibilidad.getMotivoNoDisponible());
        verify(disponibilidadRepository, times(1)).findById(1L);
        verify(disponibilidadRepository, times(1)).save(any(Disponibilidad.class));
    }

    @Test
    void testMarcarComoNoDisponible_NoExiste() {
        // Arrange
        when(disponibilidadRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        disponibilidadService.marcarComoNoDisponible(999L, "Motivo");

        // Assert
        verify(disponibilidadRepository, times(1)).findById(999L);
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class));
    }

    @Test
    void testObtenerDisponibilidadPorId() {
        // Arrange
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));

        // Act
        Optional<Disponibilidad> resultado = disponibilidadService.obtenerDisponibilidadPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(disponibilidadRepository, times(1)).findById(1L);
    }
}

