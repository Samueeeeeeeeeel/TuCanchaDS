package com.example.Canchas.service;

import com.example.Canchas.model.Cancha;
import com.example.Canchas.repository.CanchaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CanchaServiceTest {

    @Mock
    private CanchaRepository canchaRepository;

    @InjectMocks
    private CanchaService canchaService;

    private Cancha cancha;
    private Cancha cancha2;

    @BeforeEach
    void setUp() {
        cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNombre("Cancha de Fútbol 1");
        cancha.setDescripcion("Cancha de fútbol 11");
        cancha.setTipo("Fútbol");
        cancha.setPrecioPorHora(50000);
        cancha.setDireccion("Calle 123");
        cancha.setCiudad("Bogotá");
        cancha.setActiva(true);

        cancha2 = new Cancha();
        cancha2.setId(2L);
        cancha2.setNombre("Cancha de Tenis");
        cancha2.setDescripcion("Cancha de tenis profesional");
        cancha2.setTipo("Tenis");
        cancha2.setPrecioPorHora(30000);
        cancha2.setDireccion("Avenida 456");
        cancha2.setCiudad("Medellín");
        cancha2.setActiva(true);
    }

    @Test
    void testObtenerTodasLasCanchas() {
        // Arrange
        List<Cancha> canchas = Arrays.asList(cancha, cancha2);
        when(canchaRepository.findAll()).thenReturn(canchas);

        // Act
        List<Cancha> resultado = canchaService.obtenerTodasLasCanchas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(canchaRepository, times(1)).findAll();
    }

    @Test
    void testObtenerCanchasActivas() {
        // Arrange
        List<Cancha> canchasActivas = Arrays.asList(cancha, cancha2);
        when(canchaRepository.findByActivaTrue()).thenReturn(canchasActivas);

        // Act
        List<Cancha> resultado = canchaService.obtenerCanchasActivas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(canchaRepository, times(1)).findByActivaTrue();
    }

    @Test
    void testObtenerCanchaPorId_Existe() {
        // Arrange
        when(canchaRepository.findById(1L)).thenReturn(Optional.of(cancha));

        // Act
        Optional<Cancha> resultado = canchaService.obtenerCanchaPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Cancha de Fútbol 1", resultado.get().getNombre());
        verify(canchaRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerCanchaPorId_NoExiste() {
        // Arrange
        when(canchaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Cancha> resultado = canchaService.obtenerCanchaPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(canchaRepository, times(1)).findById(999L);
    }

    @Test
    void testObtenerCanchaActivaPorId() {
        // Arrange
        when(canchaRepository.findByIdAndActivaTrue(1L)).thenReturn(Optional.of(cancha));

        // Act
        Optional<Cancha> resultado = canchaService.obtenerCanchaActivaPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Cancha de Fútbol 1", resultado.get().getNombre());
        verify(canchaRepository, times(1)).findByIdAndActivaTrue(1L);
    }

    @Test
    void testObtenerCanchasPorTipo() {
        // Arrange
        List<Cancha> canchasFutbol = Arrays.asList(cancha);
        when(canchaRepository.findByTipo("Fútbol")).thenReturn(canchasFutbol);

        // Act
        List<Cancha> resultado = canchaService.obtenerCanchasPorTipo("Fútbol");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Fútbol", resultado.get(0).getTipo());
        verify(canchaRepository, times(1)).findByTipo("Fútbol");
    }

    @Test
    void testObtenerCanchasPorCiudad() {
        // Arrange
        List<Cancha> canchasBogota = Arrays.asList(cancha);
        when(canchaRepository.findByCiudad("Bogotá")).thenReturn(canchasBogota);

        // Act
        List<Cancha> resultado = canchaService.obtenerCanchasPorCiudad("Bogotá");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Bogotá", resultado.get(0).getCiudad());
        verify(canchaRepository, times(1)).findByCiudad("Bogotá");
    }

    @Test
    void testCrearCancha() {
        // Arrange
        Cancha nuevaCancha = new Cancha();
        nuevaCancha.setNombre("Nueva Cancha");
        nuevaCancha.setTipo("Básquet");
        nuevaCancha.setPrecioPorHora(40000);
        nuevaCancha.setDireccion("Calle Nueva");
        nuevaCancha.setCiudad("Cali");
        nuevaCancha.setActiva(null); // Para probar que se establece en true

        when(canchaRepository.save(any(Cancha.class))).thenAnswer(invocation -> {
            Cancha c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        // Act
        Cancha resultado = canchaService.crearCancha(nuevaCancha);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getActiva()); // Debe establecerse en true
        verify(canchaRepository, times(1)).save(any(Cancha.class));
    }

    @Test
    void testActualizarCancha_Existe() {
        // Arrange
        Cancha canchaActualizada = new Cancha();
        canchaActualizada.setNombre("Cancha Actualizada");
        canchaActualizada.setDescripcion("Nueva descripción");
        canchaActualizada.setTipo("Fútbol");
        canchaActualizada.setPrecioPorHora(60000);
        canchaActualizada.setDireccion("Nueva Dirección");
        canchaActualizada.setCiudad("Bogotá");
        canchaActualizada.setActiva(true);

        when(canchaRepository.findById(1L)).thenReturn(Optional.of(cancha));
        when(canchaRepository.save(any(Cancha.class))).thenReturn(cancha);

        // Act
        Cancha resultado = canchaService.actualizarCancha(1L, canchaActualizada);

        // Assert
        assertNotNull(resultado);
        verify(canchaRepository, times(1)).findById(1L);
        verify(canchaRepository, times(1)).save(any(Cancha.class));
    }

    @Test
    void testActualizarCancha_NoExiste() {
        // Arrange
        Cancha canchaActualizada = new Cancha();
        when(canchaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            canchaService.actualizarCancha(999L, canchaActualizada);
        });
        verify(canchaRepository, times(1)).findById(999L);
        verify(canchaRepository, never()).save(any(Cancha.class));
    }

    @Test
    void testEliminarCancha() {
        // Act
        canchaService.eliminarCancha(1L);

        // Assert
        verify(canchaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDesactivarCancha() {
        // Arrange
        when(canchaRepository.findById(1L)).thenReturn(Optional.of(cancha));
        when(canchaRepository.save(any(Cancha.class))).thenReturn(cancha);

        // Act
        canchaService.desactivarCancha(1L);

        // Assert
        assertFalse(cancha.getActiva());
        verify(canchaRepository, times(1)).findById(1L);
        verify(canchaRepository, times(1)).save(any(Cancha.class));
    }

    @Test
    void testDesactivarCancha_NoExiste() {
        // Arrange
        when(canchaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        canchaService.desactivarCancha(999L);

        // Assert
        verify(canchaRepository, times(1)).findById(999L);
        verify(canchaRepository, never()).save(any(Cancha.class));
    }
}

