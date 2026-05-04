package controladores;

import datos.UsuarioDAO;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AvatarServlet", urlPatterns = {"/AvatarServlet"})
public class AvatarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int idUsuario = Integer.parseInt(idStr);
                UsuarioDAO dao = new UsuarioDAO();
                
                // 1. Obtenemos los bytes de la imagen desde el DAO
                byte[] imagenBytes = dao.obtenerAvatar(idUsuario);
                
                // 2. Si el usuario tiene foto, se la mandamos al navegador
                if (imagenBytes != null) {
                    response.setContentType("image/jpeg"); 
                    response.setContentLength(imagenBytes.length);
                    response.getOutputStream().write(imagenBytes);
                    return; // Terminamos con éxito
                }
            } catch (NumberFormatException | SQLException e) {
                // Silenciado: si falla la BD o el ID es falso, simplemente pasará al Plan B.
            }
        }
        
        // 3. PLAN B: Si no hay ID, hay error, o el usuario no subió foto,
        // le enviamos una imagen de perfil por defecto.
        // ¡OJO! Asegúrate de tener una imagen llamada 'default-avatar.png' en tu carpeta 'img'
        response.sendRedirect(request.getContextPath() + "/img/default-avatar.png");
    }
}