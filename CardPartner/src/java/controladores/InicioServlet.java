package controladores;

import datos.MazoDAO;
import modelo.Mazo;
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
        

        try {
            // 1. Buscamos los mazos reales de tu base de datos (para la sección de comunidad)
            List<Mazo> mazosRecomendados = mazoDao.filtrarMazosPublicos(null, null, null, null, null, 3, 0);

            // 2. Pasamos la lista al request. 
            // Si la lista está vacía, el JSP mostrará el mensaje "Aún no hay mazos".
            request.setAttribute("mazosRecomendados", mazosRecomendados);

        } catch (SQLException e) {
            System.err.println("Error en InicioServlet al cargar recomendados: " + e.getMessage());
            // En caso de error, mandamos null para que la web no explote y muestre un mensaje fallback en el JSP
            request.setAttribute("mazosRecomendados", null);
        }

        // 4. SIEMPRE redirigimos al JSP
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}