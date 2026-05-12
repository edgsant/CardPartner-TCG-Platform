package datos;

import java.sql.*;
import modelo.Valoracion;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Valoracion. Gestiona de forma
 * exclusiva el sistema de puntuación (estrellas/notas) de los mazos. Delega los
 * cálculos matemáticos (medias y recuentos) directamente al motor de la base de
 * datos para maximizar el rendimiento.
 */
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
     * Registra la puntuación otorgada por un usuario a un mazo específico.
     * Utiliza una estrategia UPSERT (ON DUPLICATE KEY UPDATE): si es la primera
     * vez que el usuario vota, se inserta el registro; si ya había votado, se
     * actualiza su nota anterior.
     *
     * * @param v Objeto Valoracion que contiene el ID del usuario, el ID del
     * mazo y la nota.
     * @return true si la operación afectó a la base de datos exitosamente.
     * @throws SQLException Si ocurre un error de acceso a datos o de integridad
     * referencial.
     */
    public boolean guardarValoracion(Valoracion v) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
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
            cerrarRecursos(pool, con, ps, null, "guardarValoracion");
        }
        return exito;
    }

    /**
     * Calcula la puntuación media global de un mazo basándose en todos los
     * votos recibidos. Realiza el cálculo matemático (AVG) directamente en
     * MariaDB por eficiencia.
     *
     * * @param idMazo El ID numérico del mazo a consultar.
     * @return La nota media (ej. 4.5). Devuelve 0.0 si el mazo aún no tiene
     * valoraciones.
     * @throws SQLException Si ocurre un error al realizar el cálculo en la base
     * de datos.
     */
    public double obtenerMediaPorMazo(int idMazo) throws SQLException {
        double media = 0.0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
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
            cerrarRecursos(pool, con, ps, rs, "obtenerMediaPorMazo");
        }
        return media;
    }

    /**
     * Obtiene el número total de usuarios que han valorado un mazo específico.
     * Útil para mostrar en la interfaz textos como "4.5 estrellas (basado en 23
     * reseñas)".
     *
     * * @param idMazo El ID numérico del mazo.
     * @return El total de votos emitidos para ese mazo.
     * @throws SQLException Si ocurre un error al realizar el conteo.
     */
    public int contarVotosPorMazo(int idMazo) throws SQLException {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
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
            cerrarRecursos(pool, con, ps, rs, "contarVotosPorMazo");
        }
        return total;
    }

    /**
     * Recupera la puntuación exacta que un usuario específico le dio a un mazo
     * en concreto. Fundamental para pintar la interfaz del usuario y pre-marcar
     * las estrellas que ya había elegido.
     *
     * * @param idMazo El ID del mazo consultado.
     * @param idUsuario El ID del usuario que está navegando la página.
     * @return El objeto Valoracion poblado, o null si el usuario no ha valorado
     * este mazo nunca.
     * @throws SQLException Si ocurre un error de consulta.
     */
    public Valoracion obtenerValoracionUsuario(int idMazo, int idUsuario) throws SQLException {
        Valoracion v = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
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
            cerrarRecursos(pool, con, ps, rs, "obtenerValoracionUsuario");
        }
        return v;
    }

    /**
     * Borra el voto de un usuario sobre un mazo, permitiéndole retirar su
     * valoración.
     *
     * * @param idMazo El ID del mazo del que se quiere retirar el voto.
     * @param idUsuario El ID del usuario que solicita la eliminación.
     * @return true si la valoración existía y fue eliminada con éxito.
     * @throws SQLException Si ocurre un error durante el borrado en la base de
     * datos.
     */
    public boolean eliminarValoracion(int idMazo, int idUsuario) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
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
            cerrarRecursos(pool, con, ps, null, "eliminarValoracion");
        }
        return exito;
    }

    // ==========================================
    // 3. MÉTODOS AUXILIARES
    // ==========================================
    /**
     * Centraliza y asegura el cierre de los recursos JDBC (ResultSet,
     * PreparedStatement y Connection). Utiliza bloques try-catch individuales
     * para evitar fugas de memoria y emplea la instrospección de Java para
     * registrar dinámicamente la clase y el método en caso de error.
     *
     * * @param pool La instancia del ConnectionPool gestor.
     * @param con La conexión a liberar.
     * @param ps El PreparedStatement a cerrar.
     * @param rs El ResultSet a cerrar.
     * @param nombreMetodo Nombre del método invocador (para facilitar la
     * depuración).
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
