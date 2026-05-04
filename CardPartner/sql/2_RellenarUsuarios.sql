-- =====================================================================
-- INSERCIÓN DE DATOS DE PRUEBA: TABLA USUARIO
-- =====================================================================
INSERT IGNORE INTO USUARIO (apodo_usuario, email, password, rol) VALUES 
('admin', 'admin@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SUPERADMIN'),
('sergio', 'sergio@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'MODERADOR'),
('marcos', 'marcos@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'EDITOR'),
('alvaro', 'admin@cardpartner.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SUPERADMIN'),
('edgar', 'edgar@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SUPERADMIN'),
('xabi', 'xabi@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'JUGADOR');
-- Las passwords estan hasheadas, son 123456