package controladores;

import datos.MazoDAO;
import modelo.Mazo;
import modelo.Usuario;

import java.io.IOException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. EL CONTROL DE SEGURIDAD (La Puerta del Club)
        HttpSession session = request.getSession(false); // false = no crear una nueva si no existe

        if (session == null || session.getAttribute("usuarioActivo") == null) {
            // Si no hay sesión o no hay usuario dentro, a la calle (al login)
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=necesitas_login");
            return;
        }

        // Si pasamos el control, sacamos el carnet del usuario
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioActivo");

        // 2. EL ENRUTADOR (¿Qué quiere hacer en su perfil?)
        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "mi_perfil"; // Acción por defecto
        }

        switch (accion) {
            case "mi_perfil":
                mostrarPerfil(request, response, usuarioLogueado);
                break;
            case "borrar_mazo":
                eliminarMazo(request, response, usuarioLogueado);
                break;
            default:
                response.sendRedirect("UsuarioServlet?accion=mi_perfil");
                break;
        }
    }

    // ==========================================
    // 3. MÉTODOS DE ACCIÓN
    // ==========================================
    private void mostrarPerfil(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        // Vamos a la base de datos a buscar SUS mazos
        MazoDAO mazoDAO = new MazoDAO();
        ArrayList<Mazo> misMazos = mazoDAO.obtenerMazosPorUsuario(usuario.getIdUsuario());

        // Metemos la lista en la "caja de regalo" para el Frontend
        request.setAttribute("listaMisMazos", misMazos);

        // Usamos FORWARD para que la URL siga limpia y los datos viajen
        request.getRequestDispatcher("/Views/perfil.jsp").forward(request, response);
    }

    private void eliminarMazo(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        String idMazoStr = request.getParameter("id");

        if (idMazoStr != null && !idMazoStr.isEmpty()) {
            try {
                int idMazo = Integer.parseInt(idMazoStr);
                MazoDAO mazoDAO = new MazoDAO();

                // ¡Llamamos a tu método seguro que requiere el ID del creador!
                boolean exito = mazoDAO.borrarMazo(idMazo, usuario.getIdUsuario());

                if (exito) {
                    response.sendRedirect("UsuarioServlet?accion=mi_perfil&exito=borrado_ok");
                    return;
                }
            } catch (NumberFormatException e) {
                // Si alguien manipula la URL y pone letras en vez de un ID
                e.printStackTrace();
            }
        }

        // Si algo falla, lo devolvemos al perfil con un mensaje de error
        response.sendRedirect("UsuarioServlet?accion=mi_perfil&error=borrado_fail");
    }
}
