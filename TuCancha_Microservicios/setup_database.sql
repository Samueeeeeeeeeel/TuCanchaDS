-- ============================================================
-- SCRIPT DE INICIALIZACIÓN - TUCANCHA MICROSERVICIOS
-- Ejecutar este script en HeidiSQL o phpMyAdmin de Laragon
-- ============================================================

-- Crear base de datos para el microservicio Canchas
CREATE DATABASE IF NOT EXISTS db_canchas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Crear base de datos para el microservicio Disponibilidad
CREATE DATABASE IF NOT EXISTS db_disponibilidad
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Crear base de datos para el microservicio Login
CREATE DATABASE IF NOT EXISTS db_login
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Crear base de datos para el microservicio Reservas
CREATE DATABASE IF NOT EXISTS db_reservas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ============================================================
-- VERIFICACIÓN
-- ============================================================
SELECT 'Bases de datos creadas exitosamente:' AS Mensaje;
SHOW DATABASES LIKE 'db_%';

-- ============================================================
-- NOTAS IMPORTANTES:
-- ============================================================
-- 1. Las tablas se crean automáticamente cuando inician los microservicios
--    gracias a: spring.jpa.hibernate.ddl-auto=update
--
-- 2. El usuario administrador por defecto se crea automáticamente:
--    Email: Admin@admin.cl
--    Password: Admin123
--
-- 3. Puertos de los microservicios:
--    - Canchas: 8081
--    - Disponibilidad: 8082
--    - Login: 8083
--    - Reservas: 8084
-- ============================================================
