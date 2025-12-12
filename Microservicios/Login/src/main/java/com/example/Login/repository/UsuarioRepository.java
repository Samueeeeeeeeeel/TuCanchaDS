package com.example.Login.repository;

import com.example.Login.model.Rol;
import com.example.Login.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    
    boolean existsByEmail(String email);
    
    Optional<Usuario> findByIdAndActivoTrue(Long id);
    
    List<Usuario> findByRol(Rol rol);
    
    Optional<Usuario> findByEmailAndRol(String email, Rol rol);
}