# 📱 Guía: Configurar el Proyecto en Otra PC

## 🔧 Pasos para Configurar en una Nueva PC

### 1. **Configurar la Aplicación Android**

Edita el archivo: `app/src/main/java/com/example/proyectocancha/data/remote/ApiConfig.kt`

```kotlin
// Si usas EMULADOR en esta PC:
private const val CURRENT_HOST = BASE_HOST_EMULATOR  // 10.0.2.2

// Si usas DISPOSITIVO FÍSICO o los microservicios están en OTRA PC:
private const val CURRENT_HOST = BASE_HOST_DEVICE  // Cambia la IP abajo

// ⚠️ IMPORTANTE: Cambia esta IP por la de la PC donde corren los microservicios
private const val BASE_HOST_DEVICE = "192.168.1.XXX"  // IP de la otra PC
```

**Para obtener la IP de la otra PC:**
- **Windows**: Abre CMD y ejecuta `ipconfig`, busca "IPv4 Address"
- **Mac/Linux**: Ejecuta `ifconfig` o `ip addr`

### 2. **Configurar los Microservicios (en la PC donde corren)**

Edita los archivos `application.properties` de cada microservicio:

**Ubicación:**
- `TuCancha_Microservicios/Canchas/src/main/resources/application.properties`
- `TuCancha_Microservicios/Login/src/main/resources/application.properties`
- `TuCancha_Microservicios/Reservas/src/main/resources/application.properties`

**Cambia las credenciales de MySQL si son diferentes:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_canchas
spring.datasource.username=root          # Tu usuario MySQL
spring.datasource.password=              # Tu contraseña MySQL
```

### 3. **Verificar que MySQL esté corriendo**

En la PC donde corren los microservicios:
- Asegúrate de que MySQL esté instalado y corriendo
- Verifica que las bases de datos existan:
  - `db_canchas`
  - `db_login`
  - `db_reservas`
  - `db_disponibilidad`

### 4. **Configurar el Firewall**

En la PC donde corren los microservicios, permite conexiones en:
- Puerto **8081** (Canchas)
- Puerto **8083** (Login/Usuarios)
- Puerto **8084** (Reservas)

**Windows:**
1. Abre "Firewall de Windows Defender"
2. "Configuración avanzada"
3. "Reglas de entrada" → "Nueva regla"
4. Puerto → TCP → 8081, 8083, 8084 → Permitir conexión

### 5. **Verificar que estén en la misma red**

- La PC con los microservicios y el dispositivo Android deben estar en la **misma red WiFi**
- Si usas emulador, debe estar en la misma PC donde corren los microservicios

## 📋 Checklist Rápido

- [ ] IP configurada en `ApiConfig.kt`
- [ ] Credenciales MySQL actualizadas en `application.properties`
- [ ] MySQL corriendo en la PC servidor
- [ ] Bases de datos creadas
- [ ] Microservicios corriendo (puertos 8081, 8083, 8084)
- [ ] Firewall configurado
- [ ] Misma red WiFi (si usas dispositivo físico)

## 🧪 Probar la Conexión

1. Abre el navegador en la PC servidor y verifica:
   - http://localhost:8081/api/canchas
   - http://localhost:8083/api/usuarios
   - http://localhost:8084/api/reservas

2. Desde otra PC en la misma red, prueba:
   - http://[IP_SERVIDOR]:8081/api/canchas
   - Ejemplo: http://192.168.1.100:8081/api/canchas

3. Si funciona en el navegador, la app Android también debería funcionar.

## ⚠️ Problemas Comunes

### "Unable to resolve host"
- Verifica que la IP en `ApiConfig.kt` sea correcta
- Asegúrate de que los microservicios estén corriendo

### "Connection refused"
- Verifica el firewall
- Asegúrate de que los puertos estén abiertos
- Verifica que estén en la misma red

### "Timeout"
- Verifica que la IP sea accesible desde tu dispositivo
- Prueba con ping: `ping [IP_SERVIDOR]`

