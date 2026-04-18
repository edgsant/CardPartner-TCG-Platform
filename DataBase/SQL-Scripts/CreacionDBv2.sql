-- =====================================================================
-- CREACIÓN DE LA BASE DE DATOS Y SELECCIÓN
-- =====================================================================
CREATE DATABASE IF NOT EXISTS cardpartner_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cardpartner_db;

-- =====================================================================
-- 1. ENTIDADES PRINCIPALES
-- =====================================================================

-- Tabla: USUARIO (Agrupa jugadores y administradores)
CREATE TABLE USUARIO (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    apodo_usuario VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Nota: Deberá guardar un hash (bcrypt/argon2)
    avatar MEDIUMBLOB, -- Permite imágenes de hasta 16MB
    rol ENUM('JUGADOR', 'SUPERADMIN', 'EDITOR', 'MODERADOR') NOT NULL DEFAULT 'JUGADOR',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- CORRECCIÓN: Faltaba en el script
);

-- Tabla: CARTA (Catálogo estático)
CREATE TABLE CARTA (
    id_carta VARCHAR(50) PRIMARY KEY, 
    nombre_carta VARCHAR(255) NOT NULL,
    imagen VARCHAR(255), 
    tipo ENUM('LIDER', 'PERSONAJE', 'EVENTO', 'ESCENARIO', 'DON') NOT NULL, -- Añadido DON por si acaso
    arquetipo VARCHAR(255),
    color SET('ROJO', 'VERDE', 'AZUL', 'MORADO', 'NEGRO', 'AMARILLO', 'MULTICOLOR') NOT NULL,
    coste INT,
    poder INT,
    atributo_combate ENUM('SLASH', 'STRIKE', 'RANGED', 'SPECIAL', 'WISDOM'),
    precio_mercado DOUBLE
);

-- Tabla: MAZO (Agrupa mazos de comunidad y oficiales)
CREATE TABLE MAZO (
    id_mazo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_mazo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- MEJORA: Autogenerado
    -- Eliminado 'precio_total': Debe calcularse sumando el valor actual de las cartas
    es_publico TINYINT(1) DEFAULT 1, 
    es_oficial TINYINT(1) DEFAULT 0,
    winrate FLOAT DEFAULT 0.0,
    id_usuario_creador INT NOT NULL, 
    id_carta_lider VARCHAR(50) NOT NULL, 
    FOREIGN KEY (id_usuario_creador) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_carta_lider) REFERENCES CARTA(id_carta)
);

-- Tabla: ARTICULO (Noticias, guías y torneos)
CREATE TABLE ARTICULO (
    id_articulo INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    categoria ENUM('NOTICIA', 'GUIA', 'TORNEO') NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- CORRECCIÓN: Faltaba en el script
    fecha_edicion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP, -- MEJORA: Se actualiza solo
    enlace_externo VARCHAR(255),
    portada VARCHAR(255), 
    id_admin_autor INT NOT NULL, 
    FOREIGN KEY (id_admin_autor) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE
);

-- =====================================================================
-- 2. TABLAS INTERMEDIAS (Relaciones N:M y Asociativas)
-- =====================================================================

-- Tabla: CONTIENE_CARTA (Cartas dentro de un mazo)
CREATE TABLE CONTIENE_CARTA (
    id_mazo INT NOT NULL,
    id_carta VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id_mazo, id_carta),
    FOREIGN KEY (id_mazo) REFERENCES MAZO(id_mazo) ON DELETE CASCADE,
    FOREIGN KEY (id_carta) REFERENCES CARTA(id_carta) ON DELETE CASCADE
);

-- Tabla: GUARDA_MAZO (Biblioteca personal del jugador)
CREATE TABLE GUARDA_MAZO (
    id_usuario INT NOT NULL,
    id_mazo INT NOT NULL,
    PRIMARY KEY (id_usuario, id_mazo),
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_mazo) REFERENCES MAZO(id_mazo) ON DELETE CASCADE
);

-- Tabla: VALORACION (Puntuación de mazos)
CREATE TABLE VALORACION (
    id_usuario INT NOT NULL,
    id_mazo INT NOT NULL,
    puntuacion FLOAT NOT NULL, -- FLOAT es suficiente para puntuaciones de 1 a 5 o 1 a 10
    fecha_valoracion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_mazo),
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_mazo) REFERENCES MAZO(id_mazo) ON DELETE CASCADE
);

-- =====================================================================
-- 3. RELACIONES 1:N ADICIONALES
-- =====================================================================

-- Tabla: COMENTARIO
CREATE TABLE COMENTARIO (
    id_comentario INT AUTO_INCREMENT PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- CORRECCIÓN: Faltaba en el script
    fecha_edicion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP, -- MEJORA
    id_usuario_autor INT NOT NULL, 
    id_mazo_destino INT NOT NULL, 
    FOREIGN KEY (id_usuario_autor) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_mazo_destino) REFERENCES MAZO(id_mazo) ON DELETE CASCADE
);
