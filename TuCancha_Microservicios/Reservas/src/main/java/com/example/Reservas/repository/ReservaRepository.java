package com.example.Reservas.repository;

import com.example.Reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    List<Reserva> findByUsuarioId(Long usuarioId);
    
    List<Reserva> findByCanchaId(Long canchaId);
    
    List<Reserva> findByEstado(Reserva.EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE r.usuarioId = :usuarioId AND r.estado = :estado")
    List<Reserva> findByUsuarioIdAndEstado(@Param("usuarioId") Long usuarioId, 
                                           @Param("estado") Reserva.EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE r.canchaId = :canchaId " +
           "AND r.estado != 'CANCELADA' " +
           "AND :fechaInicio < r.fechaFin " +
           "AND :fechaFin > r.fechaInicio")
    List<Reserva> findReservasSolapadas(@Param("canchaId") Long canchaId,
                                        @Param("fechaInicio") LocalDateTime fechaInicio,
                                        @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio >= :fechaInicio AND r.fechaFin <= :fechaFin")
    List<Reserva> findReservasPorRango(@Param("fechaInicio") LocalDateTime fechaInicio,
                                       @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT r FROM Reserva r WHERE r.usuarioId = :usuarioId " +
           "AND r.fechaInicio >= :fechaInicio AND r.fechaFin <= :fechaFin")
    List<Reserva> findReservasPorUsuarioYRango(@Param("usuarioId") Long usuarioId,
                                                @Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);
}