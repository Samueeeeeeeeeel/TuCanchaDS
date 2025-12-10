package com.example.Login.service;

import com.example.Login.model.Rol;
import com.example.Login.model.Usuario;
import com.example.Login.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public Optional<Usuario> obtenerUsuarioActivoPorEmail(String email) {
        return usuarioRepository.findByEmailAndActivoTrue(email);
    }
    
    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + usuario.getEmail());
        }
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        // Los usuarios nuevos siempre son USUARIO, no se puede crear ADMIN desde aquí
        if (usuario.getRol() == null || usuario.getRol() == Rol.ADMIN) {
            usuario.setRol(Rol.USUARIO);
        }
        return usuarioRepository.save(usuario);
    }
    
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Verificar si el email cambió y no está duplicado
                    if (!usuario.getEmail().equals(usuarioActualizado.getEmail()) &&
                        usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
                        throw new RuntimeException("El email ya está registrado: " + usuarioActualizado.getEmail());
                    }
                    usuario.setEmail(usuarioActualizado.getEmail());
                    usuario.setNombre(usuarioActualizado.getNombre());
                    usuario.setApellido(usuarioActualizado.getApellido());
                    usuario.setTelefono(usuarioActualizado.getTelefono());
                    if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
                        usuario.setPassword(usuarioActualizado.getPassword());
                    }
                    if (usuarioActualizado.getActivo() != null) {
                        usuario.setActivo(usuarioActualizado.getActivo());
                    }
                    // El rol solo se puede cambiar mediante el método cambiarRol (solo admin)
                    // No se actualiza aquí para mantener la seguridad
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }
    
    public void eliminarUsuario(Long id) {
        usuarioRepository.findById(id)
                .ifPresent(usuario -> {
                    // Proteger al admin predeterminado
                    if ("Admin@admin.cl".equalsIgnoreCase(usuario.getEmail())) {
                        throw new RuntimeException("No se puede eliminar al administrador predeterminado del sistema");
                    }
                    usuarioRepository.deleteById(id);
                });
    }
    
    public void desactivarUsuario(Long id) {
        usuarioRepository.findById(id)
                .ifPresent(usuario -> {
                    usuario.setActivo(false);
                    usuarioRepository.save(usuario);
                });
    }
    
    public void actualizarUltimoAcceso(Long id) {
        usuarioRepository.findById(id)
                .ifPresent(usuario -> {
                    usuario.setUltimoAcceso(LocalDateTime.now());
                    usuarioRepository.save(usuario);
                });
    }
    
    public boolean validarCredenciales(String email, String password) {
        return usuarioRepository.findByEmailAndActivoTrue(email)
                .map(usuario -> usuario.getPassword().equals(password))
                .orElse(false);
    }
    
    public boolean esAdmin(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(usuario -> usuario.getRol() == Rol.ADMIN)
                .orElse(false);
    }
    
    public boolean esAdminPorEmail(String email) {
        return usuarioRepository.findByEmailAndActivoTrue(email)
                .map(usuario -> usuario.getRol() == Rol.ADMIN)
                .orElse(false);
    }
    
    public List<Usuario> obtenerUsuariosPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
    
    public Usuario cambiarRol(Long id, Rol nuevoRol) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Proteger al admin predeterminado
                    if ("Admin@admin.cl".equalsIgnoreCase(usuario.getEmail())) {
                        throw new RuntimeException("No se puede cambiar el rol del administrador predeterminado del sistema");
                    }
                    usuario.setRol(nuevoRol);
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }
    
    public void inicializarAdmin() {
        String adminEmail = "Admin@admin.cl";
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = new Usuario();
            admin.setEmail(adminEmail);
            admin.setPassword("Admin123");
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setActivo(true);
            admin.setRol(Rol.ADMIN);
            usuarioRepository.save(admin);
        }
    }
}