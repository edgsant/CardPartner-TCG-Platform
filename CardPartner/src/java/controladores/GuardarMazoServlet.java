package controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        String visibilidadMazo = request.getParameter("visibilidad_mazo");

        // 3. CREAR EL OBJETO MAZO
        Mazo nuevoMazo = new Mazo();
        nuevoMazo.setId((int)(Math.random() * 1000)); // ID aleatorio temporal
        nuevoMazo.setNombre(nombreMazo);
        nuevoMazo.setDescripcion(descripcionMazo);
        nuevoMazo.setVisibilidad(visibilidadMazo);
        nuevoMazo.setCreador(creador); // Asignamos el dueño del mazo

        // 4. GUARDARLO EN UNA LISTA EN LA SESIÓN (Simulando la Base de Datos)
        // Buscamos si el usuario ya tiene una lista de mazos creada en esta sesión
        List<Mazo> misMazos = (List<Mazo>) session.getAttribute("listaMisMazos");
        if (misMazos == null) {
            misMazos = new ArrayList<>(); // Si no la tiene, la creamos vacía
        }
        
        misMazos.add(nuevoMazo); // Añadimos el mazo recién creado
        session.setAttribute("listaMisMazos", misMazos); // Guardamos la lista actualizada

        // 5. REDIRIGIR AL PERFIL
        response.sendRedirect(request.getContextPath() + "/Views/perfil.jsp");
    }
}