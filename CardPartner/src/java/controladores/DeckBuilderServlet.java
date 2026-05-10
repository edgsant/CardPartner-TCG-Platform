package controladores;

import datos.CartaDAO;
import modelo.Carta;
import modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "DeckBuilderServlet", urlPatterns = {"/DeckBuilderServlet"})
public class DeckBuilderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioActivo") == null) {
            response.sendRedirect("Views/login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        try {
            switch (accion) {
                case "agregar":
                    agregarCartaAlMazo(request, response, sesion);
                    break;
                case "quitar":
                    quitarCartaDelMazo(request, response, sesion);
                    break;
                case "limpiar":
                    sesion.removeAttribute("mazoTemporal");
                    sesion.removeAttribute("liderSeleccionado");
                    sesion.removeAttribute("mazoEditandoId"); 
                    response.sendRedirect("DeckBuilderServlet");
                    break;
                case "editar":
                case "copiar":
                    // NUEVO: Atrapamos la orden de editar un mazo existente
                    cargarMazoEnSesion(request, response, sesion, accion);
                    break;
                case "listar":
                default:
                    mostrarBuscador(request, response, sesion);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("Views/error.jsp");
        }
    }

    private void mostrarBuscador(HttpServletRequest request, HttpServletResponse response, HttpSession sesion) throws SQLException, ServletException, IOException {
        CartaDAO dao = new CartaDAO();
        
        String colorFiltro = request.getParameter("color");
        String tipoFiltro = request.getParameter("tipo");
        
        int limite = 50;
        int pagina = 1;
        if (request.getParameter("page") != null) {
            pagina = Integer.parseInt(request.getParameter("page"));
        }
        int offset = (pagina - 1) * limite;

        // Recuperamos el líder de la sesión para pasárselo a la Base de Datos
        Carta lider = (Carta) sesion.getAttribute("liderSeleccionado");

        // Obtenemos el catálogo (Ahora el DAO aplica las reglas oficiales)
        ArrayList<Carta> catalogo = dao.obtenerCatalogoFiltrado(colorFiltro, tipoFiltro, limite, offset, lider);
        int totalCartas = dao.contarCartasFiltradas(colorFiltro, tipoFiltro, lider);
        int totalPaginas = (int) Math.ceil((double) totalCartas / limite);

        if (sesion.getAttribute("mazoTemporal") == null) {
            sesion.setAttribute("mazoTemporal", new ArrayList<Carta>());
        }

        request.setAttribute("catalogo", catalogo);
        request.setAttribute("paginaActual", pagina);
        request.setAttribute("totalPaginas", totalPaginas);
        request.setAttribute("colorFiltro", colorFiltro);
        request.setAttribute("tipoFiltro", tipoFiltro);

        request.getRequestDispatcher("/Views/deck-builder.jsp").forward(request, response);
    }

    private void agregarCartaAlMazo(HttpServletRequest request, HttpServletResponse response, HttpSession sesion) throws SQLException, IOException {
        String idCarta = request.getParameter("idCarta");
        CartaDAO dao = new CartaDAO();
        Carta nuevaCarta = dao.obtenerCartaPorId(idCarta);

        if (nuevaCarta == null) {
            response.sendRedirect("DeckBuilderServlet?error=carta_no_existe");
            return;
        }

        Carta lider = (Carta) sesion.getAttribute("liderSeleccionado");
        ArrayList<Carta> mazo = (ArrayList<Carta>) sesion.getAttribute("mazoTemporal");
        if (mazo == null) mazo = new ArrayList<>();

        // REGLA 1: Solo 1 Líder
        if (nuevaCarta.getTipo().equalsIgnoreCase("LIDER")) {
            sesion.setAttribute("liderSeleccionado", nuevaCarta);
            mazo.clear(); // Si cambias de líder, reseteamos el mazo por precaución de colores
            sesion.setAttribute("mazoTemporal", mazo);
            response.sendRedirect("DeckBuilderServlet?exito=lider_seleccionado");
            return;
        }

        if (lider == null) {
            response.sendRedirect("DeckBuilderServlet?error=falta_lider");
            return;
        }

        // REGLA 2: Límite de 50 cartas
        if (mazo.size() >= 50) {
            response.sendRedirect("DeckBuilderServlet?error=mazo_lleno");
            return;
        }

        // REGLA 3: Límite de 4 copias mecánicas (ignorando el _p1 de artes alternativos)
        String idBaseNueva = nuevaCarta.getIdCarta().split("_")[0]; 
        int copias = 0;
        for (Carta cMazo : mazo) {
            String idBaseMazo = cMazo.getIdCarta().split("_")[0];
            if (idBaseMazo.equals(idBaseNueva)) {
                copias++;
            }
        }
        
        if (copias >= 4) {
            response.sendRedirect("DeckBuilderServlet?error=limite_copias");
            return;
        }

        mazo.add(nuevaCarta);
        sesion.setAttribute("mazoTemporal", mazo);
        response.sendRedirect("DeckBuilderServlet?exito=carta_agregada");
    }

    private void quitarCartaDelMazo(HttpServletRequest request, HttpServletResponse response, HttpSession sesion) throws IOException {
        String idCarta = request.getParameter("idCarta");
        ArrayList<Carta> mazo = (ArrayList<Carta>) sesion.getAttribute("mazoTemporal");
        
        if (mazo != null && idCarta != null) {
            for (int i = 0; i < mazo.size(); i++) {
                if (mazo.get(i).getIdCarta().equals(idCarta)) {
                    mazo.remove(i);
                    break;
                }
            }
            sesion.setAttribute("mazoTemporal", mazo);
        }
        response.sendRedirect("DeckBuilderServlet");
    }

// ==========================================
    // CARGAR UN MAZO DE LA BASE DE DATOS
    // ==========================================
    private void cargarMazoEnSesion(HttpServletRequest request, HttpServletResponse response, HttpSession sesion, String accion) throws SQLException, IOException {
        String idMazoStr = request.getParameter("id");
        if (idMazoStr == null || idMazoStr.isEmpty()) {
            response.sendRedirect("DeckBuilderServlet");
            return;
        }
        
        int idMazo = Integer.parseInt(idMazoStr);
        CartaDAO cartaDAO = new CartaDAO();
        ArrayList<Carta> mazoTemporal = new ArrayList<>();
        Carta lider = null;
        
        datos.ConnectionPool pool = datos.ConnectionPool.getInstance();
        java.sql.Connection con = pool.getConnection();
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        
        try {
            // CORRECCIÓN: Leemos es_publico y es_oficial en lugar de "visibilidad"
            ps = con.prepareStatement("SELECT nombre_mazo, descripcion, es_publico, es_oficial, id_carta_lider, winrate FROM MAZO WHERE id_mazo = ?");
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            if (rs.next()) {
                lider = cartaDAO.obtenerCartaPorId(rs.getString("id_carta_lider"));
                
                // LÓGICA DE TRADUCCIÓN PARA EL DESPLEGABLE
                boolean esPublico = rs.getBoolean("es_publico");
                boolean esOficial = rs.getBoolean("es_oficial");
                String visibilidadStr = "Privado"; // Por defecto
                
                if (esOficial) {
                    visibilidadStr = "Meta";
                } else if (esPublico) {
                    visibilidadStr = "Público";
                }
                
                sesion.setAttribute("mazoNombrePrevio", rs.getString("nombre_mazo"));
                sesion.setAttribute("mazoDescripcionPrevio", rs.getString("descripcion"));
                sesion.setAttribute("mazoVisibilidadPrevia", visibilidadStr);
                sesion.setAttribute("mazoWinratePrevio", rs.getFloat("winrate"));
            }
            rs.close();
            ps.close();
            
            // Extraer el contenido de cartas
            ps = con.prepareStatement("SELECT id_carta, cantidad FROM CONTIENE_CARTA WHERE id_mazo = ?");
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            while (rs.next()) {
                String idCarta = rs.getString("id_carta");
                int cantidad = rs.getInt("cantidad");
                Carta c = cartaDAO.obtenerCartaPorId(idCarta);
                if (c != null) {
                    for (int i = 0; i < cantidad; i++) {
                        mazoTemporal.add(c);
                    }
                }
            }
        } finally {
            if(rs != null) rs.close();
            if(ps != null) ps.close();
            con.close();
        }
        
        sesion.setAttribute("liderSeleccionado", lider);
        sesion.setAttribute("mazoTemporal", mazoTemporal);
        
        if (accion.equals("editar")) {
            sesion.setAttribute("mazoEditandoId", idMazo); 
        } else {
            sesion.removeAttribute("mazoEditandoId"); 
        }
        
        response.sendRedirect("DeckBuilderServlet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}