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

        // 1. Pedimos a MariaDB los 3 mazos públicos más recientes
        List<Mazo> mazosRecomendados = mazoDao.filtrarMazosPublicos(null, null, null, null, 3, 0);

        // 2. Asociamos la carta líder a cada mazo desde el servidor (así el JSP no tiene que usar CartaDAO)
        if (mazosRecomendados != null) {
            for (Mazo m : mazosRecomendados) {
                Carta lider = cartaDao.obtenerCartaPorId(m.getIdCartaLider());
                m.setCartaLider(lider);
            }
        }

        // 3. Empaquetamos y mandamos al index.jsp
        request.setAttribute("mazosRecomendados", mazosRecomendados);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}