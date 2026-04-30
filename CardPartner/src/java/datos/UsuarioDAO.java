package datos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import modelo.Usuario;
import java.sql.*;

/**
 * Se encarga de todo lo que tenga que ver con la gestión de cuentas.
 *
 * @author edgar
 */
public class UsuarioDAO {

    // ==========================================
    // 1. CONSTANTES SQL
    // ==========================================
    private static final String SQL_INSERT = "INSERT INTO USUARIO (apodo_usuario, email, password, avatar, rol) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_LOGIN = "SELECT * FROM USUARIO WHERE email = ? AND password = ?";
    private static final String SQL_CHECK_EMAIL = "SELECT id_usuario FROM USUARIO WHERE email = ?";
    private static final String SQL_GET_AVATAR = "SELECT avatar FROM USUARIO WHERE id_usuario = ?";

    // Querys para poder actualizar los perfiles
    private static final String SQL_UPDATE_DATOS = "UPDATE USUARIO SET apodo_usuario = ?, email = ? WHERE id_usuario = ?";
    private static final String SQL_UPDATE_PASS = "UPDATE USUARIO SET password = ? WHERE id_usuario = ?";
    
    
    
    /**
     * La Anatomía de un Método DAO
     *
     * Todos los métodos de un DAO siguen este mismo "ritual" de 5 pasos:
     * Conectar: Llama al ConnectionPool. Preparar el SQL: Escribe el String con
     * la consulta usando ?. Rellenar datos (El puente DTO -> SQL): Usa los
     * getters del DTO para sustituir las ? por los datos reales . Ejecutar:
     * Dispara la consulta a MariaDB. Si es un SELECT, usa executeQuery(). Si es
     * un INSERT/UPDATE/DELETE, usa executeUpdate(). Desconectar: Devuelve la
     * conexión.
     */
    
    
    /**
     * Inserta usuario y devuelve su ID generado por la base de datos
     */
    public int registrarUsuario(Usuario u) throws SQLException {

        int idGenerado = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getApodoUsuario());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());

            // Lógica del Blob para el avatar
            if (u.getAvatar() != null) {
                // Como getAvatar() ya devuelve un InputStream, se lo pasamos directamente
                ps.setBlob(4, u.getAvatar());
            } else {
                ps.setNull(4, Types.BLOB);
            }

            ps.setString(5, u.getRol());
            ps.executeUpdate();

            // Recuperar el ID autogenerado
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario " + u.getEmail() + ": " + e.getMessage());
            throw e; // Lanzamos el error al Servlet
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return idGenerado;
    }

    /**
     * Verifica las credenciales y devuelve el usuario con sus datos. Devuelve
     * null si el email o la contraseña son incorrectos.
     */
    public Usuario validarLogin(String email, String password) throws SQLException {
        Usuario usuarioLogueado = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        
        try {
            ps = con.prepareStatement(SQL_LOGIN); // Usamos la constante
            ps.setString(1, email);    
            ps.setString(2, password); 

            rs = ps.executeQuery();

            if (rs.next()) {
                usuarioLogueado = new Usuario();
                usuarioLogueado.setIdUsuario(rs.getInt("id_usuario"));
                usuarioLogueado.setApodoUsuario(rs.getString("apodo_usuario"));
                usuarioLogueado.setEmail(rs.getString("email"));
                usuarioLogueado.setPassword(rs.getString("password"));
                usuarioLogueado.setRol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.err.println("Error al validar login del email " + email + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }

        return usuarioLogueado;
    }

    /**
     * Comprueba si el email ya existe en la tabla USUARIO. Devuelve true si
     * existe
     */
    public boolean existeEmail(String email) throws SQLException {
        boolean existe = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT email FROM USUARIO WHERE email = ?";

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();

            // Si el cursor puede moverse a la primera fila, el email existe[cite: 396].
            existe = rs.next();
        } catch (SQLException e) {
            System.err.println("Error al comprobar si existe el email " + email + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(connection, ps, rs);
        }
        return existe;
    }

    /**
     * Lee el BLOB de la base de datos y lo envía al flujo de salida
     * (navegador)[cite: 807].
     */
    public void obtenerAvatar(int idUsuario, OutputStream respuesta) throws SQLException, IOException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT avatar FROM USUARIO WHERE id_usuario = ?";

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                Blob blob = rs.getBlob("avatar");
                // Verificamos que no sea nulo y tenga contenido[cite: 818].
                if (blob != null && blob.length() > 0) {
                    InputStream imagen = blob.getBinaryStream();
                    byte[] buffer = new byte[1024]; // Buffer de 1KB.
                    int len;

                    // Leemos por trozos y escribimos en la respuesta [cite: 821-825].
                    while ((len = imagen.read(buffer)) != -1) {
                        respuesta.write(buffer, 0, len);
                    }
                    imagen.close();
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error al obtener el avatar del usuario ID " + idUsuario + ": " + e.getMessage());
            throw e; // Lanzamos tanto SQL como IO exceptions
        } finally {
            cerrarRecursos(connection, ps, rs);
        }
    }
    
    // ==========================================
    // 3. MÉTODOS DE ACTUALIZACIÓN DE PERFIL
    // ==========================================

    /**
     * Actualiza el apodo y el email del usuario.
     *
     * @param idUsuario
     * @param nuevoApodo
     * @param nuevoEmail
     * @return
     */
    public boolean actualizarDatosPerfil(int idUsuario, String nuevoApodo, String nuevoEmail) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(SQL_UPDATE_DATOS);
            ps.setString(1, nuevoApodo);
            ps.setString(2, nuevoEmail);
            ps.setInt(3, idUsuario);

            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar datos del usuario ID " + idUsuario + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }
    
    /**
     * Actualiza el perfil del usuario. Si se proporciona un nuevo avatar (InputStream),
     * también lo actualiza en la base de datos.
     */
    public boolean actualizarPerfilCompleto(int idUsuario, String nuevoApodo, String nuevoEmail, InputStream nuevoAvatar) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            // Si el usuario ha subido una foto nueva
            if (nuevoAvatar != null) {
                String sql = "UPDATE USUARIO SET apodo_usuario = ?, email = ?, avatar = ? WHERE id_usuario = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, nuevoApodo);
                ps.setString(2, nuevoEmail);
                ps.setBlob(3, nuevoAvatar);
                ps.setInt(4, idUsuario);
            } 
            // Si NO ha subido foto (solo quiere cambiar texto)
            else {
                String sql = "UPDATE USUARIO SET apodo_usuario = ?, email = ? WHERE id_usuario = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, nuevoApodo);
                ps.setString(2, nuevoEmail);
                ps.setInt(3, idUsuario);
            }

            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar perfil completo del usuario ID " + idUsuario + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Actualiza la contraseña del usuario.
     *
     * @param idUsuario
     * @param nuevaPass
     * @return
     */
    public boolean actualizarPassword(int idUsuario, String nuevaPass) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(SQL_UPDATE_PASS);
            ps.setString(1, nuevaPass);
            ps.setInt(2, idUsuario);

            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña del usuario ID " + idUsuario + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }
    
    /**
     * Centraliza el cierre de recursos de forma segura (sin fugas de conexión).
     */
    private void cerrarRecursos(Connection con, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement: " + e.getMessage());
            }
        }
        if (con != null) {
            ConnectionPool.getInstance().freeConnection(con);
        }
    }

}
