package controladores;

import datos.MazoDAO;
import datos.CartaDAO;
import datos.ComentarioDAO;
import datos.ValoracionDAO;
import modelo.Mazo;
import modelo.ContieneCarta;
import modelo.Carta;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import modelo.Comentario;
import modelo.Usuario;
import modelo.Valoracion;

@WebServlet(name = "CatalogoMazosServlet", urlPatterns = {"/CatalogoMazosServlet"})
public class CatalogoMazosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "listar"; // Por defecto, mostramos la galería
        }

        switch (accion) {
            case "listar":
                mostrarCatalogo(request, response);
                break;
            case "detalle":
                mostrarDetalleMazo(request, response);
                break;
            default:
                response.sendRedirect("CatalogoMazosServlet?accion=listar");
                break;
        }
    }

    // ==========================================
    // ACCIÓN: LISTAR (La Galería con Filtros)
    // ==========================================
    private void mostrarCatalogo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recogemos parámetros del buscador/filtros del Front-End
        String nombreBuscado = request.getParameter("nombre");
        String idLider = request.getParameter("lider");
        String colorLider = request.getParameter("color");
        String tipoMazoStr = request.getParameter("tipo_mazo"); // Para saber si quieren "oficial" o "comunidad"

        // 2. Procesamos el tipo de mazo
        Boolean esOficial = null; // Por defecto null = muestra ambos
        if ("oficial".equals(tipoMazoStr)) {
            esOficial = true;
        } else if ("comunidad".equals(tipoMazoStr)) {
            esOficial = false;
        }

        try {

            MazoDAO mazoDAO = new MazoDAO();

            // 3. Buscamos los mazos (¡Ahora en un solo viaje a la BD con todos los filtros!)
            // Pasamos (esOficial, minWinrate, nombre, lider, color, limite, offset)
            int pagina = 1;
            int limite = 20;
            String paginaParam = request.getParameter("pagina");
            if (paginaParam != null && !paginaParam.isEmpty()) {
                try {
                    pagina = Integer.parseInt(paginaParam);
                    if (pagina < 1) {
                        pagina = 1;
                    }
                } catch (NumberFormatException e) {
                    pagina = 1;
                }
            }
            int offset = (pagina - 1) * limite;

            List<Mazo> listaMazos = mazoDAO.filtrarMazosPublicos(esOficial, null, nombreBuscado, idLider, colorLider, 20, 0);

            // Ya NO necesitamos el CartaDAO aquí, el MazoDAO ya incluye al líder dentro del objeto.
            // 4. Empaquetamos y mandamos a la vista
            request.setAttribute("listaMazosComunidad", listaMazos);

            // Pasamos variables para que el Front pueda mantener los filtros seleccionados
            request.setAttribute("tipoMazoActivo", tipoMazoStr);
            request.setAttribute("colorSeleccionado", colorLider);

            String destino = "/Views/community-deck.jsp";

            // Si el parámetro indica que quieren la vista META, cambiamos la ruta
            if ("oficial".equals(tipoMazoStr)) {
                destino = "/Views/meta-decks.jsp";
            }

            // Ejecutamos el envío a la ruta dinámica que hayamos decidido
            request.getRequestDispatcher(destino).forward(request, response);
        } catch (SQLException e) {
            System.err.println("Error en CatalogoMazosServlet al mostrar catalogo: " + e.getMessage());
            request.setAttribute("mensajeError", "No se pudo cargar la galería de mazos.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
            return;
        }
    }

    // ==========================================
    // ACCIÓN: DETALLE (Ver 1 mazo y sus 50 cartas)
    // ==========================================
    private void mostrarDetalleMazo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int idMazo = Integer.parseInt(idParam);
                MazoDAO mazoDAO = new MazoDAO();
                CartaDAO cartaDAO = new CartaDAO();

                // 1. Buscamos la información general del mazo
                Mazo mazoActual = mazoDAO.obtenerMazoPorId(idMazo);

                if (mazoActual != null) {
                    // 2. Buscamos a su líder para la portada
                    Carta lider = cartaDAO.obtenerCartaPorId(mazoActual.getIdCartaLider());
                    mazoActual.setCartaLider(lider);

                    // 3. ¡LA MAGIA! Buscamos las 50 cartas que lo componen
                    List<ContieneCarta> listaCartas = mazoDAO.obtenerCartasDeUnMazo(idMazo);

                    // 4. Empaquetamos todo y mandamos a la vista
                    request.setAttribute("mazoParaLaVista", mazoActual);
                    request.setAttribute("cartasDelMazo", listaCartas);

                    // Instanciamos los nuevos DAOs
                    ComentarioDAO comentarioDAO = new ComentarioDAO();
                    ValoracionDAO valoracionDAO = new ValoracionDAO();

                    // Cargamos la parte social
                    List<Comentario> listaComentarios = comentarioDAO.obtenerComentariosPorMazo(idMazo);
                    double notaMedia = valoracionDAO.obtenerMediaPorMazo(idMazo);
                    int totalVotos = valoracionDAO.contarVotosPorMazo(idMazo);

                    // Empaquetamos para el JSP
                    request.setAttribute("comentariosMazo", listaComentarios);
                    request.setAttribute("notaMedia", notaMedia);
                    request.setAttribute("totalVotos", totalVotos);

                    // OPCIONAL: Si el usuario está logueado, le pasamos su voto anterior para marcarle las estrellas
                    Usuario usuarioActivo = (Usuario) request.getSession().getAttribute("usuarioActivo");
                    if (usuarioActivo != null) {
                        Valoracion miVoto = valoracionDAO.obtenerValoracionUsuario(idMazo, usuarioActivo.getIdUsuario());
                        request.setAttribute("miVoto", miVoto);
                    }

                    request.getRequestDispatcher("/Views/deck-view.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // e.printStackTrace(); no ponerlo para evitar llenar la consola de ruido
            } catch (SQLException e) {

                System.err.println("Error en CatalogoMazosServlet (detalle): " + e.getMessage());
                request.setAttribute("mensajeError", "No hemos podido cargar los detalles de este mazo.");
                request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
                return;
            }
        }

        // Si hay error (mazo no existe o ID falso), devolvemos al catálogo
        response.sendRedirect("CatalogoMazosServlet?accion=listar&error=no_encontrado");
    }
}
