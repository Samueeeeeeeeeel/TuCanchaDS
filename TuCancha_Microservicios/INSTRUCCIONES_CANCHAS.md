# 📋 Instrucciones para Crear las 4 Canchas de Fútbol

## 🎯 Opción 1: Usar el Script PowerShell (RECOMENDADO)

### Pasos:
1. **Asegúrate que el microservicio de Canchas esté corriendo:**
   ```bash
   cd "c:\Users\sable\OneDrive\Escritorio\TuCancha_Microservicios\Canchas"
   mvnw spring-boot:run
   ```

2. **Espera a que inicie completamente** (verás "Started CanchasApplication")

3. **Abre PowerShell** y ejecuta:
   ```powershell
   cd "c:\Users\sable\OneDrive\Escritorio\TuCancha_Microservicios"
   .\crear_canchas.ps1
   ```

4. **Verifica** que se crearon las 4 canchas

---

## 🎯 Opción 2: Usar SQL Directo en HeidiSQL

### Pasos:
1. **Inicia el microservicio de Canchas** (para que cree la tabla automáticamente)

2. **Abre HeidiSQL** desde Laragon

3. **Conecta a la base de datos `db_canchas`**

4. **Ejecuta el script:**
   - Abre: `insertar_4_canchas_futbol.sql`
   - O copia y pega este SQL:

```sql
USE db_canchas;

INSERT INTO canchas (nombre, descripcion, tipo, precio_por_hora, direccion, ciudad, activa, fecha_creacion, fecha_actualizacion) 
VALUES 
('Cancha Fútbol 1', 'Cancha de fútbol 11 con césped sintético', 'Fútbol', 45000.00, 'Av. Principal 1234', 'Santiago', 1, NOW(), NOW()),
('Cancha Fútbol 2', 'Cancha de fútbol 7 ideal para partidos rápidos', 'Fútbol', 35000.00, 'Calle Los Olivos 567', 'Santiago', 1, NOW(), NOW()),
('Cancha Fútbol 3', 'Cancha de fútbol 11 profesional', 'Fútbol', 55000.00, 'Av. Deportiva 890', 'Providencia', 1, NOW(), NOW()),
('Cancha Fútbol 4', 'Cancha de fútbol 5 techada', 'Fútbol', 40000.00, 'Calle Deportes 321', 'Las Condes', 1, NOW(), NOW());
```

5. **Verifica:**
```sql
SELECT * FROM canchas;
```

---

## 🎯 Opción 3: Usar Postman o Insomnia

1. **Abre el archivo:** `crear_canchas_via_api.http`
2. **Copia las peticiones** a Postman o Insomnia
3. **Ejecuta cada POST** para crear las 4 canchas

---

## ✅ Verificar que Funciona

### En el navegador:
- **Todas las canchas:** http://localhost:8081/api/canchas
- **Solo activas:** http://localhost:8081/api/canchas/activas

### En la app Android:
- Deberías ver las 4 canchas en:
  - Pantalla principal
  - Pantalla "Ver Canchas"
  - Pantalla de Administrador

---

## ⚠️ Problemas Comunes

### "No hay canchas disponibles" en AdminScreen
1. Verifica que el microservicio de Canchas esté corriendo (puerto 8081)
2. Verifica que las canchas estén en la base de datos:
   ```sql
   SELECT * FROM db_canchas.canchas;
   ```
3. Prueba en el navegador: http://localhost:8081/api/canchas/activas

### "Error de conexión" en la app
1. Verifica que uses la IP correcta en `ApiConfig.kt`:
   - Emulador: `10.0.2.2`
   - Dispositivo físico: IP de tu PC
2. Verifica que el microservicio esté corriendo

### Las canchas no se crean
1. Asegúrate que el microservicio haya iniciado completamente
2. Verifica que la tabla `canchas` exista:
   ```sql
   SHOW TABLES IN db_canchas;
   ```
3. Si la tabla no existe, el microservicio la creará al iniciar (gracias a `spring.jpa.hibernate.ddl-auto=update`)
