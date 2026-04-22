package controladores;

import datos.CartaDAO;
import modelo.Carta;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DeckBuilderServlet", urlPatterns = {"/DeckBuilderServlet"})
public class DeckBuilderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. (Opcional) Podemos comprobar si hay sesión iniciada para evitar que 
        // invitados intenten crear mazos, aunque el GuardarMazoServlet ya les echará.
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioActivo") == null) {
            // Si quieres forzar el login antes de entrar al builder, descomenta esta línea:
            // response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=necesitas_login");
            // return;
        }

        // 2. Conectamos con el DAO de las cartas
        CartaDAO cartaDAO = new CartaDAO();

        // 3. Usamos el método filtrarCartas buscando EXCLUSIVAMENTE los líderes
        // Orden de parámetros: (color, tipo, arquetipo, atributo, limite, offset)
        // Le ponemos 100 de límite para asegurarnos de traer todos los líderes disponibles.
        List<Carta> listaLideres = cartaDAO.filtrarCartas(null, "LIDER", null, null, 100, 0);

        // 4. Empaquetamos la lista con la etiqueta exacta que espera nuestro escudo MVC
        request.setAttribute("listaLideres", listaLideres);

        // 5. Enviamos al usuario y a los datos hacia la vista del constructor de mazos
        request.getRequestDispatcher("/Views/deck-builder.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Por si acaso algún enlace redirige aquí por POST, lo mandamos al GET
        doGet(request, response);
    }
}