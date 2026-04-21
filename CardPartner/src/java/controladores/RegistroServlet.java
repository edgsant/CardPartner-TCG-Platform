package controladores;

import datos.UsuarioDAO;
import modelo.Usuario;

import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5) // Límite de 5MB por foto
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String apodo = request.getParameter("apodo");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (apodo == null || email == null || password == null || apodo.isEmpty() || email.isEmpty() || password.isEmpty()) {
            response.sendRedirect("Views/registro.jsp?error=campos_vacios");
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setApodoUsuario(apodo);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(password);

        // --- TRATAMIENTO DEL ARCHIVO ---
        Part filePart = request.getPart("avatar"); // Lee el input type="file"
        
        if (filePart != null && filePart.getSize() > 0) {
            // Extraemos el flujo de bytes de la imagen y se lo damos al usuario
            InputStream fileContent = filePart.getInputStream();
            nuevoUsuario.setAvatar(fileContent);
        } else {
            // Si el usuario no ha subido nada, el avatar será null
            nuevoUsuario.setAvatar(null);
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        int exito = usuarioDAO.registrarUsuario(nuevoUsuario);

        if (exito!=0) {
            response.sendRedirect("Views/login.jsp?exito=registro_ok");
        } else {
            response.sendRedirect("Views/registro.jsp?error=email_duplicado");
        }
    }
}