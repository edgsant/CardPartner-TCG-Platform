package controladores;

import datos.UsuarioDAO;
import modelo.Usuario;

//Para el hasheo
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.ServletException;
import java.sql.SQLException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

// TODO: Hashear contraseña
@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5) // Límite de 5MB por foto
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si intentan entrar por URL, los devolvemos al formulario visual
        response.sendRedirect(request.getContextPath() + "/Views/registro.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String apodo = request.getParameter("apodo");
        String email = request.getParameter("email");
        String passwordPlana = request.getParameter("password");

        // Mejor validación:
        if (apodo == null || email == null || passwordPlana == null
                || apodo.trim().isEmpty() || email.trim().isEmpty() || passwordPlana.trim().isEmpty()) {
            response.sendRedirect("Views/registro.jsp?error=campos_vacios");
            return;
        }

        String passwordHasheada = hashearConSHA256(passwordPlana);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setApodoUsuario(apodo);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(passwordHasheada);
        nuevoUsuario.setRol("JUGADOR"); // <-- ¡AÑADE ESTO!

        // --- TRATAMIENTO DEL ARCHIVO ---
        Part filePart = request.getPart("avatar"); // Lee el input type="file"

        if (filePart != null && filePart.getSize() > 0) {
            // Extraemos el flujo de bytes de la imagen y se lo damos al usuario
            InputStream fileContent = filePart.getInputStream();
            nuevoUsuario.setAvatar(fileContent);
        } else {
            // Si el usuario no ha subido nada, el avatar será null
            nuevoUsuario.setAvatar(null);
        }

        try {

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            int idGenerado = usuarioDAO.registrarUsuario(nuevoUsuario);

            if (idGenerado != 0) {
                response.sendRedirect("Views/login.jsp?exito=registro_ok");
            } else {
                response.sendRedirect("Views/registro.jsp?error=email_duplicado");
            }
        } catch (SQLException e) {
            // 🟢 AÑADIDO: Manejo del error si se cae la base de datos
            System.err.println("Error crítico en RegistroServlet: " + e.getMessage());
            request.setAttribute("mensajeError", "Problemas técnicos al crear su cuenta. Inténtelo más tarde.");
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
