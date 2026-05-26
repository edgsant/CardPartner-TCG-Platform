-- =====================================================================
-- INSERCIÓN DE DATOS DE PRUEBA: TABLA USUARIO
-- =====================================================================
INSERT INTO USUARIO (id_usuario, apodo_usuario, email, password, rol) VALUES 
(1, 'admin', 'admin@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SUPERADMIN'),
(2, 'sergio', 'sergio@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'MODERADOR'),
(3, 'marcos', 'marcos@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'EDITOR'),
(4, 'alvaro', 'admin@cardpartner.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SUPERADMIN'),
(5, 'edgar', 'edgar@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SUPERADMIN'),
(6, 'xabi', 'xabi@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'JUGADOR')
ON DUPLICATE KEY UPDATE 
    apodo_usuario = VALUES(apodo_usuario),
    email = VALUES(email),
    password = VALUES(password),
    rol = VALUES(rol);
-- Las passwords estan hasheadas, son 123456