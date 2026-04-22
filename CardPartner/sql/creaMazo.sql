-- =====================================================================
-- SCRIPT DE REINICIO: LIMPIEZA TOTAL E INSERCIÓN DE TEACH & NAMI
-- =====================================================================

-- 1. LIMPIEZA: Borramos todos los mazos existentes. 
-- Gracias al ON DELETE CASCADE, esto vacía también CONTIENE_CARTA.
DELETE FROM MAZO;

-- Reiniciamos el contador de IDs para que empiecen desde 1 otra vez (Opcional)
ALTER TABLE MAZO AUTO_INCREMENT = 1;

-- ---------------------------------------------------------------------
-- BLOQUE 1: MAZO MARSHALL D. TEACH (Barbanegra)
-- ---------------------------------------------------------------------
SET @creador = 1; 

INSERT INTO MAZO (nombre_mazo, descripcion, id_usuario_creador, id_carta_lider, es_publico) 
VALUES (
    'Marshall D. Teach Control', 
    'ESTRATEGIA DE DESCARTE Y CONTROL DE MANO USANDO A BARBANEGRA.', 
    @creador, 
    'OP09-081', 
    1
);

SET @id_teach = LAST_INSERT_ID();

INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES 
(@id_teach, 'OP09-081', 1), (@id_teach, 'OP05-086', 4), (@id_teach, 'OP09-095', 4),
(@id_teach, 'OP11-083', 4), (@id_teach, 'OP09-089', 3), (@id_teach, 'OP09-088', 4),
(@id_teach, 'OP09-086', 4), (@id_teach, 'PRB02-015', 1), (@id_teach, 'OP10-082', 4),
(@id_teach, 'OP09-084', 1), (@id_teach, 'ST27-003', 4), (@id_teach, 'OP09-093', 4),
(@id_teach, 'OP09-096', 4), (@id_teach, 'OP09-097', 1), (@id_teach, 'OP09-098', 4),
(@id_teach, 'OP09-099', 4);


-- ---------------------------------------------------------------------
-- BLOQUE 2: MAZO NAMI (Mill Deck)
-- ---------------------------------------------------------------------
INSERT INTO MAZO (nombre_mazo, descripcion, id_usuario_creador, id_carta_lider, es_publico) 
VALUES (
    'Nami Mill Control', 
    'MAZO CON CONDICIÓN DE VICTORIA ALTERNATIVA: GANAR AL VACIAR EL MAZO PROPIO.', 
    @creador, 
    'OP03-040', 
    1
);

SET @id_nami = LAST_INSERT_ID();

INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES 
(@id_nami, 'OP03-040', 1), (@id_nami, 'OP03-044', 4), (@id_nami, 'OP04-041', 4),
(@id_nami, 'OP09-050', 4), (@id_nami, 'OP04-050', 2), (@id_nami, 'OP03-047', 2),
(@id_nami, 'OP01-088', 4), (@id_nami, 'OP02-066', 4), (@id_nami, 'OP03-054', 4),
(@id_nami, 'OP03-055', 4), (@id_nami, 'ST03-017', 4), (@id_nami, 'OP06-059', 4),
(@id_nami, 'OP07-055', 3), (@id_nami, 'OP02-067', 2), (@id_nami, 'OP03-056', 4),
(@id_nami, 'OP02-069', 1);

