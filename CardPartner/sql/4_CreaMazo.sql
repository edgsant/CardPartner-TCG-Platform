-- 1. LIMPIEZA TOTAL
-- Borramos primero las relaciones para evitar conflictos, aunque ON DELETE CASCADE debería gestionarlo
DELETE FROM CONTIENE_CARTA;
DELETE FROM MAZO;
ALTER TABLE MAZO AUTO_INCREMENT = 1;

SET @creador = 1;
-- --------------------------------------------------------------------
-- BLOQUE 1: MAZO MARSHALL D. TEACH (Comunidad)
-- ---------------------------------------------------------------------
INSERT INTO MAZO (nombre_mazo, descripcion, id_usuario_creador, id_carta_lider, es_publico, es_oficial) 
VALUES (
    'Marshall D. Teach Control', 
    'ESTRATEGIA DE DESCARTE Y CONTROL DE MANO USANDO A BARBANEGRA.', 
    @creador, 
    'OP09-081', 
    1,
    0
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
-- BLOQUE 2: MAZO NAMI MILL (Comunidad)
-- ---------------------------------------------------------------------
INSERT INTO MAZO (nombre_mazo, descripcion, id_usuario_creador, id_carta_lider, es_publico, es_oficial) 
VALUES (
    'Nami Mill Control', 
    'MAZO CON CONDICIÓN DE VICTORIA ALTERNATIVA: GANAR AL VACIAR EL MAZO PROPIO.', 
    @creador, 
    'OP03-040', 
    1,
    0
);

SET @id_nami = LAST_INSERT_ID();

INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES 
(@id_nami, 'OP03-040', 1), (@id_nami, 'OP03-044', 4), (@id_nami, 'OP04-041', 4),
(@id_nami, 'OP09-050', 4), (@id_nami, 'OP04-050', 2), (@id_nami, 'OP03-047', 2),
(@id_nami, 'OP01-088', 4), (@id_nami, 'OP02-066', 4), (@id_nami, 'OP03-054', 4),
(@id_nami, 'OP03-055', 4), (@id_nami, 'ST03-017', 4), (@id_nami, 'OP06-059', 4),
(@id_nami, 'OP07-055', 3), (@id_nami, 'OP02-067', 2), (@id_nami, 'OP03-056', 4),
(@id_nami, 'OP02-069', 1);

-- ---------------------------------------------------------------------
-- BLOQUE 3: MAZO OP10-001 (Oficial Meta)
-- ---------------------------------------------------------------------
INSERT INTO MAZO (nombre_mazo, descripcion, id_usuario_creador, id_carta_lider, es_publico, es_oficial, winrate) 
VALUES (
    'Mazo Meta OP10-001', 
    'Mazo competitivo oficial basado en el líder OP10-001.', 
    @creador, 
    'OP10-001', 
    1, 
    1, 
    55.0
);

SET @id_mazo_3 = LAST_INSERT_ID();

INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES 
(@id_mazo_3, 'OP10-001', 1), (@id_mazo_3, 'OP10-004', 4), (@id_mazo_3, 'OP11-004', 4),
(@id_mazo_3, 'OP10-011', 4), (@id_mazo_3, 'PRB02-001', 4), (@id_mazo_3, 'PRB02-002', 3),
(@id_mazo_3, 'OP12-034', 4), (@id_mazo_3, 'OP10-027', 2), (@id_mazo_3, 'OP12-027', 1),
(@id_mazo_3, 'OP10-032', 4), (@id_mazo_3, 'OP10-030', 4), (@id_mazo_3, 'OP12-118', 3),
(@id_mazo_3, 'OP12-030', 4), (@id_mazo_3, 'ST21-017', 3), (@id_mazo_3, 'OP12-037', 3),
(@id_mazo_3, 'OP04-035', 3);


-- ---------------------------------------------------------------------
-- BLOQUE 4: MAZO OP12-020 (Oficial Meta)
-- ---------------------------------------------------------------------
INSERT INTO MAZO (nombre_mazo, descripcion, id_usuario_creador, id_carta_lider, es_publico, es_oficial, winrate) 
VALUES (
    'Mazo Meta OP12-020', 
    'Mazo competitivo oficial basado en el líder OP12-020.', 
    @creador, 
    'OP12-020', 
    1, 
    1, 
    54.0
);

SET @id_mazo_4 = LAST_INSERT_ID();

INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES 
(@id_mazo_4, 'OP12-020', 1), (@id_mazo_4, 'OP12-028', 4), (@id_mazo_4, 'OP12-034', 4),
(@id_mazo_4, 'OP03-030', 4), (@id_mazo_4, 'ST12-007', 3), (@id_mazo_4, 'OP12-027', 3),
(@id_mazo_4, 'OP12-029', 4), (@id_mazo_4, 'OP03-033', 4), (@id_mazo_4, 'OP12-026', 4),
(@id_mazo_4, 'OP12-036', 3), (@id_mazo_4, 'EB01-012', 3), (@id_mazo_4, 'OP12-031', 4),
(@id_mazo_4, 'OP12-030', 2), (@id_mazo_4, 'OP05-037', 1), (@id_mazo_4, 'OP12-037', 1),
(@id_mazo_4, 'OP08-036', 2), (@id_mazo_4, 'OP12-039', 4);