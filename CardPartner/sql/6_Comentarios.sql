-- =====================================================================
-- 1. LIMPIEZA DE DATOS PREVIOS
-- =====================================================================
DELETE FROM COMENTARIO;
ALTER TABLE COMENTARIO AUTO_INCREMENT = 1;

-- =====================================================================
-- 2. INSERCIÓN DE DATOS DE PRUEBA: TABLA COMENTARIO
-- =====================================================================
INSERT INTO COMENTARIO (contenido, id_usuario_autor, id_mazo_destino) VALUES 
-- Comentarios para Mazo 1 (ID 1): Marshall D. Teach Control
('¡Este mazo es una locura! La cantidad de control de mano que tiene Teach asfixia al rival rápidamente.', 6, 1), -- xabi (Jugador)
('Gracias por el aporte. Recuerda que contra mazos muy agresivos puedes sufrir en los primeros turnos si no robas bien.', 2, 1), -- sergio (Moderador)
('Lo he probado en el simulador y confirmo que es top tier. Muy opresivo.', 4, 1), -- alvaro (Superadmin)

-- Comentarios para Mazo 2 (ID 2): Nami Mill Control
('Llevo probando esta lista un par de días y me encanta. Ganar por vaciar el mazo es muy satisfactorio.', 6, 2), -- xabi (Jugador)
('He publicado un artículo nuevo con la guía actualizada por si quieres echarle un vistazo a los combos principales.', 3, 2), -- marcos (Editor)
('¿Alguien ha probado a meter más eventos defensivos? A veces me quedo a una carta de ganar y me destrozan.', 5, 2), -- edgar (Superadmin)

-- Comentarios para Mazo 3 (ID 3): Mazo Meta OP10-001 (Smoker)
('Este líder está rompiendo el meta actual. La sinergia que tiene con las cartas de la Marina es increíble.', 1, 3), -- admin (Superadmin)
('Totalmente de acuerdo, aunque creo que depende demasiado de salir primero para estabilizar la mesa.', 6, 3), -- xabi (Jugador)

-- Comentarios para Mazo 4 (ID 4): Mazo Meta OP12-020 (Zoro)
('Puro aggro. Si tienes buena mano inicial es imparable, pero sufre mucho contra mazos de control de mesa.', 6, 4), -- xabi (Jugador)
('Como siempre, Zoro demostrando por qué es uno de los mejores líderes del formato agresivo.', 2, 4); -- sergio (Moderador)