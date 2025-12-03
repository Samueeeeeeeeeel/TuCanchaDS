package com.example.Canchas.service;

import com.example.Canchas.model.Cancha;
import com.example.Canchas.repository.CanchaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CanchaService {
    
    private final CanchaRepository canchaRepository;
    
    public List<Cancha> obtenerTodasLasCanchas() {
        return canchaRepository.findAll();
    }
    
    public List<Cancha> obtenerCanchasActivas() {
        return canchaRepository.findByActivaTrue();
    }
    
    public Optional<Cancha> obtenerCanchaPorId(Long id) {
        return canchaRepository.findById(id);
    }
    
    public Optional<Cancha> obtenerCanchaActivaPorId(Long id) {
        return canchaRepository.findByIdAndActivaTrue(id);
    }
    
    public List<Cancha> obtenerCanchasPorTipo(String tipo) {
        return canchaRepository.findByTipo(tipo);
    }
    
    public List<Cancha> obtenerCanchasPorCiudad(String ciudad) {
        return canchaRepository.findByCiudad(ciudad);
    }
    
    public List<Cancha> obtenerCanchasActivasPorCiudad(String ciudad) {
        return canchaRepository.findCanchasActivasPorCiudad(ciudad);
    }
    
    public Cancha crearCancha(Cancha cancha) {
        if (cancha.getActiva() == null) {
            cancha.setActiva(true);
        }
        return canchaRepository.save(cancha);
    }
    
    public Cancha actualizarCancha(Long id, Cancha canchaActualizada) {
        return canchaRepository.findById(id)
                .map(cancha -> {
                    cancha.setNombre(canchaActualizada.getNombre());
                    cancha.setDescripcion(canchaActualizada.getDescripcion());
                    cancha.setTipo(canchaActualizada.getTipo());
                    cancha.setPrecioPorHora(canchaActualizada.getPrecioPorHora());
                    cancha.setDireccion(canchaActualizada.getDireccion());
                    cancha.setCiudad(canchaActualizada.getCiudad());
                    cancha.setActiva(canchaActualizada.getActiva());
                    return canchaRepository.save(cancha);
                })
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada con id: " + id));
    }
    
    public void eliminarCancha(Long id) {
        canchaRepository.deleteById(id);
    }
    
    public void desactivarCancha(Long id) {
        canchaRepository.findById(id)
                .ifPresent(cancha -> {
                    cancha.setActiva(false);
                    canchaRepository.save(cancha);
                });
    }
}