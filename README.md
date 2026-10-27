# Card Partner: All-in-One TCG Platform

Una aplicación web centralizada para jugadores y aficionados a los juegos de cartas coleccionables (TCG), tomando como referencia inicial el One Piece TCG. La plataforma unifica catálogo de cartas, mercado, construcción de mazos y funciones de comunidad en una única experiencia fluida.

## 🚀 Características Principales
*   **Constructor de Mazos (Deckbuilder):** Creación interactiva con filtros automáticos basados en el color/arquetipo del líder.
*   **Catálogo Técnico Automatizado:** Sincronización de más de 2000 cartas mediante consumo REST de la OPTCG API, parseando JSONs masivos con GSON.
*   **Comunidad y Gestión de Colecciones:** Interacción social mediante comentarios y valoraciones, además de listas de deseos (wishlist) y guardado de mazos meta/comunidad.
*   **Traducción Contextual con IA:** Integración de Llama-3 (vía Groq) para traducir comentarios y jerga técnica específica de los TCG (como "Ramp" u "OTK").
*   **Gestión de Usuarios y Roles:** Diferenciación de accesos para visitantes, jugadores registrados y administradores (gestión de torneos, noticias y guías).

## 🛠️ Stack Tecnológico
*   **Backend:** Java (JSP y Servlets) bajo arquitectura MVC.
*   **Persistencia:** Base de datos relacional MariaDB, gestionada mediante patrones DAO y DTO.
*   **Frontend:** HTML5, CSS3.
*   **Entorno:** Apache NetBeans y servidor Tomcat.

## ⚙️ Instalación y Despliegue Local
Al no estar desplegada en producción, puedes ejecutar esta aplicación en un entorno local siguiendo estos pasos:
1. Clonar el repositorio.
2. Configurar una base de datos local **MariaDB** (por ejemplo, mediante XAMPP).
3. Ejecutar los scripts SQL proporcionados en la carpeta correspondiente para generar la estructura de la base de datos y poblar los datos iniciales de las cartas (usando `IGNORE` para evitar duplicados).
4. Configurar la conexión en el archivo `context.xml` de **Apache NetBeans**.
5. Construir y ejecutar el proyecto sobre un servidor **Tomcat**.

## 👥 Créditos y Contribuidores
Este proyecto fue desarrollado íntegramente como práctica para la asignatura de Servicios y Sistemas Web (Grado en Ingeniería Informática, Universidad de Valladolid) por el Grupo 33. 
Debido a la migración del repositorio original para su publicación en este portafolio, el historial de *commits* no refleja la autoría conjunta. Los desarrolladores del sistema son:
*   **Marcos Álvarez Fulconis:** Frontend (Vistas de cartas, apartado social y navegabilidad general).
*   **Sergio Otero García:** Frontend (Vistas de mazos, login, registro y constructor de mazos).
*   **Álvaro López Primo:** Backend & BBDD (Esquema MariaDB, automatización de scripts locales y población de datos).
*   **Edgar Santiesteban Mendo:** Backend & BBDD (Clases del modelo, diseño de arquitectura, creación de DAOs, DTOs y modelo Entidad-Relación).
