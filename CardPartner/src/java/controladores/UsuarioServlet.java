package controladores;

import datos.MazoDAO;
import datos.UsuarioDAO;
import modelo.Mazo;
import modelo.Usuario;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

// 1. AÑADIMOS MULTIPARTCONFIG PARA PODER SUBIR AVATARES
@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5) // Máximo 5MB por imagen
public class UsuarioServlet extends HttpServlet {

    // 2. SOPORTAMOS TANTO GET COMO POST
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarPeticion(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarPeticion(request, response);
    }

    // 3. EL CEREBRO CENTRAL
    private void procesarPeticion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioActivo") == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=necesitas_login");
            return;
        }

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioActivo");
        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty()) {
            accion = "mi_perfil";
        }

        switch (accion) {
            case "mi_perfil":
                mostrarPerfil(request, response, usuarioLogueado);
                break;
            case "borrar_mazo":
                eliminarMazo(request, response, usuarioLogueado);
                break;
            case "quitar_favorito":
                quitarFavorito(request, response, usuarioLogueado);
                break;
            case "cambiar_password":
                cambiarPassword(request, response, usuarioLogueado);
                break;
            case "editar_datos":
                editarDatos(request, response, usuarioLogueado);
                break;
            case "guardar_favorito":
                guardarFavorito(request, response, usuarioLogueado);
                break;
            default:
                response.sendRedirect("UsuarioServlet?accion=mi_perfil");
                break;
        }
    }

    // ==========================================
    // MÉTODOS DE ACCIÓN
    // ==========================================
    private void mostrarPerfil(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        MazoDAO mazoDAO = new MazoDAO();

        // Carga 1: Los mazos que YO he creado
        ArrayList<Mazo> misMazos = mazoDAO.obtenerMazosPorUsuario(usuario.getIdUsuario());

        // Carga 2: Los mazos de otros que yo he GUARDADO (Favoritos)
        ArrayList<Mazo> misFavoritos = mazoDAO.obtenerMazosGuardados(usuario.getIdUsuario());

        request.setAttribute("listaMisMazos", misMazos);
        request.setAttribute("listaMisFavoritos", misFavoritos);

        request.getRequestDispatcher("/Views/perfil.jsp").forward(request, response);
    }

    private void eliminarMazo(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        String idMazoStr = request.getParameter("id");
        if (idMazoStr != null && !idMazoStr.isEmpty()) {
            try {
                int idMazo = Integer.parseInt(idMazoStr);
                MazoDAO mazoDAO = new MazoDAO();
                if (mazoDAO.borrarMazo(idMazo, usuario.getIdUsuario())) {
                    response.sendRedirect("UsuarioServlet?accion=mi_perfil&exito=mazo_borrado");
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("UsuarioServlet?accion=mi_perfil&error=borrado_fail");
    }

    private void quitarFavorito(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        String idMazoStr = request.getParameter("id");
        if (idMazoStr != null && !idMazoStr.isEmpty()) {
            try {
                int idMazo = Integer.parseInt(idMazoStr);
                MazoDAO mazoDAO = new MazoDAO();
                // Necesitas implementar este método en MazoDAO (DELETE FROM GUARDA_MAZO...)
                if (mazoDAO.quitarMazoFavorito(usuario.getIdUsuario(), idMazo)) {
                    response.sendRedirect("UsuarioServlet?accion=mi_perfil&exito=favorito_quitado");
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("UsuarioServlet?accion=mi_perfil&error=favorito_fail");
    }
    
    private void guardarFavorito(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        String idMazoStr = request.getParameter("id");
        if (idMazoStr != null && !idMazoStr.isEmpty()) {
            try {
                int idMazo = Integer.parseInt(idMazoStr);
                MazoDAO mazoDAO = new MazoDAO();
                
                if (mazoDAO.añadirMazoFavorito(usuario.getIdUsuario(), idMazo)) {
                    // Si lo guarda, lo devolvemos a la vista del mazo con mensaje de éxito
                    response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=detalle&id=" + idMazo + "&exito=guardado");
                    return;
                }
            } catch (NumberFormatException e) { e.printStackTrace(); }
        }
        // Si falla, lo mandamos a su perfil
        response.sendRedirect("UsuarioServlet?accion=mi_perfil&error=favorito_fail");
    }

    private void cambiarPassword(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        String passActual = request.getParameter("passActual");
        String passNueva = request.getParameter("passNueva");

        if (passActual != null && passActual.equals(usuario.getPassword())) {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            // Necesitas implementar actualizarPassword en UsuarioDAO
            if (usuarioDAO.actualizarPassword(usuario.getIdUsuario(), passNueva)) {
                // Actualizamos la sesión para que no se le cierre la cuenta
                usuario.setPassword(passNueva);
                response.sendRedirect("UsuarioServlet?accion=mi_perfil&exito=pass_ok");
                return;
            }
        }
        response.sendRedirect("UsuarioServlet?accion=mi_perfil&error=pass_incorrecta");
    }

    private void editarDatos(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        String nuevoApodo = request.getParameter("apodo");
        String nuevoEmail = request.getParameter("email");
        Part filePart = request.getPart("avatar"); // Recogemos la imagen subida

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        InputStream inputStream = null;

        if (filePart != null && filePart.getSize() > 0) {
            inputStream = filePart.getInputStream();
        }

        // Llamamos al método que actualizará todo. (Debes crearlo en UsuarioDAO)
        // Ejemplo: usuarioDAO.actualizarPerfilCompleto(id, apodo, email, inputStream)
        boolean actualizado = usuarioDAO.actualizarPerfilCompleto(usuario.getIdUsuario(), nuevoApodo, nuevoEmail, inputStream);

        if (actualizado) {
            // Actualizamos los datos en vivo en la sesión
            usuario.setApodoUsuario(nuevoApodo);
            usuario.setEmail(nuevoEmail);
            response.sendRedirect("UsuarioServlet?accion=mi_perfil&exito=perfil_actualizado");
        } else {
            response.sendRedirect("UsuarioServlet?accion=mi_perfil&error=perfil_fail");
        }
    }
}
