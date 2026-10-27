# Card Partner: All-in-One TCG Platform

Una aplicación web centralizada para jugadores y aficionados a los juegos de cartas coleccionables (TCG), tomando como referencia inicial el One Piece TCG[cite: 30, 58]. La plataforma unifica catálogo de cartas, mercado, construcción de mazos y funciones de comunidad en una única experiencia fluida[cite: 44, 58].

## 🚀 Características Principales
*   **Constructor de Mazos (Deckbuilder):** Creación interactiva con filtros automáticos basados en el color/arquetipo del líder[cite: 45, 49].
*   **Catálogo Técnico Automatizado:** Sincronización de más de 2000 cartas mediante consumo REST de la OPTCG API, parseando JSONs masivos con GSON[cite: 47].
*   **Comunidad y Gestión de Colecciones:** Interacción social mediante comentarios y valoraciones, además de listas de deseos (wishlist) y guardado de mazos meta/comunidad[cite: 45, 46].
*   **Traducción Contextual con IA:** Integración de Llama-3 (vía Groq) para traducir comentarios y jerga técnica específica de los TCG (como "Ramp" u "OTK")[cite: 45, 48].
*   **Gestión de Usuarios y Roles:** Diferenciación de accesos para visitantes, jugadores registrados y administradores (gestión de torneos, noticias y guías)[cite: 68, 69].

## 🛠️ Stack Tecnológico
*   **Backend:** Java (JSP y Servlets) bajo arquitectura MVC[cite: 46, 67].
*   **Persistencia:** Base de datos relacional MariaDB, gestionada mediante patrones DAO y DTO[cite: 46, 56, 67].
*   **Frontend:** HTML5, CSS3[cite: 16].
*   **Entorno:** Apache NetBeans y servidor Tomcat[cite: 71].

## ⚙️ Instalación y Despliegue Local
Al no estar desplegada en producción, puedes ejecutar esta aplicación en un entorno local siguiendo estos pasos:
1. Clonar el repositorio.
2. Configurar una base de datos local **MariaDB** (por ejemplo, mediante XAMPP)[cite: 79].
3. Ejecutar los scripts SQL proporcionados en la carpeta correspondiente para generar la estructura de la base de datos y poblar los datos iniciales de las cartas (usando `IGNORE` para evitar duplicados)[cite: 82, 83].
4. Configurar la conexión en el archivo `context.xml` de **Apache NetBeans**[cite: 81].
5. Construir y ejecutar el proyecto sobre un servidor **Tomcat**[cite: 71].

## 👥 Créditos y Contribuidores
Este proyecto fue desarrollado íntegramente como práctica para la asignatura de Servicios y Sistemas Web (Grado en Ingeniería Informática, Universidad de Valladolid) por el Grupo 33[cite: 41, 53]. 
Debido a la migración del repositorio original para su publicación en este portafolio, el historial de *commits* no refleja la autoría conjunta. Los desarrolladores del sistema son:
*   **Marcos Álvarez Fulconis:** Frontend (Vistas de cartas, apartado social y navegabilidad general)[cite: 57].
*   **Sergio Otero García:** Frontend (Vistas de mazos, login, registro y constructor de mazos)[cite: 57].
*   **Álvaro López Primo:** Backend & BBDD (Esquema MariaDB, automatización de scripts locales y población de datos)[cite: 57].
*   **Edgar Santiesteban Mendo:** Backend & BBDD (Clases del modelo, creación de DAOs, DTOs y modelo Entidad-Relación)[cite: 57].
