package controladores;

import java.io.IOException;
import java.util.Date; // Necesario para la fecha de registro
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos los datos del formulario de registro
        String nombreUsuario = request.getParameter("nuevo_usuario");
        String email = request.getParameter("email");
        String pass1 = request.getParameter("nueva_password");
        String pass2 = request.getParameter("repetir_password");

        // 2. Validamos que las contraseñas coincidan
        if (pass1 != null && pass1.equals(pass2)) {
            
            // 3. Todo correcto: Creamos el nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setId(2); // ID inventado de momento
            nuevoUsuario.setNombreUsuario(nombreUsuario);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(pass1);
            nuevoUsuario.setFechaRegistro(new Date()); // Le asigna la fecha y hora exactas de hoy
            
            // 4. Lo guardamos en la sesión para "loguearlo" automáticamente
            HttpSession session = request.getSession();
            session.setAttribute("usuarioActivo", nuevoUsuario);
            
            // 5. Lo mandamos a su nuevo perfil
            response.sendRedirect(request.getContextPath() + "/Views/perfil.jsp");
            
        } else {
            // Si las contraseñas no coinciden, lo devolvemos al formulario con un error
            response.sendRedirect(request.getContextPath() + "/Views/register.jsp?error=passwords");
        }
    }
}