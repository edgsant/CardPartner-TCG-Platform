package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Importamos el DAO y los modelos correctos
import datos.MazoDAO;
import modelo.Mazo;
import modelo.Usuario;

@WebServlet(name = "GuardarMazoServlet", urlPatterns = {"/GuardarMazoServlet"})
public class GuardarMazoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. OBTENER LA SESIÓN Y EL USUARIO
        HttpSession session = request.getSession();
        Usuario creador = (Usuario) session.getAttribute("usuarioActivo");
        
        // Si alguien intenta guardar un mazo sin iniciar sesión, lo echamos al Login
        if (creador == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp");
            return;
        }

        // 2. RECOGER DATOS DEL FORMULARIO
        String nombreMazo = request.getParameter("nombre_mazo");
        String descripcionMazo = request.getParameter("descripcion_mazo");
        String visibilidadStr = request.getParameter("visibilidad_mazo");
        
        // Convertimos el texto del HTML a boolean (1 = público, 0 = privado)
        boolean esPublico = (visibilidadStr != null && visibilidadStr.equals("Público"));

        // 3. CREAR EL OBJETO MAZO CON LOS MÉTODOS DEL NUEVO MODELO
        Mazo nuevoMazo = new Mazo();
        nuevoMazo.setNombreMazo(nombreMazo);
        nuevoMazo.setDescripcion(descripcionMazo);
        nuevoMazo.setEsPublico(esPublico);
        nuevoMazo.setEsOficial(false); // Los creados por usuarios no son oficiales
        nuevoMazo.setIdUsuarioCreador(creador.getIdUsuario()); // FK del creador
        
        // TEMPORAL: Asignamos un líder por defecto para que la BD no dé error.
        // En el futuro, el HTML del Deck Builder deberá enviar el ID de la carta líder elegida.
        nuevoMazo.setIdCartaLider("OP01-001"); 

        // 4. GUARDARLO EN LA BASE DE DATOS REAL
        MazoDAO dao = new MazoDAO();
        int idMazoGuardado = dao.crearMazo(nuevoMazo);

        // 5. COMPROBAR Y REDIRIGIR
        if (idMazoGuardado > 0) {
            // Se guardó correctamente en MariaDB
            response.sendRedirect(request.getContextPath() + "/Views/perfil.jsp?exito=true");
        } else {
            // Hubo un error en la base de datos
            response.sendRedirect(request.getContextPath() + "/Views/deck-builder.jsp?error=true");
        }
    }
}