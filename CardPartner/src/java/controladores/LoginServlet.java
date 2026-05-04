package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import java.sql.SQLException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie; //con este import mantenemos la sesion, las famosas cookies
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import datos.UsuarioDAO;

//Para el hasheo
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recogemos los datos del formulario
        String identificador = request.getParameter("email");
        String passIngresada = request.getParameter("password");
        String passHasheada = hashearConSHA256(passIngresada);
        

        // 🌟 NUEVO: Recogemos el valor de la casilla "Mantener sesión iniciada"
        String recordarUsuario = request.getParameter("recordar");

        try {

            // 2. Llamamos al DAO para que busque en MariaDB
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuarioLogueado = dao.validarLogin(identificador, passHasheada);

            // 3. Comprobamos la respuesta
            if (usuarioLogueado != null) {

                HttpSession session = request.getSession();
                session.setAttribute("usuarioActivo", usuarioLogueado);
                session.setMaxInactiveInterval(60 * 60 * 24); // para que la sesión dure un día entero

                if (recordarUsuario != null) {
                    // Si el usuario marcó la casilla, le creamos una Cookie con su email
                    Cookie cookie = new Cookie("correoRecordado", identificador);

                    // Le decimos que la cookie caducará en 30 días (en segundos: 60s * 60m * 24h * 30d)
                    cookie.setMaxAge(60 * 60 * 24 * 30);
                    cookie.setPath("/"); // Para que la cookie se lea en toda tu web

                    // Se la enviamos al navegador para que la guarde en su disco duro
                    response.addCookie(cookie);
                }

                // Redirigimos a la página principal (index.jsp)
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                //response.sendRedirect(request.getContextPath() + "/InicioServlet");

            } else {
                // Login fallido
                response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=true");
            }
        } catch (SQLException e) {
            // 🟢 AÑADIDO: Manejo del error si la BD está apagada
            System.err.println("Error de base de datos en LoginServlet: " + e.getMessage());
            request.setAttribute("mensajeError", "Problemas técnicos al intentar iniciar sesión. Inténtelo más tarde.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }
    }
    
    private String hashearConSHA256(String passwordPlana) {
        try {
            // Le pedimos a Java que use el algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convertimos la contraseña en bytes y la hasheamos
            byte[] hashBytes = digest.digest(passwordPlana.getBytes(StandardCharsets.UTF_8));

            // Convertimos los bytes del hash a texto hexadecimal (para poder guardarlo en el VARCHAR de tu BD)
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString(); // Esto devuelve un texto de 64 caracteres

        } catch (Exception e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
}
