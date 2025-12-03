package com.example.Login.controller;

import com.example.Login.model.Rol;
import com.example.Login.model.Usuario;
import com.example.Login.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios y Autenticación", description = "API para gestión de usuarios, login y operaciones de administrador")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista con todos los usuarios registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }
    
    @Operation(summary = "Obtener usuario por ID", description = "Retorna los detalles de un usuario específico identificado por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado - El ID proporcionado no existe en el sistema",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(
            @Parameter(description = "ID del usuario a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Obtener usuario por email", description = "Retorna los detalles de un usuario específico identificado por su email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado - El email proporcionado no existe en el sistema",
                content = @Content)
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> obtenerUsuarioPorEmail(
            @Parameter(description = "Email del usuario a buscar", required = true, example = "usuario@example.com")
            @PathVariable String email) {
        return usuarioService.obtenerUsuarioPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema. El usuario se crea con rol USUARIO por defecto y estado activo.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación - El email ya está registrado o los datos proporcionados no son válidos",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"error\": \"El email ya está registrado: usuario@example.com\"}")))
    })
    @PostMapping
    public ResponseEntity<?> crearUsuario(
            @Parameter(description = "Datos del usuario a crear", required = true)
            @RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña. Si las credenciales son válidas, retorna los datos del usuario y actualiza su último acceso.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso - Credenciales válidas",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"mensaje\": \"Login exitoso\", \"usuario\": {...}}"))),
        @ApiResponse(responseCode = "400", description = "Error de validación - Email o password faltantes en el request",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Email y password son requeridos\"}"))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas - El email o password son incorrectos, o el usuario está inactivo",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Credenciales inválidas\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "Credenciales de acceso (email y password)", required = true,
                    schema = @Schema(example = "{\"email\": \"usuario@example.com\", \"password\": \"password123\"}"))
            @RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");
        
        if (email == null || password == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email y password son requeridos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        boolean valido = usuarioService.validarCredenciales(email, password);
        
        if (valido) {
            Usuario usuario = usuarioService.obtenerUsuarioActivoPorEmail(email).orElse(null);
            if (usuario != null) {
                usuarioService.actualizarUltimoAcceso(usuario.getId());
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Login exitoso");
                response.put("usuario", usuario);
                return ResponseEntity.ok(response);
            }
        }
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente. El rol no se puede cambiar desde este endpoint.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación - El email ya está registrado o los datos no son válidos",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"El email ya está registrado: nuevo@email.com\"}"))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado - El ID proporcionado no existe",
                content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @Parameter(description = "ID del usuario a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @Operation(summary = "Eliminar usuario", description = "Elimina permanentemente un usuario del sistema. Esta acción no se puede deshacer.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "ID del usuario a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Desactivar usuario", description = "Desactiva un usuario sin eliminarlo. Un usuario desactivado no podrá iniciar sesión.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente",
                content = @Content)
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarUsuario(
            @Parameter(description = "ID del usuario a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok().build();
    }
    
    // ========== ENDPOINTS EXCLUSIVOS DE ADMIN ==========
    
    @Operation(summary = "Obtener todos los usuarios (Admin)", description = "Retorna todos los usuarios del sistema. Requiere permisos de administrador. Debe incluir el header 'admin-email' con el email del administrador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - El usuario no tiene permisos de administrador o el header 'admin-email' es inválido",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Acceso denegado. Se requieren permisos de administrador.\"}")))
    })
    @GetMapping("/admin/todos")
    public ResponseEntity<?> obtenerTodosLosUsuariosAdmin(
            @Parameter(description = "Email del administrador (debe estar en el header 'admin-email')", required = true, example = "Admin@admin.cl")
            @RequestHeader("admin-email") String adminEmail) {
        if (!usuarioService.esAdminPorEmail(adminEmail)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Acceso denegado. Se requieren permisos de administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }
    
    @Operation(summary = "Obtener usuarios por rol (Admin)", description = "Retorna todos los usuarios con un rol específico (ADMIN o USUARIO). Requiere permisos de administrador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios del rol especificado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Rol inválido - El rol proporcionado no es válido. Use: ADMIN o USUARIO",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Rol inválido. Use: ADMIN o USUARIO\"}"))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - El usuario no tiene permisos de administrador",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Acceso denegado. Se requieren permisos de administrador.\"}")))
    })
    @GetMapping("/admin/rol/{rol}")
    public ResponseEntity<?> obtenerUsuariosPorRol(
            @Parameter(description = "Rol a filtrar (ADMIN o USUARIO)", required = true, example = "USUARIO")
            @PathVariable String rol,
            @Parameter(description = "Email del administrador (header)", required = true, example = "Admin@admin.cl")
            @RequestHeader("admin-email") String adminEmail) {
        if (!usuarioService.esAdminPorEmail(adminEmail)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Acceso denegado. Se requieren permisos de administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        try {
            Rol rolEnum = Rol.valueOf(rol.toUpperCase());
            return ResponseEntity.ok(usuarioService.obtenerUsuariosPorRol(rolEnum));
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Rol inválido. Use: ADMIN o USUARIO");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @Operation(summary = "Cambiar rol de usuario (Admin)", description = "Cambia el rol de un usuario (ADMIN o USUARIO). Solo los administradores pueden cambiar roles. Requiere permisos de administrador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol cambiado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Rol inválido - El rol proporcionado no es válido. Use: ADMIN o USUARIO",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Rol inválido. Use: ADMIN o USUARIO\"}"))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - El usuario no tiene permisos de administrador",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Acceso denegado. Se requieren permisos de administrador.\"}"))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado - El ID proporcionado no existe",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Usuario no encontrado con id: 999\"}")))
    })
    @PatchMapping("/admin/{id}/cambiar-rol")
    public ResponseEntity<?> cambiarRolUsuario(
            @Parameter(description = "ID del usuario cuyo rol se cambiará", required = true, example = "2")
            @PathVariable Long id,
            @Parameter(description = "Nuevo rol a asignar (ADMIN o USUARIO)", required = true, example = "ADMIN")
            @RequestParam String nuevoRol,
            @Parameter(description = "Email del administrador (header)", required = true, example = "Admin@admin.cl")
            @RequestHeader("admin-email") String adminEmail) {
        if (!usuarioService.esAdminPorEmail(adminEmail)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Acceso denegado. Se requieren permisos de administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        try {
            Rol rolEnum = Rol.valueOf(nuevoRol.toUpperCase());
            Usuario usuarioActualizado = usuarioService.cambiarRol(id, rolEnum);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Rol inválido. Use: ADMIN o USUARIO");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @Operation(summary = "Eliminar usuario (Admin)", description = "Elimina permanentemente un usuario del sistema. Solo administradores pueden eliminar usuarios. Esta acción no se puede deshacer.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - El usuario no tiene permisos de administrador",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Acceso denegado. Se requieren permisos de administrador.\"}")))
    })
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> eliminarUsuarioAdmin(
            @Parameter(description = "ID del usuario a eliminar", required = true, example = "2")
            @PathVariable Long id,
            @Parameter(description = "Email del administrador (header)", required = true, example = "Admin@admin.cl")
            @RequestHeader("admin-email") String adminEmail) {
        if (!usuarioService.esAdminPorEmail(adminEmail)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Acceso denegado. Se requieren permisos de administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Activar usuario (Admin)", description = "Activa un usuario que estaba desactivado. Solo administradores pueden activar usuarios.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario activado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - El usuario no tiene permisos de administrador",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Acceso denegado. Se requieren permisos de administrador.\"}"))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado - El ID proporcionado no existe",
                content = @Content)
    })
    @PatchMapping("/admin/{id}/activar")
    public ResponseEntity<?> activarUsuario(
            @Parameter(description = "ID del usuario a activar", required = true, example = "2")
            @PathVariable Long id,
            @Parameter(description = "Email del administrador (header)", required = true, example = "Admin@admin.cl")
            @RequestHeader("admin-email") String adminEmail) {
        if (!usuarioService.esAdminPorEmail(adminEmail)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Acceso denegado. Se requieren permisos de administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuario -> {
                    usuario.setActivo(true);
                    usuarioService.actualizarUsuario(id, usuario);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}