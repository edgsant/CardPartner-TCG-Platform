package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import datos.MazoDAO;
import java.util.ArrayList;
import modelo.ContieneCarta;
import modelo.Mazo;
import modelo.Usuario;

@WebServlet(name = "GuardarMazoServlet", urlPatterns = {"/GuardarMazoServlet"})
public class GuardarMazoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Usuario creador = (Usuario) session.getAttribute("usuarioActivo");
        
        if (creador == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp");
            return;
        }

        // 1. RECOGER PARÁMETROS DEL FORMULARIO
        String idExistenteStr = request.getParameter("id_mazo_existente"); // 🌟 NUEVO
        String nombreMazo = request.getParameter("nombre_mazo");
        String descripcionMazo = request.getParameter("descripcion_mazo");
        String visibilidadStr = request.getParameter("visibilidad_mazo");
        String idLider = request.getParameter("lider_mazo");
        String stringCartas = request.getParameter("lista_cartas"); 

        boolean esPublico = (visibilidadStr != null && visibilidadStr.equals("Público"));

        // 2. CONFIGURAR EL OBJETO MAZO
        Mazo mazo = new Mazo();
        mazo.setNombreMazo(nombreMazo);
        mazo.setDescripcion(descripcionMazo);
        mazo.setEsPublico(esPublico);
        mazo.setEsOficial(false);
        mazo.setIdUsuarioCreador(creador.getIdUsuario());
        mazo.setIdCartaLider(idLider != null ? idLider : "OP01-001");

        MazoDAO dao = new MazoDAO();
        int idMazoFinal = 0;
        boolean operacionExitosa = false;

        // 3. LÓGICA DE DECISIÓN: ¿CREAR O ACTUALIZAR?
        if (idExistenteStr != null && !idExistenteStr.trim().isEmpty()) {
            // ==========================================
            // CASO A: ACTUALIZAR MAZO EXISTENTE
            // ==========================================
            try {
                idMazoFinal = Integer.parseInt(idExistenteStr);
                mazo.setIdMazo(idMazoFinal);

                // Paso 1: Actualizar cabecera (Nombre, desc, etc.)
                boolean mazoActualizado = dao.actualizarMazo(mazo);
                
                if (mazoActualizado) {
                    // Paso 2: Borrar cartas viejas
                    dao.borrarCartasDeMazo(idMazoFinal);
                    // Marcamos como éxito para proceder a insertar las nuevas abajo
                    operacionExitosa = true;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            // ==========================================
            // CASO B: CREAR MAZO NUEVO
            // ==========================================
            idMazoFinal = dao.crearMazo(mazo);
            if (idMazoFinal > 0) {
                operacionExitosa = true;
            }
        }

        // 4. PROCESAR EL LISTADO DE CARTAS (Común para ambos casos)
        if (operacionExitosa && idMazoFinal > 0) {
            if (stringCartas != null && !stringCartas.isEmpty()) {
                ArrayList<ContieneCarta> listaParaGuardar = new ArrayList<>();
                String[] cartasArray = stringCartas.split(","); 
                
                for (String bloque : cartasArray) {
                    String[] partes = bloque.split(":"); 
                    if(partes.length == 2) {
                        ContieneCarta cc = new ContieneCarta();
                        cc.setIdCarta(partes[0]);
                        cc.setCantidad(Integer.parseInt(partes[1]));
                        listaParaGuardar.add(cc);
                    }
                }
                // Paso 3: Insertar el nuevo listado de cartas
                dao.añadirCartasAMazo(idMazoFinal, listaParaGuardar);
            }
            response.sendRedirect(request.getContextPath() + "/Views/perfil.jsp?exito=true");
        } else {
            response.sendRedirect(request.getContextPath() + "/Views/deck-builder.jsp?error=true");
        }
    }
}