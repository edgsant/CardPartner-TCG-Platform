package controladores;

import datos.CartaDAO;
import modelo.Carta;

import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CartasServlet", urlPatterns = {"/CartasServlet"})
public class CartasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty()) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
            case "filtrar":
                mostrarCatalogo(request, response);
                break;
            case "detalle":
                mostrarDetalle(request, response);
                break;
            default:
                response.sendRedirect("CartasServlet?accion=listar");
                break;
        }
    }

    private void mostrarCatalogo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Leer los parámetros del buscador
        String color = request.getParameter("color");
        String tipo = request.getParameter("tipo");
        String arquetipo = request.getParameter("arquetipo");
        String atributo = request.getParameter("atributo");

        // 2. Matemáticas de Paginación
        int pagina = 1;
        int limite = 18;

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

        try {

            // 3. Hablar con la base de datos
            CartaDAO cartaDAO = new CartaDAO();

            // Obtenemos las cartas de la página actual
            ArrayList<Carta> listaCartas = cartaDAO.filtrarCartas(color, tipo, arquetipo, atributo, limite, offset);

            // --- NUEVA LÓGICA DE CONTEO ---
            // Obtenemos cuántas cartas hay en total con esos filtros
            int totalCartas = cartaDAO.contarTotalCartas(color, tipo, arquetipo, atributo);

            // Calculamos el total de páginas (redondeando hacia arriba)
            int totalPaginas = (int) Math.ceil((double) totalCartas / limite);
            // ------------------------------

            // 4. Empaquetar datos para el JSP
            request.setAttribute("listaCartas", listaCartas);
            request.setAttribute("paginaActual", pagina);
            request.setAttribute("totalPaginas", totalPaginas); // Enviamos el total de páginas
            request.setAttribute("totalRegistros", totalCartas); // Enviamos el total de cartas encontradas

            // Para que los select del HTML recuerden qué buscó el usuario:
            request.setAttribute("colorSeleccionado", color);
            request.setAttribute("tipoSeleccionado", tipo);
            request.setAttribute("arquetipoSeleccionado", arquetipo);
            request.setAttribute("atributoSeleccionado", atributo);

            // 5. Enviar al Frontend
            request.getRequestDispatcher("/Views/cards.jsp").forward(request, response);
        } catch (SQLException e) {
            System.err.println("Error en CartasServlet al cargar catálogo: " + e.getMessage());
            request.setAttribute("mensajeError", "Error técnico al cargar la galería de cartas. Inténtelo más tarde.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }

    }

    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {

                CartaDAO cartaDAO = new CartaDAO();
                Carta cartaActual = cartaDAO.obtenerCartaPorId(idParam);

                if (cartaActual != null) {
                    request.setAttribute("cartaParaLaVista", cartaActual);
                    request.getRequestDispatcher("/Views/card-view.jsp").forward(request, response);
                    return;
                } else {
                    // Lógica: el ID no existe en BD
                    response.sendRedirect("CartasServlet?accion=listar&error=carta_no_encontrada");
                    return;
                }
            } catch (SQLException e) {
                System.err.println("Error en CartasServlet al cargar detalle carta: " + e.getMessage());
                request.setAttribute("mensajeError", "No se pudo cargar la información de la carta solicitada.");
                request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect("CartasServlet?accion=listar&error=carta_no_encontrada");
    }
}
