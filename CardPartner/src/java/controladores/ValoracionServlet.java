package controladores;

import datos.ValoracionDAO;
import modelo.Valoracion;
import modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ValoracionServlet", urlPatterns = {"/ValoracionServlet"})
public class ValoracionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Control de acceso
        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (session != null) ? (Usuario) session.getAttribute("usuarioActivo") : null;
        
        if (usuarioLogueado == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=necesitas_login");
            return;
        }

        String idMazoStr = request.getParameter("idMazo");
        String puntuacionStr = request.getParameter("puntuacion");

        if (idMazoStr == null || puntuacionStr == null) {
            response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar");
            return;
        }

        try {
            int idMazo = Integer.parseInt(idMazoStr);
            float puntuacion = Float.parseFloat(puntuacionStr); // Usamos float respetando tu BD
            
            // Seguridad básica: la puntuación debe estar entre 1 y 5
            if (puntuacion >= 1.0f && puntuacion <= 5.0f) {
                Valoracion v = new Valoracion();
                v.setIdMazo(idMazo);
                v.setIdUsuario(usuarioLogueado.getIdUsuario());
                v.setPuntuacion(puntuacion);

                ValoracionDAO dao = new ValoracionDAO();
                dao.guardarValoracion(v);
            }

            // ÉXITO: Recargamos la página del mazo
            response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=detalle&id=" + idMazo);

        } catch (SQLException e) {
            System.err.println("Error en ValoracionServlet: " + e.getMessage());
            request.setAttribute("mensajeError", "No hemos podido guardar tu valoración.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar");
        }
    }
}