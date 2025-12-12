-- Script para crear las bases de datos de los microservicios TuCancha
-- Ejecutar este script en MySQL antes de iniciar los microservicios

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

-- Mostrar las bases de datos creadas
SHOW DATABASES LIKE 'db_%';

