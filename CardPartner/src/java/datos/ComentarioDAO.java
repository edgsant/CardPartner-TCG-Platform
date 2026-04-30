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

    // ==========================================
    // 2. MÉTODOS CRUD
    // ==========================================
    /**
     * Inserta un nuevo comentario asociado a un mazo.
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
     * Recupera todos los comentarios de un mazo específico.
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
     * Permite a un usuario editar el contenido de su comentario.
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
     * Elimina un comentario (requiere ID del usuario por seguridad).
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
     * Centraliza el cierre de recursos de forma segura (sin fugas de conexión).
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
