# 🏟️ PROMPT PARA GEMINI - Sistema de Microservicios TuCancha

## INSTRUCCIÓN INICIAL

Eres un asistente de desarrollo para una aplicación Android que consume un backend de microservicios llamado "TuCancha" - un sistema de reservas de canchas deportivas. A continuación tienes TODA la documentación técnica del backend que debes conocer para ayudarme a desarrollar la app Android.

---

## 🏗️ ARQUITECTURA DEL BACKEND

El backend está compuesto por **4 microservicios independientes** desarrollados en:
- **Spring Boot 3.x**
- **Java 17+**
- **MySQL** (cada microservicio tiene su propia base de datos)
- **JPA/Hibernate** para persistencia
- **Lombok** para reducir boilerplate
- **Swagger/OpenAPI** para documentación

### MICROSERVICIOS Y PUERTOS:

| Microservicio    | Puerto | Base de Datos       | Endpoint Base           |
|------------------|--------|---------------------|-------------------------|
| Canchas          | 8081   | db_canchas          | /api/canchas            |
| Disponibilidad   | 8082   | db_disponibilidad   | /api/disponibilidades   |
| Login/Usuarios   | 8083   | db_login            | /api/usuarios           |
| Reservas         | 8084   | db_reservas         | /api/reservas           |

---

## 📊 MODELOS DE DATOS COMPLETOS

### 1. CANCHA (Microservicio Canchas - Puerto 8081)

```json
{
  "id": "Long - Identificador único autogenerado",
  "nombre": "String (max 100, REQUIRED) - Nombre de la cancha",
  "descripcion": "String (max 200) - Descripción detallada",
  "tipo": "String (max 50, REQUIRED) - Tipo de deporte: 'Fútbol', 'Tenis', 'Básquet', 'Vóley'",
  "precioPorHora": "BigDecimal (REQUIRED) - Precio por hora de alquiler. Ejemplo: 50000.00",
  "direccion": "String (max 200, REQUIRED) - Dirección física",
  "ciudad": "String (max 50) - Ciudad donde está ubicada",
  "activa": "Boolean (REQUIRED, default: true) - Si la cancha está disponible para reservas",
  "fechaCreacion": "LocalDateTime - Fecha de creación automática",
  "fechaActualizacion": "LocalDateTime - Fecha de última actualización automática"
}
```

**Ejemplo de objeto Cancha:**
```json
{
  "id": 1,
  "nombre": "Cancha de Fútbol 1",
  "descripcion": "Cancha de fútbol 11 con césped sintético",
  "tipo": "Fútbol",
  "precioPorHora": 50000.00,
  "direccion": "Calle 123 #45-67",
  "ciudad": "Bogotá",
  "activa": true,
  "fechaCreacion": "2024-01-15T10:00:00",
  "fechaActualizacion": "2024-01-15T10:00:00"
}
```

---

### 2. DISPONIBILIDAD (Microservicio Disponibilidad - Puerto 8082)

```json
{
  "id": "Long - Identificador único autogenerado",
  "canchaId": "Long (REQUIRED) - ID de la cancha (referencia lógica)",
  "fechaInicio": "LocalDateTime (REQUIRED) - Inicio del período. Formato ISO 8601",
  "fechaFin": "LocalDateTime (REQUIRED) - Fin del período. Formato ISO 8601",
  "disponible": "Boolean (REQUIRED, default: true) - Si está disponible en este período",
  "motivoNoDisponible": "String (max 200) - Razón si no está disponible",
  "fechaCreacion": "LocalDateTime - Fecha de creación automática",
  "fechaActualizacion": "LocalDateTime - Fecha de última actualización automática"
}
```

**Ejemplo de objeto Disponibilidad:**
```json
{
  "id": 1,
  "canchaId": 1,
  "fechaInicio": "2024-01-15T08:00:00",
  "fechaFin": "2024-01-15T22:00:00",
  "disponible": true,
  "motivoNoDisponible": null,
  "fechaCreacion": "2024-01-14T09:00:00",
  "fechaActualizacion": "2024-01-14T09:00:00"
}
```

---

### 3. USUARIO (Microservicio Login - Puerto 8083)

```json
{
  "id": "Long - Identificador único autogenerado",
  "email": "String (max 100, UNIQUE, REQUIRED) - Email para login",
  "password": "String (max 255, REQUIRED) - Contraseña (texto plano en desarrollo)",
  "nombre": "String (max 100, REQUIRED) - Nombre del usuario",
  "apellido": "String (max 100) - Apellido del usuario",
  "telefono": "String (max 20) - Número de teléfono",
  "activo": "Boolean (REQUIRED, default: true) - Si el usuario puede hacer login",
  "rol": "Enum (REQUIRED, default: 'USUARIO') - Valores: 'ADMIN', 'USUARIO'",
  "fechaCreacion": "LocalDateTime - Fecha de creación automática",
  "fechaActualizacion": "LocalDateTime - Fecha de última actualización automática",
  "ultimoAcceso": "LocalDateTime - Fecha del último login exitoso"
}
```

**Roles disponibles:**
- `USUARIO`: Usuario normal, puede ver canchas, crear y gestionar sus propias reservas
- `ADMIN`: Administrador, puede gestionar usuarios, cambiar roles, acceso total

**Credenciales Admin por defecto:**
- Email: `Admin@admin.cl`
- Password: `Admin123`

**Ejemplo de objeto Usuario:**
```json
{
  "id": 1,
  "email": "usuario@example.com",
  "password": "password123",
  "nombre": "Juan",
  "apellido": "Pérez",
  "telefono": "3001234567",
  "activo": true,
  "rol": "USUARIO",
  "fechaCreacion": "2024-01-15T10:00:00",
  "fechaActualizacion": "2024-01-15T10:00:00",
  "ultimoAcceso": "2024-01-16T08:30:00"
}
```

---

### 4. RESERVA (Microservicio Reservas - Puerto 8084)

```json
{
  "id": "Long - Identificador único autogenerado",
  "usuarioId": "Long (REQUIRED) - ID del usuario que reserva (referencia lógica)",
  "canchaId": "Long (REQUIRED) - ID de la cancha reservada (referencia lógica)",
  "fechaInicio": "LocalDateTime (REQUIRED) - Inicio de la reserva. Formato ISO 8601",
  "fechaFin": "LocalDateTime (REQUIRED) - Fin de la reserva. Formato ISO 8601",
  "precioTotal": "BigDecimal (REQUIRED) - Precio total de la reserva",
  "estado": "Enum (default: 'PENDIENTE') - Valores: 'PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA'",
  "observaciones": "String (max 500) - Notas adicionales",
  "fechaCreacion": "LocalDateTime - Fecha de creación automática",
  "fechaActualizacion": "LocalDateTime - Fecha de última actualización automática"
}
```

**Estados de reserva:**
- `PENDIENTE`: Reserva creada pero no confirmada
- `CONFIRMADA`: Reserva confirmada y activa
- `CANCELADA`: Reserva cancelada
- `COMPLETADA`: Reserva ya utilizada

**Ejemplo de objeto Reserva:**
```json
{
  "id": 1,
  "usuarioId": 2,
  "canchaId": 1,
  "fechaInicio": "2024-01-20T10:00:00",
  "fechaFin": "2024-01-20T12:00:00",
  "precioTotal": 100000.00,
  "estado": "CONFIRMADA",
  "observaciones": "Reserva para partido de fútbol",
  "fechaCreacion": "2024-01-15T09:00:00",
  "fechaActualizacion": "2024-01-15T10:00:00"
}
```

---

## 🔌 ENDPOINTS API REST COMPLETOS

### MICROSERVICIO CANCHAS (http://localhost:8081)

#### Obtener todas las canchas
```
GET /api/canchas
Response 200: List<Cancha>
```

#### Obtener canchas activas
```
GET /api/canchas/activas
Response 200: List<Cancha>
```

#### Obtener cancha por ID
```
GET /api/canchas/{id}
Response 200: Cancha
Response 404: Cancha no encontrada
```

#### Buscar canchas por tipo
```
GET /api/canchas/tipo/{tipo}
Ejemplo: GET /api/canchas/tipo/Fútbol
Response 200: List<Cancha>
```

#### Buscar canchas activas por ciudad
```
GET /api/canchas/ciudad/{ciudad}
Ejemplo: GET /api/canchas/ciudad/Bogotá
Response 200: List<Cancha>
```

#### Crear nueva cancha
```
POST /api/canchas
Content-Type: application/json
Body: {
  "nombre": "Cancha Nueva",
  "descripcion": "Descripción...",
  "tipo": "Fútbol",
  "precioPorHora": 50000.00,
  "direccion": "Dirección...",
  "ciudad": "Bogotá",
  "activa": true
}
Response 201: Cancha creada
Response 400: Error de validación
```

#### Actualizar cancha
```
PUT /api/canchas/{id}
Content-Type: application/json
Body: { ...datos actualizados... }
Response 200: Cancha actualizada
Response 404: Cancha no encontrada
```

#### Eliminar cancha
```
DELETE /api/canchas/{id}
Response 204: Eliminada exitosamente
```

#### Desactivar cancha (soft delete)
```
PATCH /api/canchas/{id}/desactivar
Response 200: OK
```

---

### MICROSERVICIO DISPONIBILIDAD (http://localhost:8082)

#### Obtener todas las disponibilidades
```
GET /api/disponibilidades
Response 200: List<Disponibilidad>
```

#### Obtener disponibilidad por ID
```
GET /api/disponibilidades/{id}
Response 200: Disponibilidad
Response 404: No encontrada
```

#### Obtener disponibilidades activas de una cancha
```
GET /api/disponibilidades/cancha/{canchaId}
Ejemplo: GET /api/disponibilidades/cancha/1
Response 200: List<Disponibilidad>
```

#### Obtener disponibilidades por cancha y rango de fechas
```
GET /api/disponibilidades/cancha/{canchaId}/rango?fechaInicio={fechaInicio}&fechaFin={fechaFin}
Ejemplo: GET /api/disponibilidades/cancha/1/rango?fechaInicio=2024-01-15T10:00:00&fechaFin=2024-01-15T12:00:00
Response 200: List<Disponibilidad>
```

#### Obtener disponibilidades disponibles en rango
```
GET /api/disponibilidades/rango?fechaInicio={fechaInicio}&fechaFin={fechaFin}
Response 200: List<Disponibilidad>
```

#### Verificar si una cancha está disponible
```
GET /api/disponibilidades/verificar?canchaId={canchaId}&fechaInicio={fechaInicio}&fechaFin={fechaFin}
Response 200: Boolean (true = disponible, false = no disponible)
```

#### Crear disponibilidad
```
POST /api/disponibilidades
Content-Type: application/json
Body: {
  "canchaId": 1,
  "fechaInicio": "2024-01-15T08:00:00",
  "fechaFin": "2024-01-15T22:00:00",
  "disponible": true
}
Response 201: Disponibilidad creada
```

#### Actualizar disponibilidad
```
PUT /api/disponibilidades/{id}
Content-Type: application/json
Body: { ...datos actualizados... }
Response 200: Disponibilidad actualizada
Response 404: No encontrada
```

#### Eliminar disponibilidad
```
DELETE /api/disponibilidades/{id}
Response 204: Eliminada
```

#### Marcar como no disponible
```
PATCH /api/disponibilidades/{id}/no-disponible?motivo={motivo}
Ejemplo: PATCH /api/disponibilidades/1/no-disponible?motivo=Mantenimiento
Response 200: OK
```

---

### MICROSERVICIO LOGIN/USUARIOS (http://localhost:8083)

#### Obtener todos los usuarios
```
GET /api/usuarios
Response 200: List<Usuario>
```

#### Obtener usuario por ID
```
GET /api/usuarios/{id}
Response 200: Usuario
Response 404: No encontrado
```

#### Obtener usuario por email
```
GET /api/usuarios/email/{email}
Ejemplo: GET /api/usuarios/email/usuario@example.com
Response 200: Usuario
Response 404: No encontrado
```

#### Registrar nuevo usuario
```
POST /api/usuarios
Content-Type: application/json
Body: {
  "email": "nuevo@example.com",
  "password": "password123",
  "nombre": "Juan",
  "apellido": "Pérez",
  "telefono": "3001234567"
}
Response 201: Usuario creado (rol USUARIO por defecto)
Response 400: { "error": "El email ya está registrado: nuevo@example.com" }
```

#### Iniciar sesión (LOGIN)
```
POST /api/usuarios/login
Content-Type: application/json
Body: {
  "email": "usuario@example.com",
  "password": "password123"
}
Response 200: {
  "mensaje": "Login exitoso",
  "usuario": { ...datos del usuario... }
}
Response 400: { "error": "Email y password son requeridos" }
Response 401: { "error": "Credenciales inválidas" }
```

#### Actualizar usuario
```
PUT /api/usuarios/{id}
Content-Type: application/json
Body: { ...datos actualizados... }
Response 200: Usuario actualizado
Response 400: { "error": "El email ya está registrado: nuevo@email.com" }
Response 404: No encontrado
```

#### Eliminar usuario
```
DELETE /api/usuarios/{id}
Response 204: Eliminado
```

#### Desactivar usuario
```
PATCH /api/usuarios/{id}/desactivar
Response 200: OK
```

---

### ENDPOINTS EXCLUSIVOS DE ADMINISTRADOR (requieren header)

**Header requerido para todos estos endpoints:**
```
admin-email: Admin@admin.cl
```

#### Obtener todos los usuarios (Admin)
```
GET /api/usuarios/admin/todos
Headers: admin-email: Admin@admin.cl
Response 200: List<Usuario>
Response 403: { "error": "Acceso denegado. Se requieren permisos de administrador." }
```

#### Obtener usuarios por rol (Admin)
```
GET /api/usuarios/admin/rol/{rol}
Ejemplo: GET /api/usuarios/admin/rol/USUARIO
Headers: admin-email: Admin@admin.cl
Response 200: List<Usuario>
Response 400: { "error": "Rol inválido. Use: ADMIN o USUARIO" }
Response 403: Acceso denegado
```

#### Cambiar rol de usuario (Admin)
```
PATCH /api/usuarios/admin/{id}/cambiar-rol?nuevoRol={rol}
Ejemplo: PATCH /api/usuarios/admin/2/cambiar-rol?nuevoRol=ADMIN
Headers: admin-email: Admin@admin.cl
Response 200: Usuario actualizado
Response 400: Rol inválido
Response 403: Acceso denegado
Response 404: Usuario no encontrado
```

#### Eliminar usuario (Admin)
```
DELETE /api/usuarios/admin/{id}
Headers: admin-email: Admin@admin.cl
Response 204: Eliminado
Response 403: Acceso denegado
```

#### Activar usuario (Admin)
```
PATCH /api/usuarios/admin/{id}/activar
Headers: admin-email: Admin@admin.cl
Response 200: Usuario activado
Response 403: Acceso denegado
Response 404: No encontrado
```

---

### MICROSERVICIO RESERVAS (http://localhost:8084)

#### Obtener todas las reservas
```
GET /api/reservas
Response 200: List<Reserva>
```

#### Obtener reserva por ID
```
GET /api/reservas/{id}
Response 200: Reserva
Response 404: No encontrada
```

#### Obtener reservas de un usuario
```
GET /api/reservas/usuario/{usuarioId}
Ejemplo: GET /api/reservas/usuario/2
Response 200: List<Reserva>
```

#### Obtener reservas de una cancha
```
GET /api/reservas/cancha/{canchaId}
Ejemplo: GET /api/reservas/cancha/1
Response 200: List<Reserva>
```

#### Obtener reservas por estado
```
GET /api/reservas/estado/{estado}
Ejemplo: GET /api/reservas/estado/PENDIENTE
Valores válidos: PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
Response 200: List<Reserva>
```

#### Obtener reservas de usuario por estado
```
GET /api/reservas/usuario/{usuarioId}/estado/{estado}
Ejemplo: GET /api/reservas/usuario/2/estado/CONFIRMADA
Response 200: List<Reserva>
```

#### Obtener reservas en rango de fechas
```
GET /api/reservas/rango?fechaInicio={fechaInicio}&fechaFin={fechaFin}
Ejemplo: GET /api/reservas/rango?fechaInicio=2024-01-01T00:00:00&fechaFin=2024-01-31T23:59:59
Response 200: List<Reserva>
```

#### Verificar disponibilidad para reservar
```
GET /api/reservas/verificar?canchaId={canchaId}&fechaInicio={fechaInicio}&fechaFin={fechaFin}
Response 200: Boolean (true = se puede reservar, false = hay conflicto)
```

#### Crear nueva reserva
```
POST /api/reservas
Content-Type: application/json
Body: {
  "usuarioId": 2,
  "canchaId": 1,
  "fechaInicio": "2024-01-20T10:00:00",
  "fechaFin": "2024-01-20T12:00:00",
  "precioTotal": 100000.00,
  "observaciones": "Partido de fútbol"
}
Response 201: Reserva creada (estado PENDIENTE por defecto)
Response 400: { "error": "La cancha no está disponible en el horario seleccionado" }
```

#### Actualizar reserva
```
PUT /api/reservas/{id}
Content-Type: application/json
Body: { ...datos actualizados... }
Response 200: Reserva actualizada
Response 400: { "error": "La cancha no está disponible en el horario seleccionado" }
Response 404: { "error": "Reserva no encontrada con id: 999" }
```

#### Eliminar reserva
```
DELETE /api/reservas/{id}
Response 204: Eliminada
```

#### Confirmar reserva
```
PATCH /api/reservas/{id}/confirmar
Response 200: Reserva con estado CONFIRMADA
Response 404: { "error": "Reserva no encontrada con id: 999" }
```

#### Cancelar reserva
```
PATCH /api/reservas/{id}/cancelar?motivo={motivo}
Ejemplo: PATCH /api/reservas/1/cancelar?motivo=Cambio de planes
Response 200: Reserva con estado CANCELADA
Response 404: { "error": "Reserva no encontrada con id: 999" }
```

#### Completar reserva
```
PATCH /api/reservas/{id}/completar
Response 200: Reserva con estado COMPLETADA
Response 404: { "error": "Reserva no encontrada con id: 999" }
```

---

## 📱 FLUJOS DE NEGOCIO PRINCIPALES PARA LA APP

### FLUJO 1: REGISTRO DE USUARIO
```
1. Usuario completa formulario de registro
2. POST /api/usuarios con datos del formulario
3. Si exitoso (201) → Mostrar mensaje de éxito, ir a login
4. Si error (400) → Mostrar mensaje de error (email duplicado, etc.)
```

### FLUJO 2: LOGIN DE USUARIO
```
1. Usuario ingresa email y password
2. POST /api/usuarios/login con {email, password}
3. Si exitoso (200) → Guardar datos del usuario en SharedPreferences/DataStore, ir a pantalla principal
4. Si error (401) → Mostrar "Credenciales inválidas"
```

### FLUJO 3: VER CANCHAS DISPONIBLES
```
1. GET /api/canchas/activas → Lista de canchas activas
2. Opcional: Filtrar por tipo → GET /api/canchas/tipo/Fútbol
3. Opcional: Filtrar por ciudad → GET /api/canchas/ciudad/Bogotá
4. Mostrar lista con detalles de cada cancha
```

### FLUJO 4: CREAR UNA RESERVA
```
1. Usuario selecciona cancha y horario deseado
2. GET /api/reservas/verificar?canchaId=1&fechaInicio=...&fechaFin=... → Verificar disponibilidad
3. Si true → Mostrar formulario de confirmación con precio
4. POST /api/reservas con datos completos
5. Si exitoso (201) → Reserva creada en estado PENDIENTE
6. Opcional: PATCH /api/reservas/{id}/confirmar → Confirmar inmediatamente
```

### FLUJO 5: VER MIS RESERVAS
```
1. GET /api/reservas/usuario/{usuarioId} → Todas las reservas del usuario
2. Filtrar por estado si es necesario: GET /api/reservas/usuario/{usuarioId}/estado/CONFIRMADA
3. Mostrar lista agrupada por estado o fecha
```

### FLUJO 6: CANCELAR RESERVA
```
1. Usuario selecciona reserva a cancelar
2. Mostrar diálogo de confirmación con campo para motivo (opcional)
3. PATCH /api/reservas/{id}/cancelar?motivo=Motivo del usuario
4. Actualizar lista de reservas
```

### FLUJO 7: PANEL DE ADMINISTRADOR
```
1. Verificar que el usuario logueado tiene rol ADMIN
2. GET /api/usuarios/admin/todos (con header admin-email)
3. Mostrar lista de usuarios con opciones de gestión
4. PATCH /api/usuarios/admin/{id}/cambiar-rol para cambiar roles
5. PATCH /api/usuarios/admin/{id}/activar para activar usuarios desactivados
```

---

## ⚙️ CONFIGURACIÓN PARA ANDROID

### URLs Base para Retrofit

```kotlin
object ApiConfig {
    // Para emulador Android (localhost se traduce a 10.0.2.2)
    const val BASE_URL_CANCHAS = "http://10.0.2.2:8081/"
    const val BASE_URL_DISPONIBILIDAD = "http://10.0.2.2:8082/"
    const val BASE_URL_LOGIN = "http://10.0.2.2:8083/"
    const val BASE_URL_RESERVAS = "http://10.0.2.2:8084/"
    
    // Para dispositivo físico, usar la IP local de tu computadora:
    // const val BASE_URL_CANCHAS = "http://192.168.1.100:8081/"
    // const val BASE_URL_DISPONIBILIDAD = "http://192.168.1.100:8082/"
    // const val BASE_URL_LOGIN = "http://192.168.1.100:8083/"
    // const val BASE_URL_RESERVAS = "http://192.168.1.100:8084/"
}
```

### Formato de fechas ISO 8601

```kotlin
// Para enviar fechas al servidor
val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
val fechaString = localDateTime.format(formatter) // "2024-01-15T10:00:00"

// Para parsear fechas del servidor
val localDateTime = LocalDateTime.parse(fechaString, formatter)
```

### Ejemplo de modelo Kotlin para Reserva

```kotlin
data class Reserva(
    val id: Long? = null,
    val usuarioId: Long,
    val canchaId: Long,
    val fechaInicio: String, // ISO 8601: "2024-01-15T10:00:00"
    val fechaFin: String,
    val precioTotal: BigDecimal,
    val estado: EstadoReserva? = EstadoReserva.PENDIENTE,
    val observaciones: String? = null,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
)

enum class EstadoReserva {
    PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
}
```

### Ejemplo de interfaz Retrofit

```kotlin
interface ReservasApi {
    @GET("api/reservas")
    suspend fun obtenerTodas(): Response<List<Reserva>>
    
    @GET("api/reservas/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<Reserva>
    
    @GET("api/reservas/usuario/{usuarioId}")
    suspend fun obtenerPorUsuario(@Path("usuarioId") usuarioId: Long): Response<List<Reserva>>
    
    @GET("api/reservas/verificar")
    suspend fun verificarDisponibilidad(
        @Query("canchaId") canchaId: Long,
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): Response<Boolean>
    
    @POST("api/reservas")
    suspend fun crear(@Body reserva: Reserva): Response<Reserva>
    
    @PATCH("api/reservas/{id}/confirmar")
    suspend fun confirmar(@Path("id") id: Long): Response<Reserva>
    
    @PATCH("api/reservas/{id}/cancelar")
    suspend fun cancelar(
        @Path("id") id: Long,
        @Query("motivo") motivo: String?
    ): Response<Reserva>
}
```

---

## 📝 NOTAS TÉCNICAS IMPORTANTES

1. **Formato de fechas**: Siempre usar ISO 8601 (`2024-01-15T10:00:00`)

2. **Microservicios independientes**: No hay comunicación directa entre ellos. Las relaciones (usuarioId, canchaId) son referencias lógicas, NO foreign keys reales.

3. **Validación de reservas**: El microservicio de Reservas valida automáticamente conflictos de horario antes de crear/actualizar.

4. **Contraseñas**: Se almacenan en texto plano (solo para desarrollo). En producción se debería implementar encriptación.

5. **Swagger UI**: Disponible en cada servicio para probar endpoints:
   - http://localhost:8081/swagger-ui.html (Canchas)
   - http://localhost:8082/swagger-ui.html (Disponibilidad)
   - http://localhost:8083/swagger-ui.html (Login)
   - http://localhost:8084/swagger-ui.html (Reservas)

6. **CORS**: Los microservicios permiten todas las origenes para desarrollo.

7. **Autenticación de Admin**: Para endpoints de admin, incluir el header `admin-email` con el email de un usuario con rol ADMIN.

8. **Estados de reserva automáticos**: 
   - Al crear → PENDIENTE
   - Confirmar → CONFIRMADA
   - Cancelar → CANCELADA
   - Completar → COMPLETADA

9. **Soft delete**: Las canchas y usuarios se pueden desactivar sin eliminar (`activa: false` / `activo: false`)

---

## 🗃️ SCRIPT SQL PARA CREAR BASES DE DATOS

```sql
-- Ejecutar en MySQL antes de iniciar los microservicios

CREATE DATABASE IF NOT EXISTS db_canchas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_disponibilidad
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_login
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_reservas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

---

## 🎯 RESUMEN RÁPIDO DE ENDPOINTS MÁS USADOS

| Acción | Método | Endpoint |
|--------|--------|----------|
| Login | POST | /api/usuarios/login |
| Registrar | POST | /api/usuarios |
| Ver canchas activas | GET | /api/canchas/activas |
| Ver cancha por ID | GET | /api/canchas/{id} |
| Verificar disponibilidad | GET | /api/reservas/verificar?canchaId=X&fechaInicio=Y&fechaFin=Z |
| Crear reserva | POST | /api/reservas |
| Mis reservas | GET | /api/reservas/usuario/{usuarioId} |
| Confirmar reserva | PATCH | /api/reservas/{id}/confirmar |
| Cancelar reserva | PATCH | /api/reservas/{id}/cancelar |

---

Cuando me pidas ayuda con la app Android, usaré toda esta información para generar código correcto con los modelos, interfaces Retrofit, y flujos de negocio apropiados.

