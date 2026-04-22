package datos;

import java.sql.*;
import modelo.Usuario;
// Nuevos imports obligatorios para buscar en el context.xml
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class GestionDB {

    public Usuario validarUsuario(String nombre, String password) {
        Usuario usuarioEncontrado = null;

        try {
            // 1. Conexión mediante el Pool configurado en context.xml (JNDI)
            Context initContext = new InitialContext();
            // Buscamos el entorno del servidor Tomcat
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            // Buscamos nuestro recurso exacto por el nombre que le pusimos
            DataSource ds = (DataSource)envContext.lookup("jdbc/CardPartnerDB");
            
            // Pedimos una conexión libre al servidor
            Connection con = ds.getConnection();
            
            // 2. Preparar la consulta SQL (esto se mantiene exactamente igual)
            String sql = "SELECT * FROM usuarios WHERE nombre=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, password);

            // 3. Ejecutar y leer los resultados
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                usuarioEncontrado = new Usuario();
                usuarioEncontrado.setApodoUsuario(rs.getString("nombre"));
                usuarioEncontrado.setEmail(rs.getString("email"));
            }
            
            // 4. MUY IMPORTANTE: Devolver la conexión al servidor (no la cierra, la libera)
            con.close(); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return usuarioEncontrado;
    }
}