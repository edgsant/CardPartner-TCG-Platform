-- =====================================================================
-- 1. LIMPIEZA DE DATOS PREVIOS
-- =====================================================================
-- Al no tener auto_increment (usan claves primarias compuestas), 
-- basta con hacer un DELETE simple.
DELETE FROM VALORACION;

-- =====================================================================
-- 2. INSERCIÓN DE DATOS DE PRUEBA: TABLA VALORACION
-- =====================================================================
-- Recordatorio de IDs de Mazo: 1(Teach), 2(Nami), 3(Smoker), 4(Zoro)
INSERT INTO VALORACION (id_usuario, id_mazo, puntuacion) VALUES 
(6, 1, 4.5), -- xabi (Jugador) valora el mazo de Teach
(6, 2, 5.0), -- xabi (Jugador) valora el mazo de Nami (le encanta)
(2, 1, 4.0), -- sergio (Moderador) valora el mazo de Teach
(4, 3, 5.0), -- alvaro (Superadmin) valora el mazo Meta de Smoker
(1, 4, 3.5), -- admin (Superadmin) valora el mazo Meta de Zoro
(5, 3, 4.2); -- edgar (Superadmin) valora el mazo Meta de Smoker

