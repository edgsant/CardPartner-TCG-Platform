-- =====================================================================
-- 1. LIMPIEZA DE DATOS PREVIOS
-- =====================================================================
-- Al no tener auto_increment (usan claves primarias compuestas), 
-- basta con hacer un DELETE simple.
DELETE FROM GUARDA_MAZO;

-- =====================================================================
-- 3. INSERCIÓN DE DATOS DE PRUEBA: TABLA GUARDA_MAZO
-- =====================================================================
-- Simula la "Biblioteca" o "Mazos Favoritos" de cada usuario
INSERT INTO GUARDA_MAZO (id_usuario, id_mazo) VALUES 
(6, 1), -- xabi guarda el mazo de Teach
(6, 2), -- xabi guarda el mazo de Nami
(6, 4), -- xabi guarda el mazo de Zoro
(3, 2), -- marcos (Editor) guarda el mazo de Nami
(5, 3), -- edgar guarda el mazo de Smoker
(1, 4); -- admin guarda el mazo de Zoro