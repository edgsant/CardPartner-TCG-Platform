package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Importamos el DAO y los modelos correctos
import java.sql.SQLException;
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
        // ... (Pasos 1 y 2 siguen igual: Sesión, nombre, descripción) ...
        // 🌟 NUEVO: Recoger líder y lista de cartas que envía el Front
        String idLider = request.getParameter("lider_mazo");
        String stringCartas = request.getParameter("lista_cartas"); // Ej: "OP01-001:4,ST01-002:2"

        // 3. CREAR EL OBJETO MAZO
        Mazo nuevoMazo = new Mazo();
        nuevoMazo.setNombreMazo(nombreMazo);
        nuevoMazo.setDescripcion(descripcionMazo);
        nuevoMazo.setEsPublico(esPublico);
        nuevoMazo.setEsOficial(false);
        nuevoMazo.setIdUsuarioCreador(creador.getIdUsuario());

        // Ahora el líder es dinámico, no fijo
        nuevoMazo.setIdCartaLider(idLider != null ? idLider : "OP01-001");

        // 4. GUARDAR LA "CAJA" DEL MAZO EN LA BD
        try {

            MazoDAO dao = new MazoDAO();
            int idMazoGuardado = dao.crearMazo(nuevoMazo);

            if (idMazoGuardado > 0) {

                // 🌟 NUEVO 5. PROCESAR Y GUARDAR LAS CARTAS (Si el mazo se creó bien)
                if (stringCartas != null && !stringCartas.isEmpty()) {
                    // Necesitas importar java.util.ArrayList y modelo.ContieneCarta arriba
                    ArrayList<ContieneCarta> listaParaGuardar = new ArrayList<>();

                    // Cortamos el texto por las comas -> ["OP01-001:4", "ST01-002:2"]
                    String[] cartasArray = stringCartas.split(",");

                    for (String bloque : cartasArray) {
                        // Cortamos cada bloque por los dos puntos -> ["OP01-001", "4"]
                        String[] partes = bloque.split(":");
                        if (partes.length == 2) {
                            ContieneCarta cc = new ContieneCarta();
                            cc.setIdCarta(partes[0]); // El ID
                            try {
                                cc.setCantidad(Integer.parseInt(partes[1])); // La cantidad
                                listaParaGuardar.add(cc);
                            } catch (NumberFormatException e) {
                                System.err.println("Advertencia: Cantidad inválida ignorada en la carta " + partes[0]);
                            }
                        }
                    }

                    // ¡Llamamos por fin a tu método optimizado del DAO!
                    dao.añadirCartasAMazo(idMazoGuardado, listaParaGuardar);
                }

                response.sendRedirect(request.getContextPath() + "/Views/perfil.jsp?exito=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/Views/deck-builder.jsp?error=true");
            }

        }catch (SQLException e) {
            // 🟢 AÑADIDO: Si la BD falla en cualquier paso (crear mazo o añadir cartas), lo atrapamos
            System.err.println("Error crítico al guardar el mazo del usuario " + creador.getIdUsuario() + ": " + e.getMessage());
            request.setAttribute("mensajeError", "No hemos podido guardar tu mazo por un problema en el servidor. Inténtalo de nuevo más tarde.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }
    }
}
