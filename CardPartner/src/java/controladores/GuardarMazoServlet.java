package controladores;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import datos.MazoDAO;
import modelo.ContieneCarta;
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
        
        if (creador == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp");
            return;
        }

        // 2. RECOGER DATOS DEL FORMULARIO
        String nombreMazo = request.getParameter("nombre_mazo");
        String descripcionMazo = request.getParameter("descripcion_mazo");
        String visibilidadStr = request.getParameter("visibilidad_mazo");
        String idLider = request.getParameter("lider_mazo");
        String stringCartas = request.getParameter("lista_cartas"); 
        
        // El ID oculto que nos dirá si estamos creando (0) o editando (>0)
        String idMazoStr = request.getParameter("id_mazo");
        int idMazoFront = (idMazoStr != null && !idMazoStr.isEmpty()) ? Integer.parseInt(idMazoStr) : 0;

        boolean esPublico = (visibilidadStr != null && visibilidadStr.equals("Público"));

        // 3. PREPARAR LA LISTA DE CARTAS (Tu bucle original, no se ha perdido)
        ArrayList<ContieneCarta> listaParaGuardar = new ArrayList<>();
        if (stringCartas != null && !stringCartas.isEmpty()) {
            String[] cartasArray = stringCartas.split(","); 
            for (String bloque : cartasArray) {
                String[] partes = bloque.split(":"); 
                if(partes.length == 2) {
                    ContieneCarta cc = new ContieneCarta();
                    cc.setIdCarta(partes[0]); 
                    try {
                        cc.setCantidad(Integer.parseInt(partes[1])); 
                        listaParaGuardar.add(cc);
                    } catch (NumberFormatException e) {
                        System.err.println("Advertencia: Cantidad inválida ignorada en la carta " + partes[0]);
                    }
                }
            }
        }

        // 4. CREAR EL OBJETO MAZO
        Mazo nuevoMazo = new Mazo();
        nuevoMazo.setNombreMazo(nombreMazo);
        nuevoMazo.setDescripcion(descripcionMazo);
        nuevoMazo.setEsPublico(esPublico);
        nuevoMazo.setEsOficial(false);
        nuevoMazo.setIdUsuarioCreador(creador.getIdUsuario()); 
        nuevoMazo.setIdCartaLider(idLider != null ? idLider : "OP01-001"); 

        // 5. BASE DE DATOS (EL CRUCE DE CAMINOS)
        try {
            MazoDAO dao = new MazoDAO();

            // CASO A: CREAR MAZO NUEVO O COPIA
            if (idMazoFront == 0) {
                int idMazoGuardado = dao.crearMazo(nuevoMazo);
                
                if (idMazoGuardado > 0) {
                    if (!listaParaGuardar.isEmpty()) {
                        dao.añadirCartasAMazo(idMazoGuardado, listaParaGuardar);
                    }
                    response.sendRedirect(request.getContextPath() + "/Views/perfil.jsp?exito=creado");
                } else {
                    response.sendRedirect(request.getContextPath() + "/Views/deck-builder.jsp?error=true");
                }
            } 
            // CASO B: ACTUALIZAR UN MAZO EXISTENTE
            else {
                Mazo mazoBD = dao.obtenerMazoPorId(idMazoFront);
                
                // Comprobación de seguridad: ¿El usuario actual es el dueño real del mazo?
                if (mazoBD != null && mazoBD.getIdUsuarioCreador() == creador.getIdUsuario()) {
                    
                    nuevoMazo.setIdMazo(idMazoFront); 
                    
                    if (dao.actualizarMazo(nuevoMazo)) {
                        // Vaciamos el mazo viejo y metemos la lista nueva
                        dao.borrarCartasDeMazo(idMazoFront);
                        if (!listaParaGuardar.isEmpty()) {
                            dao.añadirCartasAMazo(idMazoFront, listaParaGuardar);
                        }
                        response.sendRedirect(request.getContextPath() + "/Views/perfil.jsp?exito=actualizado");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/Views/deck-builder.jsp?error=true");
                    }
                } else {
                    // Intento de manipulación de ID en el HTML
                    response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=hack_detectado");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error crítico al guardar mazo: " + e.getMessage());
            request.setAttribute("mensajeError", "No hemos podido guardar tu mazo por un problema en el servidor. Inténtalo de nuevo más tarde.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }
    }
}