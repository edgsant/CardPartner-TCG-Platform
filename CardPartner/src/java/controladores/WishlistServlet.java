package controladores;

import datos.UsuarioDAO;
import modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "WishlistServlet", urlPatterns = {"/WishlistServlet"})
public class WishlistServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (session != null) ? (Usuario) session.getAttribute("usuarioActivo") : null;
        
        if (usuarioLogueado == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=necesitas_login");
            return;
        }

        String idCarta = request.getParameter("idCarta");
        String accion = request.getParameter("accion"); // Recogemos si quiere añadir o quitar
        
        if (idCarta == null || idCarta.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/CartasServlet?accion=listar");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAO();
            
            if ("quitar".equals(accion)) {
                dao.quitarDeWishlist(usuarioLogueado.getIdUsuario(), idCarta);
                response.sendRedirect(request.getContextPath() + "/CartasServlet?accion=detalle&id=" + idCarta + "&exito=wishlist_quitada");
            } else {
                dao.añadirAWishlist(usuarioLogueado.getIdUsuario(), idCarta);
                response.sendRedirect(request.getContextPath() + "/CartasServlet?accion=detalle&id=" + idCarta + "&exito=wishlist_ok");
            }
        } catch (SQLException e) {
            System.err.println("Error en WishlistServlet: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/CartasServlet?accion=detalle&id=" + idCarta + "&error=ya_existe");
        }
    }

    // ==========================================
    // BLOQUEO DE ACCESO DIRECTO (GET)
    // ==========================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si alguien escribe "/WishlistServlet" directamente en la URL, lo echamos al catálogo
        response.sendRedirect(request.getContextPath() + "/CartasServlet?accion=listar");
    }
}