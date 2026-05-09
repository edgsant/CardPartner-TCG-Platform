-- =====================================================================
-- INSERCIÓN DE DATOS DE PRUEBA: TABLA WISHLIST
-- =====================================================================

-- Primero borramos los datos existentes por si ejecutamos el script varias veces
DELETE FROM WISHLIST;

INSERT IGNORE INTO WISHLIST (id_usuario, id_carta) VALUES 
-- El usuario 1 ('admin') quiere a Yamato y a Shanks
(1, 'OP06-022'), 
(1, 'OP09-001'), 

-- El usuario 2 ('sergio') quiere a Monkey.D.Luffy de OP05 y a Rosinante
(2, 'OP05-119'), 
(2, 'OP04-119'), 

-- El usuario 3 ('marcos') quiere a Boa Hancock
(3, 'OP07-051'), 

-- El usuario 4 ('alvaro') quiere a Nami de OP08
(4, 'OP08-106'), 

-- El usuario 5 ('edgar') quiere a Roronoa Zoro
(5, 'OP09-076'), 

-- El usuario 6 ('xabi') quiere a Lim y Trafalgar Law
(6, 'OP09-022'),
(6, 'OP09-030');