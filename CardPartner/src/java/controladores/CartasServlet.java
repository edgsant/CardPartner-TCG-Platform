package controladores;

import datos.CartaDAO;
import modelo.Carta;

import java.io.IOException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CartasServlet", urlPatterns = {"/CartasServlet"})
public class CartasServlet extends HttpServlet {

    // ==========================================
    // 1. EL ENRUTADOR PRINCIPAL
    // ==========================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        // Si no hay acción en la URL, por defecto mostramos el catálogo
        if (accion == null || accion.isEmpty()) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
            case "filtrar":
                mostrarCatalogo(request, response);
                break;
            case "detalle":
                mostrarDetalle(request, response);
                break;
            default:
                // Acción desconocida, mandamos al catálogo por seguridad
                response.sendRedirect("CartasServlet?accion=listar");
                break;
        }
    }

    // ==========================================
    // 2. LÓGICA DEL CATÁLOGO Y FILTROS
    // ==========================================
    private void mostrarCatalogo(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Leer los parámetros del buscador (pueden ser null, tu DAO lo maneja)
        String color = request.getParameter("color");
        String tipo = request.getParameter("tipo");
        String arquetipo = request.getParameter("arquetipo");
        String atributo = request.getParameter("atributo");

        // 2. Matemáticas de Paginación
        int pagina = 1; // Página por defecto
        int limite = 20; // Cuántas cartas mostramos por página

        String paginaParam = request.getParameter("pagina");
        if (paginaParam != null && !paginaParam.isEmpty()) {
            try {
                pagina = Integer.parseInt(paginaParam);
                if (pagina < 1) pagina = 1; // Evitamos páginas negativas
            } catch (NumberFormatException e) {
                pagina = 1; // Si escriben letras en la URL, forzamos página 1
            }
        }
        
        int offset = (pagina - 1) * limite;

        // 3. Hablar con la base de datos
        CartaDAO cartaDAO = new CartaDAO();
        ArrayList<Carta> listaCartas = cartaDAO.filtrarCartas(color, tipo, arquetipo, atributo, limite, offset);

        // 4. Empaquetar datos para el JSP (El regalo)
        request.setAttribute("listaCartas", listaCartas);
        request.setAttribute("paginaActual", pagina); // Se lo pasamos para pintar los botones [Anterior] [Siguiente]
        
        // Para que los select del HTML recuerden qué buscó el usuario:
        request.setAttribute("colorSeleccionado", color);
        request.setAttribute("tipoSeleccionado", tipo);

        // 5. Enviar al Frontend (Usamos FORWARD, no Redirect, para que viajen los atributos)
        request.getRequestDispatcher("/Views/catalogo.jsp").forward(request, response);
    }

    // ==========================================
    // 3. LÓGICA DE UNA CARTA INDIVIDUAL
    // ==========================================
    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            CartaDAO cartaDAO = new CartaDAO();
            Carta cartaActual = cartaDAO.obtenerCartaPorId(idParam);

            if (cartaActual != null) {
                // Si la carta existe, la metemos en la caja y vamos a la vista
                request.setAttribute("cartaParaLaVista", cartaActual);
                request.getRequestDispatcher("/Views/card-view.jsp").forward(request, response);
                return; // Cortamos ejecución
            }
        }
        
        // Si el ID es falso o nulo, redirigimos al catálogo general con un mensaje de error
        response.sendRedirect("CartasServlet?accion=listar&error=carta_no_encontrada");
    }
}