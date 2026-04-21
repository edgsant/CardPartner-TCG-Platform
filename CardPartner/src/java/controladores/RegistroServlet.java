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

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos los datos del formulario (asegúrate de que los 'name' del HTML coinciden)
        String apodo = request.getParameter("nuevo_usuario");
        String email = request.getParameter("email");
        String pass1 = request.getParameter("nueva_password");
        String pass2 = request.getParameter("repetir_password");

        // 2. Validamos que las contraseñas coincidan
        if (pass1 == null || !pass1.equals(pass2)) {
            response.sendRedirect(request.getContextPath() + "/Views/register.jsp?error=passwords");
            return; // Cortamos la ejecución aquí
        }

        UsuarioDAO dao = new UsuarioDAO();

        // 3. Validamos que el email no esté ya registrado en MariaDB
        if (dao.existeEmail(email)) {
            response.sendRedirect(request.getContextPath() + "/Views/register.jsp?error=emailexiste");
            return;
        }

        // 4. Todo correcto: Creamos la "caja" (DTO) para enviarla al DAO
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setApodoUsuario(apodo);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(pass1);
        nuevoUsuario.setRol("JUGADOR"); // Por defecto, todos los registrados son jugadores
        // No enviamos ID ni Avatar ni Fecha, de eso se encarga MariaDB

        // 5. El DAO hace el INSERT y nos devuelve el ID que MariaDB le ha asignado
        int idGenerado = dao.registrarUsuario(nuevoUsuario);

        // 6. Comprobamos si el INSERT fue un éxito
        if (idGenerado > 0) {
            // Le ponemos el ID real a nuestro objeto
            nuevoUsuario.setIdUsuario(idGenerado);
            
            // Lo logueamos automáticamente guardándolo en sesión
            HttpSession session = request.getSession();
            session.setAttribute("usuarioActivo", nuevoUsuario);
            
            // Lo mandamos a la página principal
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            
        } else {
            // Hubo un fallo raro en la base de datos
            response.sendRedirect(request.getContextPath() + "/Views/register.jsp?error=db");
        }
    }
}