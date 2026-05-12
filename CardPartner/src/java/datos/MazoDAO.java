package datos;

import java.sql.*;
import java.util.ArrayList;
import modelo.Mazo;
import modelo.ContieneCarta;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Mazo. Actúa como el motor
 * principal de la aplicación, gestionando la creación, edición, filtrado,
 * paginación y eliminación de los mazos de cartas, así como su relación con los
 * usuarios (creadores y favoritos) y las cartas que los componen.
 */
public class MazoDAO {

    // ==========================================
    // 1. CONSTANTES SQL
    // ==========================================
    private static final String SQL_INSERT_MAZO = "INSERT INTO MAZO (nombre_mazo, descripcion, es_publico, es_oficial, id_usuario_creador, id_carta_lider, winrate) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_CARTA_EN_MAZO = "INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_POR_USUARIO = "SELECT m.*, c.nombre_carta, c.imagen, c.color, u.apodo_usuario FROM MAZO m INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta INNER JOIN USUARIO u ON m.id_usuario_creador = u.id_usuario WHERE m.id_usuario_creador = ? ORDER BY m.fecha_creacion DESC";
    private static final String SQL_DELETE_MAZO = "DELETE FROM MAZO WHERE id_mazo = ? AND id_usuario_creador = ?";
    private static final String SQL_SELECT_MAZO_POR_ID = "SELECT m.*, c.nombre_carta, c.imagen, c.color, u.apodo_usuario FROM MAZO m INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta INNER JOIN USUARIO u ON m.id_usuario_creador = u.id_usuario WHERE m.id_mazo = ?";
    private static final String SQL_SELECT_CARTAS_DE_MAZO = "SELECT cc.cantidad, c.* FROM CONTIENE_CARTA cc INNER JOIN CARTA c ON cc.id_carta = c.id_carta WHERE cc.id_mazo = ?";
    private static final String SQL_SELECT_FAVORITOS = "SELECT m.*, c.nombre_carta, c.imagen, c.color, u.apodo_usuario FROM MAZO m INNER JOIN GUARDA_MAZO gm ON m.id_mazo = gm.id_mazo INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta INNER JOIN USUARIO u ON m.id_usuario_creador = u.id_usuario WHERE gm.id_usuario = ?";
    private static final String SQL_INSERT_FAVORITO = "INSERT INTO GUARDA_MAZO (id_usuario, id_mazo) VALUES (?, ?)";
    private static final String SQL_DELETE_FAVORITO = "DELETE FROM GUARDA_MAZO WHERE id_usuario = ? AND id_mazo = ?";
    private static final String SQL_UPDATE_MAZO = "UPDATE MAZO SET nombre_mazo=?, descripcion=?, es_publico=?, es_oficial=?, id_carta_lider=?, winrate=? WHERE id_mazo=?";
    private static final String SQL_DELETE_CARTAS_DE_MAZO = "DELETE FROM CONTIENE_CARTA WHERE id_mazo=?";

    // ==========================================
    // 2. MÉTODOS DE CREACIÓN Y GESTIÓN
    // ==========================================
    /**
     * Inserta los metadatos base de un nuevo mazo en la base de datos.
     *
     * * @param mazo Objeto Mazo con todos los datos necesarios (nombre,
     * descripción, líder, etc.).
     * @return El ID numérico autogenerado del mazo si la inserción fue exitosa;
     * 0 si falló.
     * @throws SQLException Si ocurre un error de acceso a la base de datos o
     * sintaxis SQL.
     */
    public int crearMazo(Mazo mazo) throws SQLException {
        int idMazoGenerado = 0;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_INSERT_MAZO, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, mazo.getNombreMazo());
            ps.setString(2, mazo.getDescripcion());
            ps.setBoolean(3, mazo.isEsPublico());
            ps.setBoolean(4, mazo.isEsOficial());
            ps.setInt(5, mazo.getIdUsuarioCreador());
            ps.setString(6, mazo.getIdCartaLider());
            ps.setFloat(7, mazo.getWinrate()); // Es el séptimo "?" en el INSERT
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idMazoGenerado = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al crear mazo: " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return idMazoGenerado;
    }

    /**
     * Realiza una inserción masiva (Batch) de todas las cartas que componen un
     * mazo en la tabla intermedia. Mejora significativamente el rendimiento al
     * enviar todas las inserciones en un único viaje al servidor.
     *
     * * @param idMazo ID numérico del mazo al que pertenecen las cartas.
     * @param listaCartas ArrayList de objetos ContieneCarta (con el ID de la
     * carta y su cantidad).
     * @return true si el lote completo de cartas se insertó correctamente.
     * @throws SQLException Si ocurre un error de base de datos durante el
     * procesamiento por lotes.
     */
    public boolean añadirCartasAMazo(int idMazo, ArrayList<ContieneCarta> listaCartas) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_INSERT_CARTA_EN_MAZO);
            for (ContieneCarta cc : listaCartas) {
                ps.setInt(1, idMazo);
                ps.setString(2, cc.getIdCarta());
                ps.setInt(3, cc.getCantidad());
                ps.addBatch();
            }
            int[] resultados = ps.executeBatch();
            if (resultados.length > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al annadir cartas al mazo (id = " + idMazo + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    // ==========================================
    // 3. MÉTODOS DE FILTRADO Y CONSULTA
    // ==========================================
    /**
     * Realiza una búsqueda avanzada y paginada en el catálogo de mazos públicos
     * de la comunidad. Construye dinámicamente la consulta SQL dependiendo de
     * los filtros proporcionados.
     *
     * * @param esOficial Filtra por mazos marcados como oficiales de torneos
     * (opcional, puede ser null).
     * @param minWinrate Filtra mazos con un porcentaje de victoria mayor o
     * igual al indicado (opcional).
     * @param nombreBuscado Búsqueda parcial por el nombre del mazo (opcional).
     * @param idCartaLider Filtra mazos liderados por el ID de una carta
     * específica (opcional).
     * @param colorLider Filtra mazos cuyo líder pertenezca a un color
     * específico (opcional).
     * @param limite Número máximo de resultados a devolver (tamaño de la
     * página).
     * @param offset Número de resultados a saltar desde el inicio
     * (desplazamiento).
     * @return Lista de mazos públicos que cumplen los criterios, con sus
     * líderes y creadores anidados.
     * @throws SQLException Si ocurre un error durante la ejecución dinámica de
     * SQL.
     */
    public ArrayList<Mazo> filtrarMazosPublicos(Boolean esOficial, Float minWinrate, String nombreBuscado, String idCartaLider, String colorLider, int limite, int offset) throws SQLException {
        ArrayList<Mazo> listaMazos = new ArrayList<>();
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("SELECT m.*, c.nombre_carta, c.imagen, c.color, u.apodo_usuario FROM MAZO m INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta INNER JOIN USUARIO u ON m.id_usuario_creador = u.id_usuario WHERE m.es_publico = 1");

        if (esOficial != null) {
            sql.append(" AND m.es_oficial = ?");
        }
        if (minWinrate != null) {
            sql.append(" AND m.winrate >= ?");
        }
        if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) {
            sql.append(" AND m.nombre_mazo LIKE ?");
        }
        if (idCartaLider != null && !idCartaLider.trim().isEmpty()) {
            sql.append(" AND m.id_carta_lider = ?");
        }
        if (colorLider != null && !colorLider.trim().isEmpty()) {
            sql.append(" AND c.color LIKE ?");
        }

        sql.append(" ORDER BY m.fecha_creacion DESC LIMIT ? OFFSET ?");

        try {
            ps = con.prepareStatement(sql.toString());
            int paramIndex = 1;
            if (esOficial != null) {
                ps.setBoolean(paramIndex++, esOficial);
            }
            if (minWinrate != null) {
                ps.setFloat(paramIndex++, minWinrate);
            }
            if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + nombreBuscado.trim() + "%");
            }
            if (idCartaLider != null && !idCartaLider.trim().isEmpty()) {
                ps.setString(paramIndex++, idCartaLider);
            }
            if (colorLider != null && !colorLider.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + colorLider.trim() + "%");
            }

            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex, offset);
            rs = ps.executeQuery();

            while (rs.next()) {
                listaMazos.add(mapearMazoCompleto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar mazos (esOficial = " + esOficial + ", minWinrate = " + minWinrate + ", nombreBuscado = " + nombreBuscado + ", idCartaLider = " + idCartaLider + ", colorLider = " + colorLider + ", limite = " + limite + ", offset = " + offset + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return listaMazos;
    }

    /**
     * Cuenta el número total de mazos públicos que coinciden con los filtros
     * especificados. Es fundamental para calcular el número exacto de botones
     * de paginación en el frontend.
     *
     * * @param esOficial Filtra por mazos oficiales (opcional).
     * @param minWinrate Filtra por winrate mínimo (opcional).
     * @param nombreBuscado Búsqueda parcial por nombre (opcional).
     * @param idCartaLider Filtro por ID del líder (opcional).
     * @param colorLider Filtro por color del líder (opcional).
     * @return El total numérico de mazos que coinciden con la búsqueda.
     * @throws SQLException Si ocurre un error durante el recuento en la base de
     * datos.
     */
    public int contarTotalMazosFiltrados(Boolean esOficial, Float minWinrate, String nombreBuscado, String idCartaLider, String colorLider) throws SQLException {
        int total = 0;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Misma base SQL que el filtro, pero con COUNT(*)
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM MAZO m INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta WHERE m.es_publico = 1");

        if (esOficial != null) {
            sql.append(" AND m.es_oficial = ?");
        }
        if (minWinrate != null) {
            sql.append(" AND m.winrate >= ?");
        }
        if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) {
            sql.append(" AND m.nombre_mazo LIKE ?");
        }
        if (idCartaLider != null && !idCartaLider.trim().isEmpty()) {
            sql.append(" AND m.id_carta_lider = ?");
        }
        if (colorLider != null && !colorLider.trim().isEmpty()) {
            sql.append(" AND c.color LIKE ?");
        }

        try {
            ps = con.prepareStatement(sql.toString());
            int paramIndex = 1;
            if (esOficial != null) {
                ps.setBoolean(paramIndex++, esOficial);
            }
            if (minWinrate != null) {
                ps.setFloat(paramIndex++, minWinrate);
            }
            if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + nombreBuscado.trim() + "%");
            }
            if (idCartaLider != null && !idCartaLider.trim().isEmpty()) {
                ps.setString(paramIndex++, idCartaLider);
            }
            if (colorLider != null && !colorLider.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + colorLider.trim() + "%");
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1); // Recogemos el número de mazos
            }
        } catch (SQLException e) {
            System.err.println("Error al contar mazos filtrados: " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return total;
    }

    // ==========================================
    // 4. MÉTODOS DE FAVORITOS (SOLUCIÓN ERRORES SERVLET)
    // ==========================================
    /**
     * Guarda un mazo en la biblioteca personal (favoritos) de un usuario.
     *
     * * @param idUsuario ID del usuario que desea guardar el mazo.
     * @param idMazo ID del mazo que va a ser guardado.
     * @return true si se añadió correctamente a favoritos.
     * @throws SQLException Si ocurre un error en la base de datos o si la
     * relación ya existe.
     */
    public boolean añadirMazoFavorito(int idUsuario, int idMazo) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_INSERT_FAVORITO);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMazo);
            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al annadir mazo favorito (idUsuario = " + idUsuario + ", idMazo = " + idMazo + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Elimina un mazo de la biblioteca personal (favoritos) de un usuario.
     *
     * * @param idUsuario ID del usuario que desea eliminar el mazo de sus
     * guardados.
     * @param idMazo ID del mazo a eliminar.
     * @return true si se eliminó de favoritos con éxito.
     * @throws SQLException Si ocurre un error en la base de datos durante la
     * eliminación.
     */
    public boolean quitarMazoFavorito(int idUsuario, int idMazo) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE_FAVORITO);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMazo);
            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al quitar mazo favorito (idUsuario = " + idUsuario + ", idMazo = " + idMazo + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Recupera todos los mazos que un usuario tiene guardados en su lista de
     * favoritos.
     *
     * * @param idUsuario El ID del usuario a consultar.
     * @return Lista de mazos favoritos con la información completa de sus
     * líderes y creadores originales.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    public ArrayList<Mazo> obtenerMazosGuardados(int idUsuario) throws SQLException {
        ArrayList<Mazo> lista = new ArrayList<>();
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_FAVORITOS);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearMazoCompleto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mazos guardados (idUsuario = " + idUsuario + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return lista;
    }

    // ==========================================
    // 5. ACTUALIZACIÓN Y BORRADO
    // ==========================================
    /**
     * Actualiza los metadatos principales de un mazo existente (nombre,
     * descripción, estado, etc.).
     *
     * * @param m Objeto Mazo con los datos actualizados y su ID original
     * intacto.
     * @return true si la actualización modificó el registro con éxito.
     * @throws SQLException Si ocurre un error de actualización en la base de
     * datos.
     */
    public boolean actualizarMazo(Mazo m) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_UPDATE_MAZO);
            ps.setString(1, m.getNombreMazo());
            ps.setString(2, m.getDescripcion());
            ps.setBoolean(3, m.isEsPublico());
            ps.setBoolean(4, m.isEsOficial()); // NUEVO
            ps.setString(5, m.getIdCartaLider());
            ps.setFloat(6, m.getWinrate()); // El winrate
            ps.setInt(7, m.getIdMazo()); // El ID para el WHERE
            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener actualizar mazo (idMazo = " + m.getIdMazo() + "): " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Elimina todas las cartas asociadas a un mazo específico de la tabla
     * intermedia. Útil cuando se va a editar la composición de un mazo (se
     * borran las viejas y se insertan las nuevas).
     *
     * * @param idMazo El ID del mazo cuyas cartas van a ser eliminadas.
     * @return true si el borrado se ejecutó sin errores.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    public boolean borrarCartasDeMazo(int idMazo) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE_CARTAS_DE_MAZO);
            ps.setInt(1, idMazo);
            ps.executeUpdate();
            exito = true;
        } catch (SQLException e) {
            System.err.println("Error al borrar cartas de mazo (idMazo = " + idMazo + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Elimina de forma permanente un mazo completo de la base de datos. Incluye
     * una comprobación de seguridad para garantizar que solo el creador pueda
     * borrarlo.
     *
     * * @param idMazo ID del mazo a eliminar.
     * @param idUsuarioCreador ID del usuario que solicita el borrado (debe
     * coincidir con el creador original).
     * @return true si el mazo fue eliminado exitosamente.
     * @throws SQLException Si ocurre un error durante la eliminación en la base
     * de datos.
     */
    public boolean borrarMazo(int idMazo, int idUsuarioCreador) throws SQLException {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE_MAZO);
            ps.setInt(1, idMazo);
            ps.setInt(2, idUsuarioCreador);
            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al borrar mazo (idMazo = " + idMazo + ", idUsuarioCreador = " + idUsuarioCreador + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Recupera toda la información (metadatos, líder y creador) de un mazo
     * específico mediante su ID. Ideal para cargar la cabecera en la vista de
     * detalles de un mazo.
     *
     * * @param idMazo El ID numérico del mazo buscado.
     * @return Objeto Mazo completamente ensamblado, o null si no existe.
     * @throws SQLException Si ocurre un error de consulta.
     */
    public Mazo obtenerMazoPorId(int idMazo) throws SQLException {
        Mazo mazo = null;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_MAZO_POR_ID);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            if (rs.next()) {
                mazo = mapearMazoCompleto(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mazo por ID (idMazo = " + idMazo + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return mazo;
    }

    /**
     * Recupera la estructura completa (lista de cartas y sus cantidades) que
     * componen un mazo. Realiza un JOIN con la tabla CARTA para obtener
     * atributos gráficos (nombre, imagen, color).
     *
     * * @param idMazo ID del mazo a inspeccionar.
     * @return ArrayList de objetos ContieneCarta, cada uno con su objeto Carta
     * anidado.
     * @throws SQLException Si ocurre un error de consulta.
     */
    public ArrayList<ContieneCarta> obtenerCartasDeUnMazo(int idMazo) throws SQLException {
        ArrayList<ContieneCarta> lista = new ArrayList<>();
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_CARTAS_DE_MAZO);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            while (rs.next()) {
                ContieneCarta cc = new ContieneCarta();
                cc.setIdMazo(idMazo);
                cc.setIdCarta(rs.getString("id_carta"));
                cc.setCantidad(rs.getInt("cantidad"));
                modelo.Carta carta = new modelo.Carta();
                carta.setIdCarta(rs.getString("id_carta"));
                carta.setNombreCarta(rs.getString("nombre_carta"));
                carta.setImagen(rs.getString("imagen"));
                carta.setColor(rs.getString("color"));
                cc.setCarta(carta);
                lista.add(cc);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cartas de un mazo (idMazo = " + idMazo + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return lista;
    }

    /**
     * Recupera todos los mazos creados originalmente por un usuario específico.
     *
     * * @param idUsuario El ID del usuario creador.
     * @return Lista de mazos ensamblados ordenados del más reciente al más
     * antiguo.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public ArrayList<Mazo> obtenerMazosPorUsuario(int idUsuario) throws SQLException {
        ArrayList<Mazo> lista = new ArrayList<>();
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_POR_USUARIO);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearMazoCompleto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mazos por Usuario (idUsuario = " + idUsuario + "): " + e.getMessage());
            throw e; // <-- Lanzar al Servlet
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return lista;
    }

    // ==========================================
    //  METODOS AUXILIARES
    // ==========================================
    /**
     * Método auxiliar que transforma la fila actual de un ResultSet en un
     * objeto Mazo básico. Solo extrae los metadatos propios de la tabla MAZO.
     *
     * * @param rs El ResultSet posicionado en la fila a extraer.
     * @return Objeto Mazo con sus propiedades primitivas asignadas.
     * @throws SQLException Si hay un error al leer las columnas.
     */
    private Mazo mapearMazo(ResultSet rs) throws SQLException {
        Mazo m = new Mazo();
        m.setIdMazo(rs.getInt("id_mazo"));
        m.setNombreMazo(rs.getString("nombre_mazo"));
        m.setDescripcion(rs.getString("descripcion"));
        m.setEsPublico(rs.getBoolean("es_publico"));
        m.setEsOficial(rs.getBoolean("es_oficial"));
        m.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        m.setWinrate(rs.getFloat("winrate"));
        m.setIdUsuarioCreador(rs.getInt("id_usuario_creador"));
        m.setIdCartaLider(rs.getString("id_carta_lider"));
        return m;
    }

    /**
     * Método auxiliar avanzado que mapea un Mazo, ensamblando también los
     * objetos Carta (líder) y Usuario (creador) a partir de los JOINs de la
     * consulta SQL. Aplica el principio DRY (Don't Repeat Yourself) para la
     * creación de objetos complejos.
     *
     * * @param rs El ResultSet que contiene los datos del mazo, el líder y el
     * creador.
     * @return Objeto Mazo con la "Trifuerza" (Mazo + Líder + Creador) lista
     * para las vistas JSP.
     * @throws SQLException Si alguna columna del JOIN no está presente o hay
     * error de lectura.
     */
    private Mazo mapearMazoCompleto(ResultSet rs) throws SQLException {
        Mazo m = mapearMazo(rs); // Mapea los datos básicos del mazo

        // Mapea la carta Líder
        modelo.Carta lider = new modelo.Carta();
        lider.setIdCarta(rs.getString("id_carta_lider"));
        lider.setNombreCarta(rs.getString("nombre_carta"));
        lider.setImagen(rs.getString("imagen"));
        lider.setColor(rs.getString("color"));
        m.setCartaLider(lider);

        // Mapea el Usuario Creador
        modelo.Usuario creador = new modelo.Usuario();
        creador.setIdUsuario(rs.getInt("id_usuario_creador"));
        creador.setApodoUsuario(rs.getString("apodo_usuario"));
        m.setUsuarioCreador(creador);

        return m;
    }

    /**
     * Centraliza el cierre de los recursos de la base de datos (ResultSet,
     * PreparedStatement y Connection). Previene fugas de memoria y devuelve la
     * conexión de manera segura al ConnectionPool.
     *
     * * @param con Conexión a liberar.
     * @param ps PreparedStatement a cerrar.
     * @param rs ResultSet a cerrar.
     */
    private void cerrarRecursos(Connection con, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar RS: " + e.getMessage());
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar PS: " + e.getMessage());
            }
        }
        if (con != null) {
            ConnectionPool.getInstance().freeConnection(con);
        }
    }
}
