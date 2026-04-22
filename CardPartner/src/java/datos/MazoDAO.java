package datos;

import java.sql.*;
import java.util.ArrayList;
import modelo.Mazo;
import modelo.ContieneCarta;

public class MazoDAO {

    // ==========================================
    // 1. CONSTANTES SQL
    // ==========================================
    private static final String SQL_INSERT_MAZO = "INSERT INTO MAZO (nombre_mazo, descripcion, es_publico, es_oficial, id_usuario_creador, id_carta_lider) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_CARTA_EN_MAZO = "INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_POR_USUARIO = "SELECT m.*, c.nombre_carta, c.imagen, c.color FROM MAZO m INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta WHERE m.id_usuario_creador = ? ORDER BY m.fecha_creacion DESC";
    private static final String SQL_DELETE_MAZO = "DELETE FROM MAZO WHERE id_mazo = ? AND id_usuario_creador = ?";
    private static final String SQL_SELECT_MAZO_POR_ID = "SELECT * FROM MAZO WHERE id_mazo = ?";
    private static final String SQL_SELECT_CARTAS_DE_MAZO = "SELECT cc.cantidad, c.* FROM CONTIENE_CARTA cc INNER JOIN CARTA c ON cc.id_carta = c.id_carta WHERE cc.id_mazo = ?";
    private static final String SQL_SELECT_FAVORITOS = "SELECT m.*, c.nombre_carta, c.imagen, c.color FROM MAZO m INNER JOIN GUARDA_MAZO gm ON m.id_mazo = gm.id_mazo INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta WHERE gm.id_usuario = ?";
    private static final String SQL_INSERT_FAVORITO = "INSERT INTO GUARDA_MAZO (id_usuario, id_mazo) VALUES (?, ?)";
    private static final String SQL_DELETE_FAVORITO = "DELETE FROM GUARDA_MAZO WHERE id_usuario = ? AND id_mazo = ?";
    private static final String SQL_UPDATE_MAZO = "UPDATE MAZO SET nombre_mazo=?, descripcion=?, es_publico=?, id_carta_lider=? WHERE id_mazo=?";
    private static final String SQL_DELETE_CARTAS_DE_MAZO = "DELETE FROM CONTIENE_CARTA WHERE id_mazo=?";

    // ==========================================
    // 2. MÉTODOS DE CREACIÓN Y GESTIÓN
    // ==========================================

    public int crearMazo(Mazo mazo) {
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
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) idMazoGenerado = rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, rs); }
        return idMazoGenerado;
    }

    public boolean añadirCartasAMazo(int idMazo, ArrayList<ContieneCarta> listaCartas) {
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
            if (resultados.length > 0) exito = true;
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, null); }
        return exito;
    }

    // ==========================================
    // 3. MÉTODOS DE FILTRADO Y CONSULTA
    // ==========================================

    public ArrayList<Mazo> filtrarMazosPublicos(Boolean esOficial, Float minWinrate, String nombreBuscado, String idCartaLider, String colorLider, int limite, int offset) {
        ArrayList<Mazo> listaMazos = new ArrayList<>();
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("SELECT m.*, c.nombre_carta, c.imagen, c.color FROM MAZO m INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta WHERE m.es_publico = 1");

        if (esOficial != null) sql.append(" AND m.es_oficial = ?");
        if (minWinrate != null) sql.append(" AND m.winrate >= ?");
        if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) sql.append(" AND m.nombre_mazo LIKE ?");
        if (idCartaLider != null && !idCartaLider.trim().isEmpty()) sql.append(" AND m.id_carta_lider = ?");
        if (colorLider != null && !colorLider.trim().isEmpty()) sql.append(" AND c.color LIKE ?");

        sql.append(" ORDER BY m.fecha_creacion DESC LIMIT ? OFFSET ?");

        try {
            ps = con.prepareStatement(sql.toString());
            int paramIndex = 1;
            if (esOficial != null) ps.setBoolean(paramIndex++, esOficial);
            if (minWinrate != null) ps.setFloat(paramIndex++, minWinrate);
            if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) ps.setString(paramIndex++, "%" + nombreBuscado.trim() + "%");
            if (idCartaLider != null && !idCartaLider.trim().isEmpty()) ps.setString(paramIndex++, idCartaLider);
            if (colorLider != null && !colorLider.trim().isEmpty()) ps.setString(paramIndex++, "%" + colorLider.trim() + "%");

            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex, offset);
            rs = ps.executeQuery();

            while (rs.next()) {
                Mazo m = mapearMazo(rs);
                modelo.Carta lider = new modelo.Carta();
                lider.setIdCarta(rs.getString("id_carta_lider"));
                lider.setNombreCarta(rs.getString("nombre_carta"));
                lider.setImagen(rs.getString("imagen"));
                lider.setColor(rs.getString("color"));
                m.setCartaLider(lider);
                listaMazos.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, rs); }
        return listaMazos;
    }

    // ==========================================
    // 4. MÉTODOS DE FAVORITOS (SOLUCIÓN ERRORES SERVLET)
    // ==========================================

    public boolean añadirMazoFavorito(int idUsuario, int idMazo) {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_INSERT_FAVORITO);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMazo);
            if (ps.executeUpdate() > 0) exito = true;
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, null); }
        return exito;
    }

    public boolean quitarMazoFavorito(int idUsuario, int idMazo) {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE_FAVORITO);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMazo);
            if (ps.executeUpdate() > 0) exito = true;
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, null); }
        return exito;
    }

    public ArrayList<Mazo> obtenerMazosGuardados(int idUsuario) {
        ArrayList<Mazo> lista = new ArrayList<>();
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_FAVORITOS);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                Mazo m = mapearMazo(rs);
                modelo.Carta lider = new modelo.Carta();
                lider.setIdCarta(rs.getString("id_carta_lider"));
                lider.setNombreCarta(rs.getString("nombre_carta"));
                lider.setImagen(rs.getString("imagen"));
                lider.setColor(rs.getString("color"));
                m.setCartaLider(lider);
                lista.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, rs); }
        return lista;
    }

    // ==========================================
    // 5. ACTUALIZACIÓN Y BORRADO
    // ==========================================

    public boolean actualizarMazo(Mazo m) {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_UPDATE_MAZO);
            ps.setString(1, m.getNombreMazo());
            ps.setString(2, m.getDescripcion());
            ps.setBoolean(3, m.isEsPublico());
            ps.setString(4, m.getIdCartaLider());
            ps.setInt(5, m.getIdMazo());
            if (ps.executeUpdate() > 0) exito = true;
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, null); }
        return exito;
    }

    public boolean borrarCartasDeMazo(int idMazo) {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE_CARTAS_DE_MAZO);
            ps.setInt(1, idMazo);
            ps.executeUpdate();
            exito = true;
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, null); }
        return exito;
    }

    public boolean borrarMazo(int idMazo, int idUsuarioCreador) {
        boolean exito = false;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQL_DELETE_MAZO);
            ps.setInt(1, idMazo);
            ps.setInt(2, idUsuarioCreador);
            if (ps.executeUpdate() > 0) exito = true;
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, null); }
        return exito;
    }

    public Mazo obtenerMazoPorId(int idMazo) {
        Mazo mazo = null;
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_MAZO_POR_ID);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();
            if (rs.next()) mazo = mapearMazo(rs);
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, rs); }
        return mazo;
    }

    public ArrayList<ContieneCarta> obtenerCartasDeUnMazo(int idMazo) {
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
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, rs); }
        return lista;
    }

    public ArrayList<Mazo> obtenerMazosPorUsuario(int idUsuario) {
        ArrayList<Mazo> lista = new ArrayList<>();
        Connection con = ConnectionPool.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQL_SELECT_POR_USUARIO);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                Mazo m = mapearMazo(rs);
                modelo.Carta lider = new modelo.Carta();
                lider.setIdCarta(rs.getString("id_carta_lider"));
                lider.setNombreCarta(rs.getString("nombre_carta"));
                lider.setImagen(rs.getString("imagen"));
                lider.setColor(rs.getString("color"));
                m.setCartaLider(lider);
                lista.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { cerrarRecursos(con, ps, rs); }
        return lista;
    }

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

    private void cerrarRecursos(Connection con, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) ConnectionPool.getInstance().freeConnection(con);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}