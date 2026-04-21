package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import datos.UsuarioDAO;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos los datos del formulario JSP (identificador puede ser email o username)
        String identificador = request.getParameter("email"); 
        String passIngresada = request.getParameter("password");

        // 2. Llamamos al DAO para que busque en MariaDB
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogueado = dao.validarLogin(identificador, passIngresada);

        // 3. Comprobamos la respuesta
        if (usuarioLogueado != null) {
            
            HttpSession session = request.getSession();
            session.setAttribute("usuarioActivo", usuarioLogueado);
            
            // Redirigimos a la página principal (index.jsp)
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            
        } else {
            
          
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=true");
        }
    }
}