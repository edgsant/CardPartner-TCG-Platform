# 🚀 Guía de Inicio: Proyecto CardPartner

Bienvenido al repositorio de la asignatura de Servicios y Sistemas Web. [cite_start]En esta fase nos centramos exclusivamente en el análisis y en la creación de las páginas web estáticas[cite: 2598, 2602].

## 🛠️ 1. Lo que necesitas tener instalado
Asegúrate de tener en tu ordenador con Windows:
* **Git** (para bajarte el código).
* **Apache NetBeans**.
* **Apache Tomcat** (configurado dentro de NetBeans).

## 📥 2. Descargar el código
Abre la consola de comandos (o Git Bash) en la carpeta donde quieras guardar el proyecto y ejecuta esto paso a paso:

1. `git clone https://github.com/ALVPI/SSW.git´
2. `cd CardPartner`
3. `git checkout code` (Para movernos a la rama donde estamos programando)

## 💻 3. Abrir y configurar en NetBeans (¡Paso Clave!)
1. Abre NetBeans y ve a `File` > `Open Project...`.
2. Selecciona la carpeta `CardPartner` que acabas de descargar.
3. **Solucionar el error del servidor:** Como el proyecto se creó en otro ordenador, NetBeans no encontrará la ruta de tu Tomcat y el proyecto saldrá con un error rojo. 
   * Haz clic derecho sobre el proyecto `CardPartner`.
   * Selecciona **Resolve Missing Server...** (o ve a *Properties* > *Run*).
   * En el desplegable, elige el **Apache Tomcat** que tú tienes instalado.
   * Dale a "OK".

## ▶️ 4. Probar que funciona
Haz clic derecho en el proyecto y dale a **Run** (o al botón verde del "Play" arriba). Se abrirá tu navegador con nuestra página web.

## ⚠️ 5. Reglas de oro para programar (Según las instrucciones)
Todo nuestro código debe ir **obligatoriamente** dentro de la carpeta **`Web Pages`**. Por favor, respeta estas normas de la rúbrica:
* [cite_start]**Solo estático:** No añadas ninguna funcionalidad rara, son páginas completamente estáticas[cite: 2615].
* [cite_start]**Separación:** Todo el diseño y los colores van en archivos `.css` independientes de los ficheros `.html`[cite: 2616].
* **DATOS REALES:** Prohibido usar "Lorem Ipsum" o dejar plantillas vacías. [cite_start]Hay que usar datos concretos de los escenarios que hemos pensado[cite: 2617, 2614].
