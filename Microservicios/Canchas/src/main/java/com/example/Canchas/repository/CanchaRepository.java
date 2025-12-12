package com.example.Canchas.repository;

import com.example.Canchas.model.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Long> {
    
    List<Cancha> findByActivaTrue();
    
    List<Cancha> findByTipo(String tipo);
    
    List<Cancha> findByCiudad(String ciudad);
    
    @Query("SELECT c FROM Cancha c WHERE c.activa = true AND c.ciudad = :ciudad")
    List<Cancha> findCanchasActivasPorCiudad(@Param("ciudad") String ciudad);
    
    @Query("SELECT c FROM Cancha c WHERE c.activa = true AND c.tipo = :tipo")
    List<Cancha> findCanchasActivasPorTipo(@Param("tipo") String tipo);
    
    Optional<Cancha> findByIdAndActivaTrue(Long id);
}