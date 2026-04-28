package controladores;

import datos.CartaDAO;
import modelo.Carta;

import java.sql.SQLException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CartasApiServlet", urlPatterns = {"/CartasApiServlet"})
public class CartasApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Configurar la respuesta para que el navegador entienda que es JSON puro (y no una web HTML)
        response.setContentType("application/json;charset=UTF-8");

        // 2. Leer los parámetros que nos envía el JavaScript mediante la URL
        String coloresLider = request.getParameter("colores"); // Ej: "ROJO,VERDE" o "AZUL"
        String nombreBuscado = request.getParameter("nombre");

        try {

            // 3. Obtener las cartas de la base de datos
            CartaDAO cartaDAO = new CartaDAO();
            // Como no podemos modificar el DAO, le pedimos un límite alto (1000) para traer el catálogo
            // y hacer nosotros el filtrado complejo aquí en el servidor con Java.
            ArrayList<Carta> todasLasCartas = cartaDAO.filtrarCartas(null, null, null, null, 1000, 0);

            ArrayList<Carta> cartasFiltradas = new ArrayList<>();

            // 4. LÓGICA DE FILTRADO EN MEMORIA
            for (Carta c : todasLasCartas) {

                // Regla A: Jamás enviar cartas de tipo LIDER (no se pueden meter en el mazo principal)
                if ("LIDER".equalsIgnoreCase(c.getTipo())) {
                    continue;
                }

                // Regla B: Si el usuario escribió un nombre, debe coincidir (ignorando mayúsculas)
                if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) {
                    if (!c.getNombreCarta().toLowerCase().contains(nombreBuscado.toLowerCase())) {
                        continue;
                    }
                }

                // Regla C: La carta debe ser del color del líder (o de uno de sus colores)
                if (coloresLider != null && !coloresLider.trim().isEmpty()) {
                    String colorCarta = c.getColor();
                    if (colorCarta != null) {
                        // Comprobamos si el color de la carta está incluido en los colores del líder
                        if (!coloresLider.toUpperCase().contains(colorCarta.toUpperCase())) {
                            continue;
                        }
                    }
                }

                // Si pasa todas las pruebas, la añadimos a la lista final
                cartasFiltradas.add(c);

                // Límite de seguridad: devolvemos un máximo de 50 resultados para que el modal no se sature
                if (cartasFiltradas.size() >= 50) {
                    break;
                }
            }

            // 5. CONSTRUCCIÓN DEL JSON A MANO (Para evitar dependencias externas como Gson)
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            for (int i = 0; i < cartasFiltradas.size(); i++) {
                Carta c = cartasFiltradas.get(i);

                // 1. Limpieza EXTREMA para evitar que JSON.parse() explote en el frontend
                String nombreSeguro = c.getNombreCarta()
                        .replace("\\", "\\\\") // IMPORTANTE: Escapar barras invertidas lo primero
                        .replace("\"", "\\\"") // Escapar comillas dobles
                        .replace("\n", " ") // Cambiar saltos de línea por un espacio
                        .replace("\r", "");    // Eliminar retornos de carro

                String imagenSegura = (c.getImagen() != null) ? c.getImagen().replace("\\", "/") : "";

                jsonBuilder.append("{")
                        .append("\"idCarta\":\"").append(c.getIdCarta()).append("\", ")
                        .append("\"nombreCarta\":\"").append(nombreSeguro).append("\", ")
                        .append("\"imagen\":\"").append(imagenSegura).append("\"")
                        .append("}");

                // Añadimos una coma si no es el último elemento
                if (i < cartasFiltradas.size() - 1) {
                    jsonBuilder.append(", ");
                }
            }

            jsonBuilder.append("]");

            // 6. Enviar el JSON de vuelta al JavaScript
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonBuilder.toString());
                out.flush();
            }

        } catch (SQLException e) {
            // ERROR TÉCNICO: Lo logueamos
            System.err.println("Error de base de datos en CartasApiServlet: " + e.getMessage());

            // Devolvemos un error 500 al navegador y un JSON vacío para que JS no rompa
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("[]");
                out.flush();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
