-- ============================================================
-- SCRIPT PARA INSERTAR 4 CANCHAS DE FÚTBOL
-- IMPORTANTE: Ejecutar DESPUÉS de iniciar el microservicio de Canchas
-- El microservicio crea la tabla automáticamente con spring.jpa.hibernate.ddl-auto=update
-- ============================================================

USE db_canchas;

-- Verificar que la tabla existe
SHOW TABLES LIKE 'canchas';

-- Eliminar todas las canchas existentes (opcional - solo si quieres empezar de cero)
-- DELETE FROM canchas;

-- Insertar 4 canchas de fútbol
-- NOTA: Los nombres de columnas deben coincidir con el modelo Java
INSERT INTO canchas (nombre, descripcion, tipo, precio_por_hora, direccion, ciudad, activa, fecha_creacion, fecha_actualizacion) 
VALUES 
('Cancha Fútbol 1', 'Cancha de fútbol 11 con césped sintético de última generación. Iluminación LED para partidos nocturnos. Incluye vestuarios y duchas.', 'Fútbol', 45000.00, 'Av. Principal 1234', 'Santiago', 1, NOW(), NOW()),

('Cancha Fútbol 2', 'Cancha de fútbol 7 ideal para partidos rápidos. Superficie de césped natural. Perfecta para grupos pequeños.', 'Fútbol', 35000.00, 'Calle Los Olivos 567', 'Santiago', 1, NOW(), NOW()),

('Cancha Fútbol 3', 'Cancha de fútbol 11 profesional con graderías. Ideal para torneos y eventos deportivos. Incluye sistema de sonido.', 'Fútbol', 55000.00, 'Av. Deportiva 890', 'Providencia', 1, NOW(), NOW()),

('Cancha Fútbol 4', 'Cancha de fútbol 5 techada. Perfecta para jugar en cualquier clima. Incluye iluminación y vestuarios modernos.', 'Fútbol', 40000.00, 'Calle Deportes 321', 'Las Condes', 1, NOW(), NOW());

-- Verificar las canchas insertadas
SELECT id, nombre, tipo, precio_por_hora, ciudad, activa FROM canchas ORDER BY id;

-- Contar total de canchas
SELECT COUNT(*) as total_canchas FROM canchas;
