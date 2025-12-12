package com.example.Disponibilidad.repository;

import com.example.Disponibilidad.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    
    List<Disponibilidad> findByCanchaId(Long canchaId);
    
    List<Disponibilidad> findByCanchaIdAndDisponibleTrue(Long canchaId);
    
    @Query("SELECT d FROM Disponibilidad d WHERE d.canchaId = :canchaId " +
           "AND d.disponible = true " +
           "AND d.fechaInicio >= :fechaInicio " +
           "AND d.fechaFin <= :fechaFin")
    List<Disponibilidad> findDisponibilidadesPorRango(
            @Param("canchaId") Long canchaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT d FROM Disponibilidad d WHERE d.canchaId = :canchaId " +
           "AND d.disponible = true " +
           "AND :fechaInicio < d.fechaFin " +
           "AND :fechaFin > d.fechaInicio")
    List<Disponibilidad> findDisponibilidadesSolapadas(
            @Param("canchaId") Long canchaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT d FROM Disponibilidad d WHERE d.disponible = true " +
           "AND d.fechaInicio >= :fechaInicio " +
           "AND d.fechaFin <= :fechaFin")
    List<Disponibilidad> findDisponibilidadesDisponiblesPorRango(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}