# Configuración de Bases de Datos - TuCancha Microservicios

## Bases de Datos por Microservicio

Cada microservicio está configurado para usar su propia base de datos:

| Microservicio | Base de Datos | Puerto |
|--------------|--------------|--------|
| Canchas | `db_canchas` | 8081 |
| Disponibilidad | `db_disponibilidad` | 8082 |
| Login | `db_login` | 8083 |
| Reservas | `db_reservas` | 8084 |

## Configuración Inicial

### 1. Crear las Bases de Datos

Ejecuta el script SQL proporcionado:

```bash
mysql -u root -p < database_setup.sql
```

O ejecuta manualmente en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS db_canchas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_disponibilidad CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_login CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_reservas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar Credenciales

Si tu usuario y contraseña de MySQL son diferentes a los predeterminados, actualiza los archivos `application.properties` de cada microservicio:

**Ubicación de los archivos:**
- `Canchas/src/main/resources/application.properties`
- `Disponibilidad/src/main/resources/application.properties`
- `Login/src/main/resources/application.properties`
- `Reservas/src/main/resources/application.properties`

**Parámetros a modificar:**
```properties
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 3. Configuración Actual

Por defecto, la configuración está establecida para:
- **Host:** localhost
- **Puerto MySQL:** 3306
- **Usuario:** root
- **Contraseña:** (vacía)

### 4. Creación Automática de Tablas

Los microservicios están configurados con `spring.jpa.hibernate.ddl-auto=update`, lo que significa que:
- Las tablas se crearán automáticamente al iniciar cada microservicio
- Las tablas se actualizarán si hay cambios en los modelos
- **⚠️ IMPORTANTE:** En producción, cambia esto a `validate` o `none`

## Estructura de Tablas

Cada base de datos tendrá las siguientes tablas:

### db_canchas
- `canchas`

### db_disponibilidad
- `disponibilidades`

### db_login
- `usuarios`

### db_reservas
- `reservas`

## Verificación

Para verificar que las bases de datos se crearon correctamente:

```sql
SHOW DATABASES LIKE 'db_%';
```

Para verificar las tablas en cada base de datos:

```sql
USE db_canchas;
SHOW TABLES;

USE db_disponibilidad;
SHOW TABLES;

USE db_login;
SHOW TABLES;

USE db_reservas;
SHOW TABLES;
```

## Notas Importantes

1. **Seguridad:** En producción, no dejes la contraseña vacía y usa credenciales seguras.
2. **Backup:** Realiza backups regulares de cada base de datos.
3. **Conexiones:** Asegúrate de que MySQL esté ejecutándose antes de iniciar los microservicios.
4. **Puertos:** Cada microservicio usa un puerto diferente para evitar conflictos.

