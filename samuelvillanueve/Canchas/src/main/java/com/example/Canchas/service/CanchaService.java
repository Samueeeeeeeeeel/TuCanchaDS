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
                    cancha.setImagenUrl(canchaActualizada.getImagenUrl());
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
    
    /**
     * Inicializa las 4 canchas de fútbol por defecto si no existen
     */
    public void inicializarCanchas() {
        // Verificar si ya existen canchas
        if (canchaRepository.count() > 0) {
            return; // Ya hay canchas, no inicializar
        }
        
        // Crear Cancha Fútbol 1
        Cancha cancha1 = new Cancha();
        cancha1.setNombre("Cancha Fútbol 1");
        cancha1.setDescripcion("Cancha de fútbol 11 con césped sintético de última generación. Iluminación LED para partidos nocturnos. Incluye vestuarios y duchas.");
        cancha1.setTipo("Fútbol");
        cancha1.setPrecioPorHora(45000);
        cancha1.setDireccion("Av. Principal 1234");
        cancha1.setCiudad("Santiago");
        cancha1.setImagenUrl("https://sportwelt.cl/wp-content/uploads/2022/12/CANCHAS-DE-FUTBOL-SINTETICO-EN-CLUB-PALESTINO-2-750x423.jpg");
        cancha1.setActiva(true);
        canchaRepository.save(cancha1);
        
        // Crear Cancha Fútbol 2
        Cancha cancha2 = new Cancha();
        cancha2.setNombre("Cancha Fútbol 2");
        cancha2.setDescripcion("Cancha de fútbol 7 ideal para partidos rápidos. Superficie de césped natural. Perfecta para grupos pequeños.");
        cancha2.setTipo("Fútbol");
        cancha2.setPrecioPorHora(35000);
        cancha2.setDireccion("Calle Los Olivos 567");
        cancha2.setCiudad("Santiago");
        cancha2.setImagenUrl("https://canchas.ymcatemuco.cl/img/canchas1.jpg");
        cancha2.setActiva(true);
        canchaRepository.save(cancha2);
        
        // Crear Cancha Fútbol 3
        Cancha cancha3 = new Cancha();
        cancha3.setNombre("Cancha Fútbol 3");
        cancha3.setDescripcion("Cancha de fútbol 11 profesional con graderías. Ideal para torneos y eventos deportivos. Incluye sistema de sonido.");
        cancha3.setTipo("Fútbol");
        cancha3.setPrecioPorHora(55000);
        cancha3.setDireccion("Av. Deportiva 890");
        cancha3.setCiudad("Providencia");
        cancha3.setImagenUrl("https://alquilatucancha-public.s3.sa-east-1.amazonaws.com/production/public/clubs/bg/pueblito-panuelas-la-serena.jpeg?598843");
        cancha3.setActiva(true);
        canchaRepository.save(cancha3);
        
        // Crear Cancha Fútbol 4
        Cancha cancha4 = new Cancha();
        cancha4.setNombre("Cancha Fútbol 4");
        cancha4.setDescripcion("Cancha de fútbol 5 techada. Perfecta para jugar en cualquier clima. Incluye iluminación y vestuarios modernos.");
        cancha4.setTipo("Fútbol");
        cancha4.setPrecioPorHora(40000);
        cancha4.setDireccion("Calle Deportes 321");
        cancha4.setCiudad("Las Condes");
        cancha4.setImagenUrl("https://cl.habcdn.com/photos/business/medium/img-20160419-wa0029-152049.jpg");
        cancha4.setActiva(true);
        canchaRepository.save(cancha4);
    }
}