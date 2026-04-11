package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtenemos la sesión actual (sin crear una nueva)
        HttpSession session = request.getSession(false);
        
        // 2. Si existe, la destruimos (borra al usuarioActivo y todo lo demás)
        if (session != null) {
            session.invalidate();
        }
        
        // 3. Redirigimos al usuario a la página principal
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}