package datos;

import java.io.IOException;
import java.io.InputStream;
import modelo.Usuario;
import java.sql.*;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Usuario. Se encarga de
 * gestionar el ciclo de vida de las cuentas de usuario (registro, login,
 * edición de perfil, cambio de contraseñas y borrado de cuenta), así como el
 * manejo optimizado de archivos binarios (imágenes de avatar) en la base de
 * datos. Temporalmente gestiona también la funcionalidad de la Wishlist.
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
     * Inserta un nuevo usuario en la base de datos durante el proceso de
     * registro. Gestiona correctamente la subida de un avatar inicial si el
     * usuario proporciona uno.
     *
     * * @param u El objeto Usuario poblado con los datos del formulario de
     * registro.
     * @return El ID numérico autogenerado por la base de datos si tiene éxito;
     * 0 si falla.
     * @throws SQLException Si ocurre un error de inserción (ej: email o apodo
     * duplicado).
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
            cerrarRecursos(pool, con, ps, rs, "registrarUsuario");
        }
        return idGenerado;
    }

    /**
     * Elimina permanentemente una cuenta de usuario del sistema. Gracias a las
     * restricciones ON DELETE CASCADE de la base de datos, esta acción borrará
     * automáticamente todos sus mazos, artículos, comentarios y valoraciones
     * asociados.
     *
     * * @param idUsuario El ID del usuario que solicita la baja.
     * @return true si la cuenta fue eliminada con éxito.
     * @throws SQLException Si ocurre un error crítico durante el borrado.
     */
    public boolean borrarUsuario(int idUsuario) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("DELETE FROM USUARIO WHERE id_usuario = ?");
            ps.setInt(1, idUsuario);
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error crítico al intentar borrar el usuario ID " + idUsuario + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, null, "borrarUsuario");
        }
        return exito;
    }

    /**
     * Verifica las credenciales de un usuario durante el proceso de inicio de
     * sesión. Compara el email y la contraseña (previamente hasheada en el
     * Servlet) con los registros.
     *
     * * @param email El correo electrónico introducido por el usuario.
     * @param password La contraseña (en formato hash SHA-256) introducida por
     * el usuario.
     * @return Un objeto Usuario con sus datos básicos poblados si el login es
     * correcto; null si falla.
     * @throws SQLException Si ocurre un error de consulta a la base de datos.
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
                usuarioLogueado = mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al validar login del email " + email + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, rs, "validarLogin");
        }

        return usuarioLogueado;
    }

    /**
     * Comprueba si un correo electrónico ya está registrado en el sistema.
     * Utilizado para validaciones asíncronas (AJAX) o previas al registro para
     * evitar errores SQL.
     *
     * * @param email El correo electrónico a verificar.
     * @return true si el email ya existe en la base de datos; false en caso
     * contrario.
     * @throws SQLException Si ocurre un error de consulta.
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
            cerrarRecursos(pool, connection, ps, rs, "existeEmail");
        }
        return existe;
    }

    /**
     * Recupera el archivo binario (imagen) del avatar de un usuario específico.
     * Extrae el objeto BLOB de MariaDB y lo convierte en un array de bytes para
     * su renderizado web.
     *
     * * @param idUsuario El ID del usuario cuyo avatar se quiere recuperar.
     * @return Un array de bytes (byte[]) con la imagen; null si el usuario no
     * tiene avatar.
     * @throws SQLException Si ocurre un error de base de datos.
     * @throws IOException Si ocurre un error al procesar el flujo de datos
     * binarios.
     */
    public byte[] obtenerAvatar(int idUsuario) throws SQLException, IOException {
        byte[] imagenBytes = null;
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
                    // Extraemos todos los bytes de golpe
                    imagenBytes = blob.getBytes(1, (int) blob.length());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el avatar del usuario ID " + idUsuario + ": " + e.getMessage());
            throw e; // Lanzamos tanto SQL como IO exceptions
        } finally {
            cerrarRecursos(pool, connection, ps, rs, "obtenerAvatar");
        }
        return imagenBytes;
    }

    // ==========================================
    // 3. MÉTODOS DE ACTUALIZACIÓN DE PERFIL
    // ==========================================
    /**
     * Actualiza la información básica del perfil del usuario (apodo y email)
     * sin tocar su imagen.
     *
     * * @param idUsuario El ID del usuario a actualizar.
     * @param nuevoApodo El nuevo apodo elegido por el usuario.
     * @param nuevoEmail El nuevo correo electrónico elegido.
     * @return true si la actualización fue exitosa.
     * @throws SQLException Si ocurre un error durante el UPDATE (ej: apodo ya
     * en uso por otro).
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
            cerrarRecursos(pool, con, ps, null, "actualizarDatosPerfil");
        }
        return exito;
    }

    /**
     * Actualiza el perfil del usuario incluyendo un cambio en su foto de perfil
     * (avatar). Discrimina de forma inteligente la consulta SQL a lanzar
     * dependiendo de si el InputStream del nuevo avatar es nulo o no.
     *
     * * @param idUsuario El ID del usuario a modificar.
     * @param nuevoApodo El nuevo apodo elegido.
     * @param nuevoEmail El nuevo correo electrónico elegido.
     * @param nuevoAvatar InputStream conteniendo el nuevo archivo de imagen
     * subido (puede ser null).
     * @return true si la base de datos se actualizó correctamente.
     * @throws SQLException Si ocurre un error durante la actualización.
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
            } // Si NO ha subido foto (solo quiere cambiar texto)
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
            cerrarRecursos(pool, con, ps, null, "actualizarPerfilCompleto");
        }
        return exito;
    }

    /**
     * Modifica la contraseña de un usuario en la base de datos.
     *
     * * @param idUsuario El ID del usuario que solicita el cambio.
     * @param nuevaPass El nuevo hash SHA-256 de la contraseña.
     * @return true si se actualizó con éxito.
     * @throws SQLException Si ocurre un error durante la consulta.
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
            cerrarRecursos(pool, con, ps, null, "actualizarPassword");
        }
        return exito;
    }

    /**
     * Borra la foto de perfil actual del usuario estableciendo la columna
     * avatar a NULL.
     *
     * * @param idUsuario El ID del usuario que desea eliminar su foto.
     * @return true si la operación se completó con éxito.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public boolean eliminarAvatar(int idUsuario) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE USUARIO SET avatar = NULL WHERE id_usuario = ?");
            ps.setInt(1, idUsuario);
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el avatar del usuario ID " + idUsuario + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, null, "eliminarAvatar");
        }
        return exito;
    }

    /**
     * Recupera toda la información de un usuario utilizando únicamente su
     * correo electrónico. Muy útil para integraciones de inicio de sesión de
     * terceros (como Google OAuth) donde no se requiere contraseña inicial.
     *
     * * @param email El correo electrónico del usuario a buscar.
     * @return El objeto Usuario poblado, o null si no existe ninguna cuenta
     * asociada a ese email.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    public Usuario obtenerUsuarioPorEmail(String email) throws SQLException {
        Usuario usuario = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement("SELECT * FROM USUARIO WHERE email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por email: " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, rs, "obtenerUsuarioPorEmail");
        }
        return usuario;
    }

    //FUNCIONALIDADES A MIGRAR A WISHLIST -----------------------------------------------------------------------------------
    /**
     * Añade una carta específica a la lista de deseos (Wishlist) del usuario.
     *
     * * @param idUsuario El ID del usuario.
     * @param idCarta El ID de la carta a añadir.
     * @return true si la inserción fue exitosa.
     * @throws SQLException Si ocurre un error o si la carta ya está en la
     * lista.
     */
    public boolean añadirAWishlist(int idUsuario, String idCarta) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO WISHLIST (id_usuario, id_carta) VALUES (?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, idCarta);
            exito = ps.executeUpdate() > 0;
        } finally {
            // Usa tu método cerrarRecursos existente
            cerrarRecursos(pool, con, ps, null, "añadirAWishlist");
        }
        return exito;
    }

    /**
     * Recupera la lista de cartas que el usuario ha guardado en su lista de
     * deseos, ordenadas por la fecha en la que fueron añadidas (las más
     * recientes primero).
     *
     * * @param idUsuario El ID del usuario a consultar.
     * @return Un ArrayList de objetos Carta con su información básica.
     * @throws SQLException Si ocurre un error durante la consulta JOIN.
     */
    public java.util.ArrayList<modelo.Carta> obtenerCartasWishlist(int idUsuario) throws SQLException {
        java.util.ArrayList<modelo.Carta> cartas = new java.util.ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT c.* FROM CARTA c JOIN WISHLIST w ON c.id_carta = w.id_carta WHERE w.id_usuario = ? ORDER BY w.fecha_añadido DESC";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                modelo.Carta c = new modelo.Carta();
                c.setIdCarta(rs.getString("id_carta"));
                c.setNombreCarta(rs.getString("nombre_carta"));
                c.setImagen(rs.getString("imagen"));
                cartas.add(c);
            }
        } finally {
            cerrarRecursos(pool, con, ps, rs, "obtenerCartasWishlist");
        }
        return cartas;
    }

    /**
     * Elimina una carta de la lista de deseos del usuario.
     *
     * * @param idUsuario El ID del usuario.
     * @param idCarta El ID de la carta a eliminar.
     * @return true si el borrado fue exitoso.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public boolean quitarDeWishlist(int idUsuario, String idCarta) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        try {
            String sql = "DELETE FROM WISHLIST WHERE id_usuario = ? AND id_carta = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, idCarta);
            exito = ps.executeUpdate() > 0;
        } finally {
            cerrarRecursos(pool, con, ps, null, "quitarDeWishlist");
        }
        return exito;
    }

    /**
     * Comprueba de forma booleana si una carta concreta ya está presente en la
     * lista de deseos del usuario. Útil para determinar qué tipo de botón
     * renderizar en el frontend ("Añadir" vs "Quitar").
     *
     * * @param idUsuario El ID del usuario.
     * @param idCarta El ID de la carta a comprobar.
     * @return true si la relación ya existe; false en caso contrario.
     * @throws SQLException Si ocurre un error de lectura.
     */
    public boolean estaEnWishlist(int idUsuario, String idCarta) throws SQLException {
        boolean existe = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM WISHLIST WHERE id_usuario = ? AND id_carta = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, idCarta);
            rs = ps.executeQuery();
            existe = rs.next();
        } finally {
            cerrarRecursos(pool, con, ps, rs, "estaEnWishlist");
        }
        return existe;
    }

    // ==========================================
    //  METODOS AUXILIARES
    // ==========================================
    /**
     * Método auxiliar que transforma la fila actual de un ResultSet en un
     * objeto Usuario. Centraliza la lógica de extracción para evitar repetición
     * de código. Nota: El avatar no se extrae aquí porque se gestiona como un
     * flujo de bytes independiente.
     *
     * * @param rs El ResultSet posicionado en la fila actual.
     * @return El objeto Usuario poblado.
     * @throws SQLException Si ocurre un error de lectura de columnas.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setApodoUsuario(rs.getString("apodo_usuario"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));
        u.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return u;
    }

    /**
     * Centraliza y asegura el cierre de los recursos JDBC (ResultSet,
     * PreparedStatement y Connection). Utiliza bloques try-catch individuales
     * para evitar que un fallo en el cierre de un recurso impida liberar la
     * conexión al pool. Incorpora trazabilidad dinámica del origen del error.
     *
     * * @param pool La instancia del ConnectionPool gestor.
     * @param con La conexión a liberar.
     * @param ps El PreparedStatement a cerrar.
     * @param rs El ResultSet a cerrar.
     * @param nombreMetodo Nombre del método invocador (para facilitar el
     * rastreo en los logs).
     */
    private void cerrarRecursos(ConnectionPool pool, Connection con, PreparedStatement ps, ResultSet rs, String nombreMetodo) {
        String ubicacion = this.getClass().getSimpleName() + "." + nombreMetodo;
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error RS en " + ubicacion + ": " + e.getMessage());
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.err.println("Error PS en " + ubicacion + ": " + e.getMessage());
            }
        }
        if (pool != null && con != null) {
            pool.freeConnection(con);
        }
    }

}
