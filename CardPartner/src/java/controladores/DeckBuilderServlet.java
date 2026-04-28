package controladores;

import datos.CartaDAO;
import datos.MazoDAO;
import modelo.Carta;
import modelo.Mazo;
import modelo.ContieneCarta;

import java.sql.SQLException;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;

@WebServlet(name = "DeckBuilderServlet", urlPatterns = {"/DeckBuilderServlet"})
public class DeckBuilderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioActivo") == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=necesitas_login");
            return;
        }

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioActivo");

        try {
            CartaDAO cartaDAO = new CartaDAO();
            List<Carta> listaLideres = cartaDAO.filtrarCartas(null, "LIDER", null, null, 100, 0);
            request.setAttribute("listaLideres", listaLideres);

            // ==========================================
            // 🌟 NUEVO: LÓGICA DE EDICIÓN
            // ==========================================
            String accion = request.getParameter("accion");
            String idParam = request.getParameter("id");

            if ("editar".equals(accion) && idParam != null) {
                try {
                    int idMazo = Integer.parseInt(idParam);
                    MazoDAO mazoDAO = new MazoDAO();
                    Mazo mazoAEditar = mazoDAO.obtenerMazoPorId(idMazo);

                    // Comprobación de seguridad: El mazo debe existir y ser del usuario logueado
                    if (mazoAEditar != null && mazoAEditar.getIdUsuarioCreador() == usuarioLogueado.getIdUsuario()) {

                        // Cargamos el líder para tener su foto
                        Carta lider = cartaDAO.obtenerCartaPorId(mazoAEditar.getIdCartaLider());
                        mazoAEditar.setCartaLider(lider);

                        // Cargamos las 50 cartas
                        List<ContieneCarta> cartasDelMazo = mazoDAO.obtenerCartasDeUnMazo(idMazo);

                        // Enviamos todo a la vista
                        request.setAttribute("mazoAEditar", mazoAEditar);
                        request.setAttribute("cartasAEditar", cartasDelMazo);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            request.getRequestDispatcher("/Views/deck-builder.jsp").forward(request, response);
        } catch (SQLException e) {
            System.err.println("Error de BD en DeckBuilderServlet: " + e.getMessage());
            request.setAttribute("mensajeError", "Hubo un problema al cargar el constructor de mazos.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
