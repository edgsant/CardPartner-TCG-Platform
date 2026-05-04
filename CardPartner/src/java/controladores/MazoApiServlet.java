package controladores;

import datos.MazoDAO;
import modelo.Mazo;
import modelo.ContieneCarta;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ObtenerDetallesMazo", urlPatterns = {"/ObtenerDetallesMazo"})
public class MazoApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String idParam = request.getParameter("id");
        
        System.out.println("DEBUG: Petición recibida para ID mazo: " + idParam);

        try (PrintWriter out = response.getWriter()) {
            if (idParam == null || idParam.isEmpty()) {
                out.print("{\"error\": \"ID no recibido\"}");
                return;
            }

            int idMazo = Integer.parseInt(idParam);
            MazoDAO dao = new MazoDAO();
            Mazo m = dao.obtenerMazoPorId(idMazo);
            ArrayList<ContieneCarta> cartas = dao.obtenerCartasDeUnMazo(idMazo);

            if (m == null) {
                out.print("{\"error\": \"Mazo no encontrado\"}");
                return;
            }

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"nombre\":\"").append(m.getNombreMazo().replace("\"", "\\\"")).append("\", ");
            json.append("\"descripcion\":\"").append(m.getDescripcion() != null ? m.getDescripcion().replace("\"", "\\\"") : "").append("\", ");
            json.append("\"idLider\":\"").append(m.getIdCartaLider()).append("\", ");
            json.append("\"colorLider\":\"").append(m.getCartaLider() != null ? m.getCartaLider().getColor() : "").append("\", ");
            json.append("\"cartas\": [");

            for (int i = 0; i < cartas.size(); i++) {
                ContieneCarta cc = cartas.get(i);
                String img = cc.getCarta().getImagen();
                json.append("{");
                json.append("\"idCarta\":\"").append(cc.getIdCarta()).append("\", ");
                json.append("\"cantidad\":").append(cc.getCantidad()).append(", ");
                json.append("\"imagen\":\"").append(img != null ? img : "").append("\"");
                json.append("}");
                if (i < cartas.size() - 1) json.append(", ");
            }
            json.append("]}");
            out.print(json.toString()); // Fix de imprimir el JSON
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}