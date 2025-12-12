# 📱 Instrucciones para Conectar Android con los Microservicios

## 🚀 Pasos para que funcione

### Paso 1: Iniciar Laragon
1. Abre **Laragon**
2. Haz clic en **"Start All"** o asegúrate de que **MySQL** esté corriendo (verde)

### Paso 2: Crear las bases de datos
Ejecuta el archivo `setup_database.sql` en HeidiSQL (viene con Laragon):
1. Abre Laragon → Click derecho → **HeidiSQL**
2. Conecta con usuario `root` (sin contraseña)
3. Abre el archivo `setup_database.sql` y ejecútalo (F9)

O simplemente ejecuta `iniciar_microservicios.bat` que lo hace automáticamente.

### Paso 3: Iniciar los microservicios
**Opción A (Recomendada):** Doble clic en `iniciar_microservicios.bat`

**Opción B (Manual):** Abre 4 terminales y ejecuta en cada una:
```bash
# Terminal 1 - Canchas
cd Canchas
.\mvnw spring-boot:run

# Terminal 2 - Disponibilidad  
cd Disponibilidad
.\mvnw spring-boot:run

# Terminal 3 - Login
cd Login
.\mvnw spring-boot:run

# Terminal 4 - Reservas
cd Reservas
.\mvnw spring-boot:run
```

### Paso 4: Verificar que funcionan
Ejecuta `verificar_servicios.bat` o abre en tu navegador:
- http://localhost:8081/swagger-ui.html (Canchas)
- http://localhost:8082/swagger-ui.html (Disponibilidad)
- http://localhost:8083/swagger-ui.html (Login)
- http://localhost:8084/swagger-ui.html (Reservas)

---

## 📱 Configuración en Android Studio

### Si usas EMULADOR:
La configuración ya está correcta. El emulador usa `10.0.2.2` para acceder al localhost de tu PC.

### Si usas DISPOSITIVO FÍSICO:
1. Ejecuta `verificar_servicios.bat` para ver tu IP
2. Edita `ApiConfig.kt` en tu proyecto Android:

```kotlin
// Cambia esto:
private const val BASE_HOST_DEVICE = "TU_IP_AQUI"  // Ej: "192.168.1.50"
private const val CURRENT_HOST = BASE_HOST_DEVICE  // Cambia a device
```

3. Tu PC y celular deben estar en la **misma red WiFi**

### Permisos en AndroidManifest.xml
Asegúrate de tener estos permisos:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

<application
    android:usesCleartextTraffic="true"
    ...>
```

---

## 🔧 Solución de Problemas

### "No se puede conectar" o "Connection refused"
- ✅ Verifica que los microservicios estén corriendo (ventanas de CMD abiertas)
- ✅ Verifica que MySQL esté corriendo en Laragon
- ✅ Si usas dispositivo físico, verifica la IP

### "Las tablas no existen"
Las tablas se crean automáticamente cuando inicia cada microservicio.
Solo necesitas crear las bases de datos (db_login, db_canchas, etc.)

### Los datos no se guardan
1. Verifica que el microservicio de Login (8083) esté corriendo
2. Mira el Logcat en Android Studio para ver errores
3. Mira la consola del microservicio para ver errores de SQL

### Usuario administrador por defecto
- **Email:** Admin@admin.cl
- **Password:** Admin123

---

## 📋 Resumen de Puertos

| Microservicio   | Puerto | URL Base                    |
|-----------------|--------|------------------------------|
| Canchas         | 8081   | http://localhost:8081/       |
| Disponibilidad  | 8082   | http://localhost:8082/       |
| Login           | 8083   | http://localhost:8083/       |
| Reservas        | 8084   | http://localhost:8084/       |

---

## 🔄 Flujo de datos

```
Android App 
    ↓ (HTTP Request)
Microservicio Spring Boot (ej: puerto 8083)
    ↓ (JPA/Hibernate)
MySQL en Laragon (puerto 3306)
    ↓
Base de datos (ej: db_login)
```

**IMPORTANTE:** Los microservicios Spring Boot DEBEN estar corriendo para que Android pueda guardar datos.
