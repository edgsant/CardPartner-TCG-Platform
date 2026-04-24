-- 1. LIMPIEZA DE DATOS PREVIOS
-- Borramos todo el contenido actual para eliminar los duplicados existentes
TRUNCATE TABLE ARTICULO;

-- 2. REFORZAR ESTRUCTURA (UNICIDAD)
-- Añadimos una restricción UNIQUE al título. 
-- Esto es lo que permite que el 'ON DUPLICATE KEY UPDATE' funcione de verdad.
-- Si el título ya existe, MariaDB sabrá que debe actualizar en lugar de insertar.
ALTER TABLE ARTICULO ADD UNIQUE INDEX idx_titulo_unico (titulo);

-- 3. INSERCIÓN Y ACTUALIZACIÓN DINÁMICA
-- Este comando insertará los registros la primera vez. 
-- Si vuelves a ejecutar el script, detectará el título duplicado y actualizará los campos.
INSERT INTO ARTICULO (titulo, descripcion, categoria, enlace_externo, portada, id_admin_autor) 
VALUES 
(
    'Championship 2026 - Global Events', 
    'Información oficial sobre el circuito de campeonatos mundiales de One Piece TCG para la temporada 2026.', 
    'TORNEO', 
    'https://en.onepiece-cardgame.com/events/2026/championship/', 
    'https://en.onepiece-cardgame.com/images/events/2026/championship/index/img_mv.webp?v2', 
    1
),
(
    'Treasure Cup May 2026', 
    'Calendario y detalles de premios para los eventos Treasure Cup que se disputarán en mayo de 2026.', 
    'TORNEO', 
    'https://en.onepiece-cardgame.com/events/treasure-cup-may-2026.html', 
    'https://en.onepiece-cardgame.com/onepiececg/bccard/en/news/2026/03/18/EUqM9uv282vnbJZU/img_mv.webp', 
    1
),
(
    'Deck Tech: Kalgara', 
    'Guía estratégica completa para el mazo de Kalgara.', 
    'GUIA', 
    'https://metafy.gg/guides/view/the-kalgara-guide-star-op11-star-RuUmLRpS4t4', 
    'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.ytimg.com%2Fvi%2FvFhH44x-dp8%2Fmaxresdefault.jpg&f=1&nofb=1&ipt=67ff08f72194ceddb0fa4c169366aeebc1cc9457bf4a4c378a1a6aa774631d33',
    1
),
(
    'Deck Tech: Nami biColor', 
    'Guía estratégica completa para el mazo de Nami, el mazo mas opresivo con lo mejor de los dos colores.', 
    'GUIA', 
    'https://onepiece.cardsrealm.com/es-es/articles/one-piece-tcg-deck-guide-blue-yellow-nami', 
    'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%2Fid%2FOIP.HhHGtoIm822_DMs3ws5mxgHaEK%3Fpid%3DApi&f=1&ipt=621ff2a67ed15d4cba98167dda7c9c85d0b62192b8ec022bf5f43fd810e62fd9&ipo=images', 
    1
),
(
    'Nueva expansión japonesa: Four Emperors [OP-09]', 
    'Anunciada la novena expansión del TCG en Japón, centrada en los Cuatro Emperadores. Detalles sobre cartas y arte.', 
    'NOTICIA', 
    'https://www.pokemillon.com/blogs/noticias/nueva-expansion-japonesa-de-one-piece-tcg-four-emperors-op-09', 
    'https://cdn.shopify.com/s/files/1/0608/8045/3878/files/OP01_480x480.png?v=1723582926', 
    1
),
(
    'Bandai anuncia rotación de sets para One Piece TCG', 
    'Noticia importante sobre el formato de juego: Bandai detalla cómo funcionará la rotación de colecciones próximamente.', 
    'NOTICIA', 
    'https://tcgrocks.com/article/bandai-announce-set-rotation-for-one-piece-tcg', 
    'https://gdm-universal-media.b-cdn.net/tcgrocks/1f4d2749ee064b799234a6a5e21b7fa200145472-1920x1080.jpg?width=1320', 
    1
),
(
    'Review del regional de Brasil', 
    'Resumen de todo lo acontecido en el torneo regional de una de las mejores regiones de Latam', 
    'NOTICIA', 
    'https://onepiece.cardsrealm.com/es-es/articles/reviewing-brazils-treasure-cup-results-from-february-15th', 
    'https://cdn.cardsrealm.com/images/uploads/1740054355.jpeg?&width=850', 
    1
)
ON DUPLICATE KEY UPDATE 
    descripcion = VALUES(descripcion), 
    categoria = VALUES(categoria), 
    enlace_externo = VALUES(enlace_externo), 
    portada = VALUES(portada);

