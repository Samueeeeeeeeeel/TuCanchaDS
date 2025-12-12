package com.example.Disponibilidad.service;

import com.example.Disponibilidad.model.Disponibilidad;
import com.example.Disponibilidad.repository.DisponibilidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DisponibilidadService {
    
    private final DisponibilidadRepository disponibilidadRepository;
    
    public List<Disponibilidad> obtenerTodasLasDisponibilidades() {
        return disponibilidadRepository.findAll();
    }
    
    public List<Disponibilidad> obtenerDisponibilidadesPorCancha(Long canchaId) {
        return disponibilidadRepository.findByCanchaId(canchaId);
    }
    
    public List<Disponibilidad> obtenerDisponibilidadesActivasPorCancha(Long canchaId) {
        return disponibilidadRepository.findByCanchaIdAndDisponibleTrue(canchaId);
    }
    
    public List<Disponibilidad> obtenerDisponibilidadesPorRango(
            Long canchaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return disponibilidadRepository.findDisponibilidadesPorRango(canchaId, fechaInicio, fechaFin);
    }
    
    public List<Disponibilidad> obtenerDisponibilidadesDisponiblesPorRango(
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return disponibilidadRepository.findDisponibilidadesDisponiblesPorRango(fechaInicio, fechaFin);
    }
    
    public boolean verificarDisponibilidad(Long canchaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Disponibilidad> solapadas = disponibilidadRepository.findDisponibilidadesSolapadas(
                canchaId, fechaInicio, fechaFin);
        return solapadas.isEmpty();
    }
    
    public Disponibilidad crearDisponibilidad(Disponibilidad disponibilidad) {
        if (disponibilidad.getDisponible() == null) {
            disponibilidad.setDisponible(true);
        }
        return disponibilidadRepository.save(disponibilidad);
    }
    
    public Disponibilidad actualizarDisponibilidad(Long id, Disponibilidad disponibilidadActualizada) {
        return disponibilidadRepository.findById(id)
                .map(disponibilidad -> {
                    disponibilidad.setCanchaId(disponibilidadActualizada.getCanchaId());
                    disponibilidad.setFechaInicio(disponibilidadActualizada.getFechaInicio());
                    disponibilidad.setFechaFin(disponibilidadActualizada.getFechaFin());
                    disponibilidad.setDisponible(disponibilidadActualizada.getDisponible());
                    disponibilidad.setMotivoNoDisponible(disponibilidadActualizada.getMotivoNoDisponible());
                    return disponibilidadRepository.save(disponibilidad);
                })
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada con id: " + id));
    }
    
    public void eliminarDisponibilidad(Long id) {
        disponibilidadRepository.deleteById(id);
    }
    
    public void marcarComoNoDisponible(Long id, String motivo) {
        disponibilidadRepository.findById(id)
                .ifPresent(disponibilidad -> {
                    disponibilidad.setDisponible(false);
                    disponibilidad.setMotivoNoDisponible(motivo);
                    disponibilidadRepository.save(disponibilidad);
                });
    }
    
    public Optional<Disponibilidad> obtenerDisponibilidadPorId(Long id) {
        return disponibilidadRepository.findById(id);
    }
}