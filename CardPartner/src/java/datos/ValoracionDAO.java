package datos;

import java.sql.*;
import modelo.Valoracion;

public class ValoracionDAO {

    // ==========================================
    // 1. CONSTANTES SQL
    // ==========================================
    // Si la clave primaria compuesta (id_usuario, id_mazo) ya existe, actualiza la puntuación.
    private static final String SQL_UPSERT = "INSERT INTO VALORACION (id_usuario, id_mazo, puntuacion) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE puntuacion = ?";

    private static final String SQL_SELECT_MEDIA = "SELECT AVG(puntuacion) FROM VALORACION WHERE id_mazo = ?";

    private static final String SQL_COUNT_VOTOS = "SELECT COUNT(*) FROM VALORACION WHERE id_mazo = ?";

    private static final String SQL_SELECT_VOTO_USUARIO = "SELECT * FROM VALORACION WHERE id_mazo = ? AND id_usuario = ?";

    private static final String SQL_DELETE = "DELETE FROM VALORACION WHERE id_mazo = ? AND id_usuario = ?";

    // ==========================================
    // 2. MÉTODOS CRUD Y LÓGICA DE NEGOCIO
    // ==========================================
    /**
     * Guarda una nueva valoración o actualiza la existente si el usuario ya
     * había votado este mazo.
     */
    public boolean guardarValoracion(Valoracion v) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_UPSERT);
            ps.setInt(1, v.getIdUsuario());
            ps.setInt(2, v.getIdMazo());
            ps.setFloat(3, v.getPuntuacion());
            ps.setFloat(4, v.getPuntuacion()); // SOLUCIÓN: Asignamos el valor para la parte del UPDATE

            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar valoración del usuario " + v.getIdUsuario() + " en mazo " + v.getIdMazo() + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Devuelve la puntuación media (ej. 4.2) de un mazo.
     */
    public double obtenerMediaPorMazo(int idMazo) throws SQLException {
        double media = 0.0;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_MEDIA);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            if (rs.next()) {
                media = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la media del mazo " + idMazo + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return media;
    }

    /**
     * Devuelve el total de personas que han votado un mazo.
     */
    public int contarVotosPorMazo(int idMazo) throws SQLException {
        int total = 0;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_COUNT_VOTOS);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar votos del mazo " + idMazo + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return total;
    }

    /**
     * Obtiene la valoración específica de un usuario para pre-marcarle las
     * estrellas. Devuelve null si el usuario no ha votado.
     */
    public Valoracion obtenerValoracionUsuario(int idMazo, int idUsuario) throws SQLException {
        Valoracion v = null;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_VOTO_USUARIO);
            ps.setInt(1, idMazo);
            ps.setInt(2, idUsuario);
            rs = ps.executeQuery();
            if (rs.next()) {
                v = new Valoracion();
                v.setIdUsuario(rs.getInt("id_usuario"));
                v.setIdMazo(rs.getInt("id_mazo"));
                v.setPuntuacion(rs.getFloat("puntuacion"));
                v.setFechaValoracion(rs.getTimestamp("fecha_valoracion"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener valoración del usuario " + idUsuario + " en mazo " + idMazo + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return v;
    }

    /**
     * Elimina la valoración de un usuario.
     */
    public boolean eliminarValoracion(int idMazo, int idUsuario) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE);
            ps.setInt(1, idMazo);
            ps.setInt(2, idUsuario);
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar valoración del usuario " + idUsuario + " en mazo " + idMazo + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    // ==========================================
    // 3. MÉTODOS AUXILIARES
    // ==========================================
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
