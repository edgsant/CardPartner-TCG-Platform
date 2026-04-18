package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException; // NOTA: Si te da error, cambia 'jakarta' por 'javax'
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import datos.GestionDB;
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos los datos que el usuario ha escrito en el formulario
        String user = request.getParameter("usuario_input");
        String pass = request.getParameter("password_input");

        // 2. Simulamos la Base de Datos (Hardcodeamos un usuario de prueba)
        // En el futuro, aquí tu compañero hará un "SELECT * FROM Usuarios WHERE..."
        if ("admin".equals(user) && "1234".equals(pass)) {
            
            // 3. ¡El login es correcto! Creamos el objeto Usuario con los datos falsos acordados
            Usuario usuarioLogueado = new Usuario();
            usuarioLogueado.setId(1);
            usuarioLogueado.setNombreUsuario("Usuario de Prueba");
            usuarioLogueado.setEmail("usuario.prueba@estudiantes.uva.es");
            
            // 4. Guardamos al usuario en la "Sesión" (para que la web lo recuerde mientras navega)
            HttpSession session = request.getSession();
            session.setAttribute("usuarioActivo", usuarioLogueado);
            
            // 5. Lo redirigimos a la pantalla de Perfil
            response.sendRedirect(request.getContextPath() + "/Views/perfil.jsp");
            
        } else {
            // Si falla la contraseña, lo devolvemos al login mandando un aviso de error en la URL
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=true");
        }
        // Llamar al backend (Base de Datos)
        GestionDB db = new GestionDB();
        Usuario userDB = db.validarUsuario(user, pass);

        if (userDB != null) {
            // Guardar en sesión para que cabecera.jsp lo reconozca
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuarioActivo", user);
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("Views/login.jsp?error=1");
        }
         
    }
}