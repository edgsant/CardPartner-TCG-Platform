package controladores;

import datos.ComentarioDAO;
import modelo.Comentario;
import modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ComentarioServlet", urlPatterns = {"/ComentarioServlet"})
public class ComentarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Validar que el usuario esté logueado
        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (session != null) ? (Usuario) session.getAttribute("usuarioActivo") : null;
        
        if (usuarioLogueado == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=necesitas_login");
            return;
        }

        // 2. Recoger parámetros básicos
        String accion = request.getParameter("accion");
        String idMazoStr = request.getParameter("idMazo");
        
        if (idMazoStr == null || idMazoStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar");
            return;
        }
        
        int idMazo = Integer.parseInt(idMazoStr);
        ComentarioDAO dao = new ComentarioDAO();

        try {
            // 3. Ejecutar la acción solicitada
            if ("crear".equals(accion)) {
                String contenido = request.getParameter("contenido");
                
                if (contenido != null && !contenido.trim().isEmpty()) {
                    Comentario nuevoComentario = new Comentario();
                    nuevoComentario.setContenido(contenido.trim());
                    nuevoComentario.setIdMazoDestino(idMazo);
                    nuevoComentario.setUsuarioAutor(usuarioLogueado); // Usamos composición
                    
                    dao.crearComentario(nuevoComentario);
                }
            } 
            else if ("borrar".equals(accion)) {
                int idComentario = Integer.parseInt(request.getParameter("idComentario"));
                dao.borrarComentario(idComentario, usuarioLogueado.getIdUsuario());
            }

            // 4. ÉXITO: Redirigimos de vuelta a la vista del mazo (Patrón PRG)
            response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=detalle&id=" + idMazo);

        } catch (SQLException e) {
            // ERROR: Base de datos caída
            System.err.println("Error en ComentarioServlet: " + e.getMessage());
            request.setAttribute("mensajeError", "Hubo un problema al procesar tu comentario.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            // ERROR: IDs manipulados en el HTML
            response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=detalle&id=" + idMazo);
        }
    }
}