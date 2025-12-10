package com.example.Reservas.service;

import com.example.Reservas.config.DisponibilidadClient;
import com.example.Reservas.model.Reserva;
import com.example.Reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final DisponibilidadClient disponibilidadClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }
    
    public Optional<Reserva> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }
    
    public List<Reserva> obtenerReservasPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
    
    public List<Reserva> obtenerReservasPorCancha(Long canchaId) {
        return reservaRepository.findByCanchaId(canchaId);
    }
    
    public List<Reserva> obtenerReservasPorEstado(Reserva.EstadoReserva estado) {
        return reservaRepository.findByEstado(estado);
    }
    
    public List<Reserva> obtenerReservasPorUsuarioYEstado(Long usuarioId, Reserva.EstadoReserva estado) {
        return reservaRepository.findByUsuarioIdAndEstado(usuarioId, estado);
    }
    
    public List<Reserva> obtenerReservasPorRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return reservaRepository.findReservasPorRango(fechaInicio, fechaFin);
    }
    
    public boolean verificarDisponibilidad(Long canchaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Reserva> reservasSolapadas = reservaRepository.findReservasSolapadas(
                canchaId, fechaInicio, fechaFin);
        return reservasSolapadas.isEmpty();
    }
    
    public Reserva crearReserva(Reserva reserva) {
        // Verificar que no haya conflictos de horario
        if (!verificarDisponibilidad(reserva.getCanchaId(), reserva.getFechaInicio(), reserva.getFechaFin())) {
            throw new RuntimeException("La cancha no está disponible en el horario seleccionado");
        }
        
        // Calcular precio total si no está establecido
        if (reserva.getPrecioTotal() == null) {
            // Aquí deberías obtener el precio por hora de la cancha desde el microservicio de Canchas
            // Por ahora, asumimos que viene calculado o lo establecemos manualmente
            reserva.setPrecioTotal(BigDecimal.ZERO);
        }
        
        if (reserva.getEstado() == null) {
            reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        }
        
        Reserva reservaGuardada = reservaRepository.save(reserva);
        
        // Crear disponibilidad marcada como "no disponible" en el microservicio de Disponibilidad
        try {
            String fechaInicioStr = reserva.getFechaInicio().format(FORMATTER);
            String fechaFinStr = reserva.getFechaFin().format(FORMATTER);
            disponibilidadClient.crearDisponibilidadOcupada(
                reserva.getCanchaId(),
                fechaInicioStr,
                fechaFinStr
            );
        } catch (Exception e) {
            // Si falla crear disponibilidad, no es crítico
            // La reserva ya está guardada
            System.err.println("Error al crear disponibilidad: " + e.getMessage());
        }
        
        return reservaGuardada;
    }
    
    public Reserva actualizarReserva(Long id, Reserva reservaActualizada) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    // Si cambia el horario, verificar disponibilidad
                    if (!reserva.getFechaInicio().equals(reservaActualizada.getFechaInicio()) ||
                        !reserva.getFechaFin().equals(reservaActualizada.getFechaFin()) ||
                        !reserva.getCanchaId().equals(reservaActualizada.getCanchaId())) {
                        
                        // Verificar disponibilidad excluyendo la reserva actual
                        List<Reserva> solapadas = reservaRepository.findReservasSolapadas(
                                reservaActualizada.getCanchaId(),
                                reservaActualizada.getFechaInicio(),
                                reservaActualizada.getFechaFin());
                        solapadas.removeIf(r -> r.getId().equals(id));
                        
                        if (!solapadas.isEmpty()) {
                            throw new RuntimeException("La cancha no está disponible en el horario seleccionado");
                        }
                    }
                    
                    reserva.setUsuarioId(reservaActualizada.getUsuarioId());
                    reserva.setCanchaId(reservaActualizada.getCanchaId());
                    reserva.setFechaInicio(reservaActualizada.getFechaInicio());
                    reserva.setFechaFin(reservaActualizada.getFechaFin());
                    reserva.setPrecioTotal(reservaActualizada.getPrecioTotal());
                    reserva.setEstado(reservaActualizada.getEstado());
                    reserva.setObservaciones(reservaActualizada.getObservaciones());
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }
    
    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }
    
    public Reserva confirmarReserva(Long id) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }
    
    public Reserva cancelarReserva(Long id, String motivo) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
                    if (motivo != null) {
                        reserva.setObservaciones(motivo);
                    }
                    
                    Reserva reservaCancelada = reservaRepository.save(reserva);
                    
                    // Liberar disponibilidad (eliminar o marcar como disponible)
                    try {
                        String fechaInicioStr = reserva.getFechaInicio().format(FORMATTER);
                        String fechaFinStr = reserva.getFechaFin().format(FORMATTER);
                        disponibilidadClient.eliminarDisponibilidad(
                            reserva.getCanchaId(),
                            fechaInicioStr,
                            fechaFinStr
                        );
                    } catch (Exception e) {
                        // Si falla, no es crítico
                        System.err.println("Error al liberar disponibilidad: " + e.getMessage());
                    }
                    
                    return reservaCancelada;
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }
    
    public Reserva completarReserva(Long id) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setEstado(Reserva.EstadoReserva.COMPLETADA);
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }
}