package controladores;

import datos.CartaDAO;
import modelo.Carta;

import jakarta.servlet.http.HttpSession;
import datos.UsuarioDAO;

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

        // 1. Recoger parámetros múltiples (Arrays)
        String nombre = request.getParameter("nombre");
        String[] colores = request.getParameterValues("color");
        String[] tipos = request.getParameterValues("tipo");
        String[] atributos = request.getParameterValues("atributo");

        // NUEVO: Recoger la cantidad de cartas por página (por defecto 25)
        int limite = 25;
        String sizeParam = request.getParameter("size");
        if (sizeParam != null && !sizeParam.isEmpty()) {
            try { limite = Integer.parseInt(sizeParam); } 
            catch (NumberFormatException e) { limite = 25; }
        }

        // 2. Matemáticas de Paginación
        int pagina = 1;
        String paginaParam = request.getParameter("pagina");
        if (paginaParam != null && !paginaParam.isEmpty()) {
            try { pagina = Integer.parseInt(paginaParam); if (pagina < 1) pagina = 1; } 
            catch (NumberFormatException e) { pagina = 1; }
        }
        int offset = (pagina - 1) * limite;

        try {
            CartaDAO cartaDAO = new CartaDAO();

            // 3. Ejecutar consultas inteligentes con el límite dinámico
            ArrayList<Carta> listaCartas = cartaDAO.filtrarCartas(nombre, colores, tipos, atributos, limite, offset);
            int totalCartas = cartaDAO.contarTotalCartas(nombre, colores, tipos, atributos);
            int totalPaginas = (int) Math.ceil((double) totalCartas / limite);

            // 4. Empaquetar para la vista
            request.setAttribute("listaCartas", listaCartas);
            request.setAttribute("paginaActual", pagina);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("totalRegistros", totalCartas);
            request.setAttribute("registrosPorPagina", limite); // Enviamos el límite seleccionado

            // 5. Guardar selecciones para el Frontend (para que las casillas se queden marcadas)
            request.setAttribute("nombreBuscado", nombre);
            request.setAttribute("coloresSeleccionados", colores);
            request.setAttribute("tiposSeleccionados", tipos);
            request.setAttribute("atributosSeleccionados", atributos);

            // 6. Construir la URL de paginación para mantener los filtros al cambiar de página
            StringBuilder queryFiltros = new StringBuilder();
            if (nombre != null && !nombre.isEmpty()) queryFiltros.append("&nombre=").append(nombre);
            if (colores != null) for (String c : colores) queryFiltros.append("&color=").append(c);
            if (tipos != null) for (String t : tipos) queryFiltros.append("&tipo=").append(t);
            if (atributos != null) for (String a : atributos) queryFiltros.append("&atributo=").append(a);
            // Añadimos el size a la query de paginación
            queryFiltros.append("&size=").append(limite);
            
            request.setAttribute("queryFiltros", queryFiltros.toString());

            request.getRequestDispatcher("/Views/cards.jsp").forward(request, response);
        } catch (SQLException e) {
            System.err.println("Error en CartasServlet: " + e.getMessage());
            request.setAttribute("mensajeError", "Error técnico al cargar la galería de cartas.");
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
                    
                    // --- NUEVO: COMPROBAR WISHLIST ---
                    boolean enWishlist = false;
                    HttpSession session = request.getSession(false);
                    if (session != null && session.getAttribute("usuarioActivo") != null) {
                        modelo.Usuario usuarioLogueado = (modelo.Usuario) session.getAttribute("usuarioActivo");
                        UsuarioDAO uDao = new UsuarioDAO();
                        enWishlist = uDao.estaEnWishlist(usuarioLogueado.getIdUsuario(), cartaActual.getIdCarta());
                    }
                    
                    request.setAttribute("cartaParaLaVista", cartaActual);
                    request.setAttribute("enWishlist", enWishlist); // Pasamos el booleano a la vista
                    
                    request.getRequestDispatcher("/Views/card-view.jsp").forward(request, response);
                    return;
                } else {
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
