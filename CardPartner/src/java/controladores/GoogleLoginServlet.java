package controladores;

import datos.UsuarioDAO;
import modelo.Usuario;

import java.io.IOException;
import java.util.UUID;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "GoogleLoginServlet", urlPatterns = {"/GoogleLoginServlet"})
public class GoogleLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        String tokenRecibido = request.getParameter("credential");
        
        if (tokenRecibido != null && !tokenRecibido.isEmpty()) {
            try {
                // 1. Decodificar el Token JWT de Google (Formato: header.payload.signature)
                String[] chunks = tokenRecibido.split("\\.");
                if (chunks.length != 3) throw new Exception("Token inválido");
                
                // Extraemos la parte central (el Payload) que contiene los datos en Base64
                Base64.Decoder decoder = Base64.getUrlDecoder();
                String payloadJSON = new String(decoder.decode(chunks[1]), StandardCharsets.UTF_8);
                
                // Extraemos Email y Nombre del JSON "a mano" para evitar librerías externas
                String emailGoogle = extraerValorJSON(payloadJSON, "email");
                String nombreGoogle = extraerValorJSON(payloadJSON, "name");
                
                if (emailGoogle == null || emailGoogle.isEmpty()) {
                    throw new Exception("El token no contiene un email válido.");
                }
                
                UsuarioDAO dao = new UsuarioDAO();
                Usuario usuarioActual = dao.obtenerUsuarioPorEmail(emailGoogle);
                
                // 2. Si no existe en la BD, lo registramos automáticamente (Auto-Registro)
                if (usuarioActual == null) {
                    usuarioActual = new Usuario();
                    usuarioActual.setEmail(emailGoogle);
                    usuarioActual.setApodoUsuario(nombreGoogle != null ? nombreGoogle : "UsuarioGoogle");
                    usuarioActual.setRol("JUGADOR");
                    
                    // Asignamos una contraseña aleatoria muy larga, ya que siempre entrará con Google
                    usuarioActual.setPassword(UUID.randomUUID().toString()); 
                    
                    int idGenerado = dao.registrarUsuario(usuarioActual);
                    if (idGenerado > 0) {
                        usuarioActual.setIdUsuario(idGenerado);
                    } else {
                        throw new Exception("Error al guardar el nuevo usuario en la Base de Datos");
                    }
                }
                
                // 3. Lo metemos en la sesión (Auto-Login)
                HttpSession session = request.getSession();
                session.setAttribute("usuarioActivo", usuarioActual);
                session.setMaxInactiveInterval(60 * 60 * 24); // Sesión de 1 día
                
                // Redirigimos al perfil con mensaje de bienvenida
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=mi_perfil&exito=bienvenida");
                
            } catch (Exception e) {
                System.err.println("Error en GoogleLoginServlet: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=google_fail");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp?error=google_fail");
        }
    }
    
    /**
     * Función auxiliar rápida para extraer un campo de un JSON sin usar librerías externas.
     */
    private String extraerValorJSON(String json, String clave) {
        String busqueda = "\"" + clave + "\":\"";
        int inicio = json.indexOf(busqueda);
        if (inicio == -1) return null;
        inicio += busqueda.length();
        int fin = json.indexOf("\"", inicio);
        if (fin == -1) return null;
        return json.substring(inicio, fin);
    }
}