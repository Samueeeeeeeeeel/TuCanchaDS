-- ============================================================
-- SCRIPT PARA INSERTAR CANCHAS DE EJEMPLO
-- Ejecutar en HeidiSQL después de iniciar el microservicio de Canchas
-- ============================================================

USE db_canchas;

-- Primero verificamos si ya existen canchas
SELECT COUNT(*) as total_canchas FROM canchas;

-- Insertar canchas de ejemplo (si no existen)
INSERT INTO canchas (nombre, descripcion, tipo, precio_por_hora, direccion, ciudad, activa, fecha_creacion, fecha_actualizacion) 
VALUES 
-- Canchas de Fútbol
('Cancha Fútbol Norte', 'Cancha de fútbol 11 con césped sintético de última generación. Iluminación LED para partidos nocturnos.', 'Fútbol', 45000.00, 'Av. Principal 1234', 'Santiago', true, NOW(), NOW()),

('Cancha Fútbol Sur', 'Cancha de fútbol 7 ideal para partidos rápidos. Incluye vestuarios y duchas.', 'Fútbol', 35000.00, 'Calle Los Olivos 567', 'Santiago', true, NOW(), NOW()),

('Stadium Central', 'Cancha profesional de fútbol 11 con graderías. Perfecta para torneos y eventos.', 'Fútbol', 65000.00, 'Av. Deportiva 890', 'Providencia', true, NOW(), NOW()),

-- Canchas de Tenis
('Club Tenis Premium', 'Cancha de tenis con superficie de arcilla. Incluye iluminación nocturna y raquetas disponibles.', 'Tenis', 25000.00, 'Calle Raqueta 123', 'Las Condes', true, NOW(), NOW()),

('Tenis Park', 'Cancha de tenis con piso duro. Ideal para principiantes y jugadores intermedios.', 'Tenis', 20000.00, 'Av. del Deporte 456', 'Ñuñoa', true, NOW(), NOW()),

-- Canchas de Básquet
('Basket Arena', 'Cancha de básquetbol techada con piso de madera profesional. Sistema de marcador electrónico.', 'Básquet', 30000.00, 'Calle Canasta 789', 'Santiago', true, NOW(), NOW()),

('Street Basketball', 'Cancha de básquet al aire libre estilo streetball. Perfecta para 3x3.', 'Básquet', 18000.00, 'Plaza Central 321', 'Maipú', true, NOW(), NOW()),

-- Canchas de Vóley
('Vóley Beach Club', 'Cancha de vóley playa con arena importada. Incluye red profesional y balones.', 'Vóley', 22000.00, 'Costanera 999', 'Viña del Mar', true, NOW(), NOW()),

('Vóley Indoor', 'Cancha de voleibol techada con piso especial antideslizante.', 'Vóley', 28000.00, 'Av. Olímpica 654', 'Santiago', true, NOW(), NOW());

-- Verificar las canchas insertadas
SELECT id, nombre, tipo, precio_por_hora, ciudad, activa FROM canchas;
