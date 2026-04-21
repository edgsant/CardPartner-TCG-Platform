package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtenemos la sesión actual (la "pulsera" de la RAM)
        HttpSession session = request.getSession(false);
        
        // 2. Si existe, la destruimos
        if (session != null) {
            session.invalidate();
        }
        
        // 3. 🌟 NUEVO: Destruimos la Cookie del disco duro
        // Creamos una cookie con el MISMO nombre que usamos en el LoginServlet
        Cookie cookie = new Cookie("correoRecordado", ""); 
        
        // Le ponemos 0 segundos de vida (esto ordena al navegador que la borre YA)
        cookie.setMaxAge(0); 
        
        // Tiene que tener la misma ruta que le pusimos al crearla
        cookie.setPath("/"); 
        
        // Se la disparamos al navegador
        response.addCookie(cookie);
        
        // 4. Redirigimos al usuario a la página principal limpia
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}