-- =====================================================================
-- CREACIÓN DE LA BASE DE DATOS Y SELECCIÓN
-- =====================================================================
CREATE DATABASE IF NOT EXISTS cardpartner_db;
USE cardpartner_db;

-- =====================================================================
-- 1. ENTIDADES PRINCIPALES
-- =====================================================================

-- Tabla: USUARIO (Agrupa jugadores y administradores)
CREATE TABLE USUARIO (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    apodo_usuario VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar MEDIUMBLOB, -- Permite imágenes de hasta 16MB
    rol ENUM('JUGADOR', 'SUPERADMIN', 'EDITOR', 'MODERADOR') NOT NULL DEFAULT 'JUGADOR'
);

-- Tabla: CARTA (Catálogo estático)
CREATE TABLE CARTA (
    id_carta VARCHAR(50) PRIMARY KEY, -- Optimizado para códigos tipo 'OP01-001'
    nombre_carta VARCHAR(255) NOT NULL,
    imagen VARCHAR(255), -- Ruta de la imagen (VARCHAR)
    tipo ENUM('LIDER', 'PERSONAJE', 'EVENTO', 'ESCENARIO') NOT NULL,
    arquetipo VARCHAR(255),
    color ENUM('ROJO', 'VERDE', 'AZUL', 'MORADO', 'NEGRO', 'AMARILLO', 'MULTICOLOR') NOT NULL,
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
    fecha_creacion DATE,
    precio_total DOUBLE, -- Nota: Atributo derivado, se podría calcular al vuelo
    es_publico TINYINT(1) DEFAULT 1, -- Usamos TINYINT(1) para booleanos en MySQL/MariaDB
    es_oficial TINYINT(1) DEFAULT 0,
    winrate FLOAT,
    id_usuario_creador INT NOT NULL, -- Obligatorio
    id_carta_lider VARCHAR(50) NOT NULL, -- Obligatorio
    FOREIGN KEY (id_usuario_creador) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_carta_lider) REFERENCES CARTA(id_carta)
);

-- Tabla: ARTICULO (Noticias, guías y torneos)
CREATE TABLE ARTICULO (
    id_articulo INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    categoria ENUM('NOTICIA', 'GUIA', 'TORNEO') NOT NULL,
    fecha_edicion DATE,
    enlace_externo VARCHAR(255),
    portada VARCHAR(255),
    id_admin_autor INT NOT NULL, -- Obligatorio
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
    puntuacion DOUBLE NOT NULL,
    fecha_valoracion DATE,
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
    fecha_edicion DATE,
    id_usuario_autor INT NOT NULL, -- Obligatorio
    id_mazo_destino INT NOT NULL, -- Obligatorio
    FOREIGN KEY (id_usuario_autor) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_mazo_destino) REFERENCES MAZO(id_mazo) ON DELETE CASCADE
);