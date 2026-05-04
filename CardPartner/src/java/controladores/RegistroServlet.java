package controladores;

import datos.UsuarioDAO;
import modelo.Usuario;
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
import jakarta.servlet.http.HttpSession; // Importante para el auto-login

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5) // Límite de 5MB por foto
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si intentan entrar por URL, los devolvemos al formulario visual
        response.sendRedirect(request.getContextPath() + "/Views/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String apodo = request.getParameter("nuevo_usuario");
        String email = request.getParameter("email");
        String passwordPlana = request.getParameter("nueva_password");
        String repetirPassword = request.getParameter("repetir_password");

        // 1. Validar que no haya campos vacíos
        if (apodo == null || email == null || passwordPlana == null || repetirPassword == null
                || apodo.trim().isEmpty() || email.trim().isEmpty() || passwordPlana.trim().isEmpty()) {
            response.sendRedirect("Views/register.jsp?error=campos_vacios");
            return;
        }

        // 2. Validar que las contraseñas coinciden
        if (!passwordPlana.equals(repetirPassword)) {
            response.sendRedirect("Views/register.jsp?error=passwords");
            return;
        }

        String passwordHasheada = hashearConSHA256(passwordPlana);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setApodoUsuario(apodo);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(passwordHasheada);
        nuevoUsuario.setRol("JUGADOR");

        // --- TRATAMIENTO DEL ARCHIVO ---
        Part filePart = request.getPart("avatar");
        if (filePart != null && filePart.getSize() > 0) {
            InputStream fileContent = filePart.getInputStream();
            nuevoUsuario.setAvatar(fileContent);
        } else {
            nuevoUsuario.setAvatar(null);
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            // Comprobamos si el email ya existe ANTES de intentar registrar
            if (usuarioDAO.existeEmail(email)) {
                response.sendRedirect("Views/register.jsp?error=emailexiste");
                return;
            }

            int idGenerado = usuarioDAO.registrarUsuario(nuevoUsuario);

            if (idGenerado != 0) {
                // ==========================================
                // LÓGICA DE AUTO-LOGIN
                // ==========================================
                // 1. Asignamos al usuario el ID real que le acaba de dar la base de datos
                nuevoUsuario.setIdUsuario(idGenerado);
                
                // 2. Le creamos la sesión para que ya esté logueado
                HttpSession session = request.getSession();
                session.setAttribute("usuarioActivo", nuevoUsuario);
                session.setMaxInactiveInterval(60 * 60 * 24); // Sesión de 1 día
                
                // 3. Lo redirigimos a su perfil pasando por el Servlet (para que cargue sus mazos vacíos)
                // y le mandamos el parámetro de "bienvenida"
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=mi_perfil&exito=bienvenida");
                
            } else {
                response.sendRedirect("Views/register.jsp?error=db");
            }

        } catch (SQLException e) {
            System.err.println("Error crítico en RegistroServlet: " + e.getMessage());
            request.setAttribute("mensajeError", "Problemas técnicos al crear su cuenta. Inténtelo más tarde.");
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }
    }

    private String hashearConSHA256(String passwordPlana) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(passwordPlana.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
}