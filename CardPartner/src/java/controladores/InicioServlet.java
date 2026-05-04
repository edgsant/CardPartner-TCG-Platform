package controladores;

import datos.MazoDAO;
import datos.ArticuloDAO;
import modelo.Mazo;
import modelo.Articulo;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import java.sql.SQLException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "InicioServlet", urlPatterns = {"/InicioServlet"})
public class InicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        MazoDAO mazoDao = new MazoDAO();
        ArticuloDAO articuloDao = new ArticuloDAO();
                 
        try {
            // 1. Buscamos los 3 mazos más recientes
            List<Mazo> mazosRecomendados = mazoDao.filtrarMazosPublicos(null, null, null, null, null, 3, 0);
            request.setAttribute("mazosRecomendados", mazosRecomendados);

            // 2. Buscamos la última guía (1 resultado, offset 0)
            List<Articulo> guiasRecomendadas = articuloDao.obtenerPorCategoria("GUIA", 1, 0);
            request.setAttribute("guiasRecomendadas", guiasRecomendadas);
            
        } catch (SQLException e) {
            System.err.println("Error en InicioServlet al cargar recomendados: " + e.getMessage());
            request.setAttribute("mazosRecomendados", null);
            request.setAttribute("guiasRecomendadas", null);
        }

        // 3. SIEMPRE redirigimos al JSP
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}