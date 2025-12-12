# 🔧 Solución: Canchas Vacías en Laragon

## ✅ Solución Rápida (HeidiSQL)

### Paso 1: Abre HeidiSQL
Desde Laragon → Click derecho → **HeidiSQL**

### Paso 2: Conecta a la base de datos
- Selecciona la base de datos: **`db_canchas`**

### Paso 3: Ejecuta este SQL

```sql
USE db_canchas;

INSERT INTO canchas (nombre, descripcion, tipo, precio_por_hora, direccion, ciudad, activa, fecha_creacion, fecha_actualizacion) 
VALUES 
('Cancha Fútbol 1', 'Cancha de fútbol 11 con césped sintético', 'Fútbol', 45000.00, 'Av. Principal 1234', 'Santiago', 1, NOW(), NOW()),
('Cancha Fútbol 2', 'Cancha de fútbol 7 ideal para partidos rápidos', 'Fútbol', 35000.00, 'Calle Los Olivos 567', 'Santiago', 1, NOW(), NOW()),
('Cancha Fútbol 3', 'Cancha de fútbol 11 profesional', 'Fútbol', 55000.00, 'Av. Deportiva 890', 'Providencia', 1, NOW(), NOW()),
('Cancha Fútbol 4', 'Cancha de fútbol 5 techada', 'Fútbol', 40000.00, 'Calle Deportes 321', 'Las Condes', 1, NOW(), NOW());
```

### Paso 4: Verifica
```sql
SELECT * FROM canchas;
```

Deberías ver 4 filas.

---

## 🔍 Verificar que el Microservicio Funciona

### En el navegador:
Abre: **http://localhost:8081/api/canchas**

**Si ves un JSON con las canchas** → ✅ Todo funciona

**Si ves "No se puede acceder"** → ❌ El microservicio NO está corriendo

---

## ⚠️ Si el SQL da error

### Error: "Table doesn't exist"
**Solución:** El microservicio no ha creado la tabla aún.
1. Asegúrate que el microservicio esté corriendo
2. Espera 10-20 segundos después de iniciarlo
3. Vuelve a ejecutar el SQL

### Error: "Duplicate entry"
**Solución:** Las canchas ya existen. Para empezar de cero:
```sql
DELETE FROM canchas;
```
Luego ejecuta el INSERT de nuevo.

---

## 📱 Verificar en la App Android

Después de insertar las canchas:
1. **Rebuild** la app Android
2. **Abre la pantalla de Administrador**
3. Deberías ver las 4 canchas

---

## 🎯 Resumen

1. ✅ Microservicio corriendo (puerto 8081)
2. ✅ Tabla `canchas` existe en `db_canchas`
3. ✅ Ejecutar SQL para insertar las 4 canchas
4. ✅ Verificar en navegador: http://localhost:8081/api/canchas
5. ✅ Rebuild app Android
