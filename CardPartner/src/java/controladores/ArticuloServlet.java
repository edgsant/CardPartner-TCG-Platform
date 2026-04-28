package controladores;

import datos.ArticuloDAO;
import modelo.Articulo;

import java.sql.*;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ArticuloServlet")
public class ArticuloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tipo = request.getParameter("tipo"); // "NOTICIA", "GUIA", "TORNEO"

        // Si no hay tipo, por defecto mostramos noticias
        // Validación estricta: si es nulo, vacío, o cualquier otra palabra rara, forzamos "NOTICIA"
        if (tipo == null || (!tipo.equals("NOTICIA") && !tipo.equals("GUIA") && !tipo.equals("TORNEO"))) {
            tipo = "NOTICIA";
        }

        // 1. Obtener parámetros de paginación (ahora sí es dinámico)
        int pagina = 1;
        String pParam = request.getParameter("p");
        if (pParam != null && !pParam.isEmpty()) {
            try {
                pagina = Integer.parseInt(pParam);
            } catch (NumberFormatException e) {
                pagina = 1;
            }
        }

        int limite = 5; // Limite unificado para todos (1 principal + 4 secundarios)
        int offset = (pagina - 1) * limite;

        // 2. Cargar datos desde el DAO
        ArticuloDAO dao = new ArticuloDAO();

        try {
            List<Articulo> lista = dao.obtenerPorCategoria(tipo, limite, offset);
            int totalArticulos = dao.contarPorCategoria(tipo);
            int totalPaginas = (int) Math.ceil((double) totalArticulos / limite);

            // 3. Enviar datos al JSP
            request.setAttribute("articulos", lista);
            request.setAttribute("totalPaginas", totalPaginas);

            // 4. Mapeo CORRECTO de rutas (Inglés a Español)
            String destino = "/Views/news.jsp"; // Por defecto
            switch (tipo) {
                case "NOTICIA":
                    destino = "/Views/news.jsp";
                    break;
                case "GUIA":
                    destino = "/Views/guides.jsp";
                    break;
                case "TORNEO":
                    destino = "/Views/tournaments.jsp";
                    break;
            }

            request.getRequestDispatcher(destino).forward(request, response);

        } catch (SQLException e) {
            // EL DAO LANZÓ UN ERROR TÉCNICO
            System.err.println("Error crítico en ArticuloServlet: " + e.getMessage());

            // Pasamos un mensaje amigable a la vista
            request.setAttribute("mensajeError", "Lo sentimos, estamos experimentando problemas técnicos al cargar los artículos. Inténtelo más tarde.");

            // Redirigimos a una página de error genérica (deberíais crear un error.jsp si no lo tenéis)
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }

    }
}
