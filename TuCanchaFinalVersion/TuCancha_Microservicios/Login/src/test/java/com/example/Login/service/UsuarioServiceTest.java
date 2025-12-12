package com.example.Login.service;

import com.example.Login.model.Usuario;
import com.example.Login.repository.UsuarioRepository;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("usuario1@test.com");
        usuario.setPassword("password123");
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setTelefono("3001234567");
        usuario.setActivo(true);

        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setEmail("usuario2@test.com");
        usuario2.setPassword("password456");
        usuario2.setNombre("María");
        usuario2.setApellido("García");
        usuario2.setActivo(true);
    }

    @Test
    void testObtenerTodosLosUsuarios() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testObtenerUsuarioPorId_Existe() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("usuario1@test.com", resultado.get().getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerUsuarioPorId_NoExiste() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    void testObtenerUsuarioPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmail("usuario1@test.com")).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorEmail("usuario1@test.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("usuario1@test.com", resultado.get().getEmail());
        verify(usuarioRepository, times(1)).findByEmail("usuario1@test.com");
    }

    @Test
    void testObtenerUsuarioActivoPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmailAndActivoTrue("usuario1@test.com"))
                .thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioActivoPorEmail("usuario1@test.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().getActivo());
        verify(usuarioRepository, times(1)).findByEmailAndActivoTrue("usuario1@test.com");
    }

    @Test
    void testExisteUsuarioPorEmail_Existe() {
        // Arrange
        when(usuarioRepository.existsByEmail("usuario1@test.com")).thenReturn(true);

        // Act
        boolean resultado = usuarioService.existeUsuarioPorEmail("usuario1@test.com");

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsByEmail("usuario1@test.com");
    }

    @Test
    void testExisteUsuarioPorEmail_NoExiste() {
        // Arrange
        when(usuarioRepository.existsByEmail("nuevo@test.com")).thenReturn(false);

        // Act
        boolean resultado = usuarioService.existeUsuarioPorEmail("nuevo@test.com");

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).existsByEmail("nuevo@test.com");
    }

    @Test
    void testCrearUsuario_Exitoso() {
        // Arrange
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("nuevo@test.com");
        nuevoUsuario.setPassword("password789");
        nuevoUsuario.setNombre("Carlos");
        nuevoUsuario.setActivo(null); // Para probar que se establece en true

        when(usuarioRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(3L);
            return u;
        });

        // Act
        Usuario resultado = usuarioService.crearUsuario(nuevoUsuario);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getActivo()); // Debe establecerse en true
        verify(usuarioRepository, times(1)).existsByEmail("nuevo@test.com");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_EmailDuplicado() {
        // Arrange
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("usuario1@test.com");
        when(usuarioRepository.existsByEmail("usuario1@test.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(nuevoUsuario);
        });
        verify(usuarioRepository, times(1)).existsByEmail("usuario1@test.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_Exitoso() {
        // Arrange
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setEmail("usuario1@test.com");
        usuarioActualizado.setNombre("Juan Actualizado");
        usuarioActualizado.setApellido("Pérez Actualizado");
        usuarioActualizado.setPassword("nuevopassword");
        usuarioActualizado.setActivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.actualizarUsuario(1L, usuarioActualizado);

        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_NoExiste() {
        // Arrange
        Usuario usuarioActualizado = new Usuario();
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(999L, usuarioActualizado);
        });
        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_EmailDuplicado() {
        // Arrange
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setEmail("usuario2@test.com"); // Email diferente pero existente

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("usuario2@test.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(1L, usuarioActualizado);
        });
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).existsByEmail("usuario2@test.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario() {
        // Act
        usuarioService.eliminarUsuario(1L);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDesactivarUsuario() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.desactivarUsuario(1L);

        // Assert
        assertFalse(usuario.getActivo());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testActualizarUltimoAcceso() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.actualizarUltimoAcceso(1L);

        // Assert
        assertNotNull(usuario.getUltimoAcceso());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testValidarCredenciales_Correctas() {
        // Arrange
        when(usuarioRepository.findByEmailAndActivoTrue("usuario1@test.com"))
                .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = usuarioService.validarCredenciales("usuario1@test.com", "password123");

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).findByEmailAndActivoTrue("usuario1@test.com");
    }

    @Test
    void testValidarCredenciales_Incorrectas() {
        // Arrange
        when(usuarioRepository.findByEmailAndActivoTrue("usuario1@test.com"))
                .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = usuarioService.validarCredenciales("usuario1@test.com", "passwordincorrecto");

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).findByEmailAndActivoTrue("usuario1@test.com");
    }

    @Test
    void testValidarCredenciales_UsuarioNoExiste() {
        // Arrange
        when(usuarioRepository.findByEmailAndActivoTrue("noexiste@test.com"))
                .thenReturn(Optional.empty());

        // Act
        boolean resultado = usuarioService.validarCredenciales("noexiste@test.com", "password123");

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).findByEmailAndActivoTrue("noexiste@test.com");
    }
}

