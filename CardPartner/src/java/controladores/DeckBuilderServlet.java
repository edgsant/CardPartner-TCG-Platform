package controladores;

import datos.CartaDAO;
import modelo.Carta;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DeckBuilderServlet", urlPatterns = {"/DeckBuilderServlet"})
public class DeckBuilderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ==========================================
        // 1. BARRERA DE SEGURIDAD (Solo usuarios logueados)
        // ==========================================
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioActivo") == null) {
            // Si no hay sesión o no hay usuario activo, lo echamos al Login
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=necesitas_login");
            return; // ¡Muy importante el return para cortar la ejecución!
        }

        // ==========================================
        // 2. CARGA DE DATOS PARA EL CONSTRUCTOR
        // ==========================================
        CartaDAO cartaDAO = new CartaDAO();

        // Traemos todos los líderes para llenar el modal visual
        List<Carta> listaLideres = cartaDAO.filtrarCartas(null, "LIDER", null, null, 100, 0);

        request.setAttribute("listaLideres", listaLideres);

        // Enviamos a la vista
        request.getRequestDispatcher("/Views/deck-builder.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}