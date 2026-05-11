package datos;

import java.sql.*;
import java.util.ArrayList;
import modelo.Comentario;
import modelo.Usuario; // ¡Importante importar el modelo Usuario!

public class ComentarioDAO {

    // ==========================================
    // 1. CONSTANTES SQL
    // ==========================================
    private static final String SQL_INSERT = "INSERT INTO COMENTARIO (id_usuario_autor, id_mazo_destino, contenido, fecha_creacion) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

    // IMPORTANTE: Ya NO traemos u.avatar para ahorrar RAM y no saturar la conexión con BLOBs
    private static final String SQL_SELECT_BY_MAZO = "SELECT c.*, u.apodo_usuario FROM COMENTARIO c INNER JOIN USUARIO u ON c.id_usuario_autor = u.id_usuario WHERE c.id_mazo_destino = ? ORDER BY c.fecha_creacion DESC";

    private static final String SQL_UPDATE = "UPDATE COMENTARIO SET contenido = ?, fecha_edicion = CURRENT_TIMESTAMP WHERE id_comentario = ? AND id_usuario_autor = ?";

    private static final String SQL_DELETE = "DELETE FROM COMENTARIO WHERE id_comentario = ? AND id_usuario_autor = ?";

    private static final String SQL_COUNT_BY_MAZO = "SELECT COUNT(*) FROM COMENTARIO WHERE id_mazo_destino = ?";
    private static final String SQL_DELETE_ADMIN = "DELETE FROM COMENTARIO WHERE id_comentario = ?";

    // ==========================================
    // 2. MÉTODOS CRUD
    // ==========================================
    /**
     * Inserta un nuevo comentario en la base de datos. Utiliza el objeto
     * Usuario anidado en el Comentario para obtener el ID del autor.
     *
     * * @param c El objeto Comentario poblado con los datos del autor, mazo y
     * contenido.
     * @return true si el comentario se insertó correctamente.
     * @throws SQLException Si hay un error de integridad (ej: autor o mazo
     * inexistente).
     */
    public boolean crearComentario(Comentario c) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_INSERT);
            // Obtenemos el ID navegando por el objeto Usuario anidado
            ps.setInt(1, c.getUsuarioAutor().getIdUsuario());
            ps.setInt(2, c.getIdMazoDestino());
            ps.setString(3, c.getContenido());

            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar comentario del usuario " + c.getUsuarioAutor().getIdUsuario() + " en mazo " + c.getIdMazoDestino() + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Recupera la lista de comentarios de un mazo, incluyendo el nombre de los
     * autores. Realiza un INNER JOIN con la tabla USUARIO para evitar consultas
     * adicionales en la vista. Los avatares no se cargan por rendimiento; se
     * delegan al AvatarServlet.
     *
     * * @param idMazo ID del mazo del que se quieren leer los comentarios.
     * @return ArrayList de comentarios ordenados del más reciente al más
     * antiguo.
     * @throws SQLException Si ocurre un error en la consulta JOIN.
     */
    public ArrayList<Comentario> obtenerComentariosPorMazo(int idMazo) throws SQLException {
        ArrayList<Comentario> lista = new ArrayList<>();
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_BY_MAZO);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            while (rs.next()) {
                Comentario c = new Comentario();
                c.setIdComentario(rs.getInt("id_comentario"));
                c.setIdMazoDestino(rs.getInt("id_mazo_destino"));
                c.setContenido(rs.getString("contenido"));
                c.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                c.setFechaEdicion(rs.getTimestamp("fecha_edicion"));

                // 🌟 LÓGICA DE COMPOSICIÓN 🌟
                // Creamos el usuario, le asignamos sus datos y lo guardamos en el comentario
                Usuario autor = new Usuario();
                autor.setIdUsuario(rs.getInt("id_usuario_autor"));
                autor.setApodoUsuario(rs.getString("apodo_usuario"));
                // No asignamos el avatar, se queda a null por defecto. El JSP llamará a AvatarServlet.

                c.setUsuarioAutor(autor);

                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener comentarios del mazo ID " + idMazo + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return lista;
    }

    /**
     * Modifica el texto de un comentario existente. Por seguridad, requiere que
     * el ID del usuario coincida con el autor original.
     *
     * * @param idComentario ID del comentario a modificar.
     * @param idUsuario ID del usuario que solicita la edición (comprobación de
     * autoría).
     * @param nuevoContenido El nuevo texto del comentario.
     * @return true si la edición fue permitida y exitosa.
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    public boolean editarComentario(int idComentario, int idUsuario, String nuevoContenido) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_UPDATE);
            ps.setString(1, nuevoContenido);
            ps.setInt(2, idComentario);
            ps.setInt(3, idUsuario); // Seguridad: solo el dueño puede editar

            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al editar el comentario ID " + idComentario + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Elimina un comentario solicitado por su propio autor. Verifica que el ID
     * del solicitante coincida con el ID del autor guardado en la base de
     * datos.
     *
     * * @param idComentario ID del comentario a borrar.
     * @param idUsuario ID del usuario que solicita el borrado.
     * @return true si el borrado fue permitido y exitoso.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    public boolean borrarComentario(int idComentario, int idUsuario) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE);
            ps.setInt(1, idComentario);
            ps.setInt(2, idUsuario); // Seguridad: solo el dueño puede borrar

            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al borrar el comentario ID " + idComentario + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Cuenta el número total de comentarios asociados a un mazo específico.
     * Utilizado para mostrar el contador de actividad en el catálogo o listas.
     *
     * * @param idMazo El ID del mazo a consultar.
     * @return El número total de comentarios del mazo.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public int contarComentariosPorMazo(int idMazo) throws SQLException {
        int total = 0;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_COUNT_BY_MAZO);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar comentarios del mazo ID " + idMazo + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return total;
    }

    /**
     * Elimina un comentario de forma forzada sin verificar la autoría. Este
     * método está diseñado para ser usado exclusivamente por administradores o
     * moderadores en labores de gestión de contenido tóxico o inapropiado.
     *
     * * @param idComentario El ID del comentario que se desea eliminar.
     * @return true si el comentario fue eliminado exitosamente.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    public boolean borrarComentarioPorModerador(int idComentario) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE_ADMIN);
            ps.setInt(1, idComentario);

            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error (Admin) al borrar el comentario ID " + idComentario + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Método auxiliar para cerrar de forma segura los recursos de JDBC.
     * Intercepta posibles excepciones de cierre para garantizar que la conexión
     * siempre sea devuelta al ConnectionPool, previniendo fugas de memoria y
     * caídas del servidor.
     *
     * * @param con La conexión a la base de datos a liberar.
     * @param ps El PreparedStatement a cerrar.
     * @param rs El ResultSet a cerrar.
     */
    private void cerrarRecursos(Connection con, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet en ComentarioDAO: " + e.getMessage());
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en ComentarioDAO: " + e.getMessage());
            }
        }
        // Liberamos la conexión SIEMPRE, de forma independiente
        if (con != null) {
            ConnectionPool.getInstance().freeConnection(con);
        }
    }
}
