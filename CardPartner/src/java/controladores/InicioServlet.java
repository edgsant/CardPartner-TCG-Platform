package controladores;

import datos.MazoDAO;
import datos.CartaDAO;
import modelo.Mazo;
import modelo.Carta;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
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
        CartaDAO cartaDao = new CartaDAO();

        try {
            // 1. Buscamos los mazos reales de tu base de datos (para la sección de comunidad)
            List<Mazo> mazosRecomendados = mazoDao.filtrarMazosPublicos(null, null, null, null, null, 3, 0);

            // 2. Si hay mazos, les asociamos su objeto Carta líder para que el JSP tenga la imagen
            if (mazosRecomendados != null) {
                for (Mazo m : mazosRecomendados) {
                    Carta lider = cartaDao.obtenerCartaPorId(m.getIdCartaLider());
                    m.setCartaLider(lider);
                }
            }

            // 3. Pasamos la lista al request. 
            // Si la lista está vacía, el JSP mostrará el mensaje "Aún no hay mazos".
            request.setAttribute("mazosRecomendados", mazosRecomendados);

        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error de conexión, mandamos una lista vacía para que la web no explote
            request.setAttribute("mazosRecomendados", null);
        }

        // 4. SIEMPRE redirigimos al JSP
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}