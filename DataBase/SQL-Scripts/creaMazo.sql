-- =====================================================================
-- SCRIPT UNIFICADO DE INSERCIÓN DE MAZOS (TEACH & NAMI)
-- =====================================================================

-- ---------------------------------------------------------------------
-- BLOQUE 1: MAZO MARSHALL D. TEACH (Barbanegra)
-- ---------------------------------------------------------------------

-- Definimos datos del primer mazo
SET @creador = 1; -- Asegúrate de que este usuario ID existe en tu tabla USUARIO

INSERT INTO MAZO (nombre_mazo, descripcion, id_usuario_creador, id_carta_lider, es_publico) 
VALUES (
    'Marshall D. Teach Control', 
    'Estrategia de descarte y control de mano usando a Barbanegra.', 
    @creador, 
    'OP09-081', 
    1
);

-- Capturamos el ID de Teach
SET @id_teach = LAST_INSERT_ID();

-- Insertamos sus cartas
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

-- Definimos datos del segundo mazo
INSERT INTO MAZO (nombre_mazo, descripcion, id_usuario_creador, id_carta_lider, es_publico) 
VALUES (
    'Nami Mill Control', 
    'Mazo con condición de victoria alternativa: ganar al vaciar el mazo propio.', 
    @creador, 
    'OP03-040', 
    1
);

-- Capturamos el ID de Nami (ahora LAST_INSERT_ID ya no vale @id_teach, sino el nuevo)
SET @id_nami = LAST_INSERT_ID();

-- Insertamos sus cartas
INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES 
(@id_nami, 'OP03-040', 1), (@id_nami, 'OP03-044', 4), (@id_nami, 'OP04-041', 4),
(@id_nami, 'OP09-050', 4), (@id_nami, 'OP04-050', 2), (@id_nami, 'OP03-047', 2),
(@id_nami, 'OP01-088', 4), (@id_nami, 'OP02-066', 4), (@id_nami, 'OP03-054', 4),
(@id_nami, 'OP03-055', 4), (@id_nami, 'ST03-017', 4), (@id_nami, 'OP06-059', 4),
(@id_nami, 'OP07-055', 3), (@id_nami, 'OP02-067', 2), (@id_nami, 'OP03-056', 4),
(@id_nami, 'OP02-069', 1);

-- =====================================================================
-- VERIFICACIÓN FINAL (Opcional: Ejecuta esto para ver si se han creado bien)
-- =====================================================================
SELECT id_mazo, nombre_mazo, id_carta_lider FROM MAZO WHERE id_mazo IN (@id_teach, @id_nami);
