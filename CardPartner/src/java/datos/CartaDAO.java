package datos;

import java.sql.*;
import java.util.ArrayList;
import modelo.Carta;

public class CartaDAO {

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM CARTA WHERE id_carta = ?";

    /**
     * Busca una carta específica por su código identificador.
     *
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public Carta obtenerCartaPorId(String idCarta) throws SQLException {
        Carta cartaEncontrada = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_BY_ID);
            ps.setString(1, idCarta);
            rs = ps.executeQuery();

            if (rs.next()) {
                cartaEncontrada = mapearCarta(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la carta ID " + idCarta + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, rs, "obtenerCartaPorId");
        }
        return cartaEncontrada;
    }

    /**
     * Busca cartas aplicando filtros MÚLTIPLES (Arrays).
     */
    public ArrayList<Carta> filtrarCartas(String nombre, String[] colores, String[] tipos, String[] atributos, int limite, int offset) throws SQLException {
        ArrayList<Carta> listaCartas = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("SELECT * FROM CARTA WHERE 1=1");
        construirFiltrosDinamicos(sql, nombre, colores, tipos, atributos);
        sql.append(" ORDER BY id_carta ASC LIMIT ? OFFSET ?");

        try {
            ps = con.prepareStatement(sql.toString());
            int paramIndex = asignarParametrosDinamicos(ps, nombre, colores, tipos, atributos, 1);
            
            // Asignamos límite y offset al final
            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex, offset);

            rs = ps.executeQuery();
            while (rs.next()) {
                listaCartas.add(mapearCarta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar cartas múltiples: " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, rs, "filtrarCartas");
        }
        return listaCartas;
    }

    /**
     * Cuenta el total de cartas que cumplen con los filtros MÚLTIPLES.
     */
    public int contarTotalCartas(String nombre, String[] colores, String[] tipos, String[] atributos) throws SQLException {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM CARTA WHERE 1=1");
        construirFiltrosDinamicos(sql, nombre, colores, tipos, atributos);

        try {
            ps = con.prepareStatement(sql.toString());
            asignarParametrosDinamicos(ps, nombre, colores, tipos, atributos, 1);

            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar cartas múltiples: " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, rs, "contarTotalCartas");
        }
        return total;
    }

    // --- MÉTODOS AUXILIARES PARA NO REPETIR CÓDIGO ---

    private void construirFiltrosDinamicos(StringBuilder sql, String nombre, String[] colores, String[] tipos, String[] atributos) {
        // NUEVO: Buscador Global (Nombre OR Atributo OR Tipo)
        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND (nombre_carta LIKE ? OR atributo_combate LIKE ? OR tipo LIKE ?)");
        }
        if (colores != null && colores.length > 0) {
            sql.append(" AND (");
            for (int i = 0; i < colores.length; i++) {
                sql.append("FIND_IN_SET(?, color) > 0");
                if (i < colores.length - 1) sql.append(" OR ");
            }
            sql.append(")");
        }
        if (tipos != null && tipos.length > 0) {
            sql.append(" AND tipo IN (");
            for (int i = 0; i < tipos.length; i++) {
                sql.append("?");
                if (i < tipos.length - 1) sql.append(", ");
            }
            sql.append(")");
        }
        if (atributos != null && atributos.length > 0) {
            sql.append(" AND atributo_combate IN (");
            for (int i = 0; i < atributos.length; i++) {
                sql.append("?");
                if (i < atributos.length - 1) sql.append(", ");
            }
            sql.append(")");
        }
    }

    private int asignarParametrosDinamicos(PreparedStatement ps, String nombre, String[] colores, String[] tipos, String[] atributos, int startIndex) throws SQLException {
        if (nombre != null && !nombre.trim().isEmpty()) {
            String busqueda = "%" + nombre.trim() + "%";
            ps.setString(startIndex++, busqueda); // Para nombre_carta
            ps.setString(startIndex++, busqueda); // Para atributo_combate
            ps.setString(startIndex++, busqueda); // Para tipo
        }
        if (colores != null) {
            for (String c : colores) ps.setString(startIndex++, c);
        }
        if (tipos != null) {
            for (String t : tipos) ps.setString(startIndex++, t);
        }
        if (atributos != null) {
            for (String a : atributos) ps.setString(startIndex++, a);
        }
        return startIndex;
    }

    // --- MÉTODOS AUXILIARES ---
    /**
     * Centraliza el mapeo de ResultSet a objeto Carta.
     */
    private Carta mapearCarta(ResultSet rs) throws SQLException {
        Carta c = new Carta();
        c.setIdCarta(rs.getString("id_carta"));
        c.setNombreCarta(rs.getString("nombre_carta"));
        c.setColor(rs.getString("color"));
        c.setTipo(rs.getString("tipo"));
        c.setArquetipo(rs.getString("arquetipo"));
        c.setAtributoCarta(rs.getString("atributo_combate"));
        c.setCoste(rs.getInt("coste"));
        c.setPoder(rs.getInt("poder"));
        c.setImagen(rs.getString("imagen"));
        c.setPrecio(rs.getDouble("precio_mercado"));
        return c;
    }

    /**
     * Centraliza el cierre de recursos de forma segura (sin fugas de conexión).
     */
    private void cerrarRecursos(ConnectionPool pool, Connection con, PreparedStatement ps, ResultSet rs, String nombreMetodo) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet en " + nombreMetodo + ": " + e.getMessage());
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en " + nombreMetodo + ": " + e.getMessage());
            }
        }
        // Liberamos la conexión SIEMPRE, pase lo que pase arriba
        if (pool != null && con != null) {
            pool.freeConnection(con);
        }
    }
    
    /**
     * Inserta una carta nueva desde la API, o actualiza su precio/imagen si ya existe.
     */
    public boolean insertarOActualizarCarta(Carta c) throws SQLException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO CARTA (id_carta, nombre_carta, imagen, tipo, arquetipo, color, coste, poder, atributo_combate, precio_mercado) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE precio_mercado = ?, imagen = ?";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, c.getIdCarta());
            ps.setString(2, c.getNombreCarta());
            ps.setString(3, c.getImagen());
            ps.setString(4, c.getTipo());
            ps.setString(5, c.getArquetipo());
            ps.setString(6, c.getColor());
            ps.setInt(7, c.getCoste());
            ps.setInt(8, c.getPoder());
            ps.setString(9, c.getAtributoCarta()); // Puede ser null
            ps.setDouble(10, c.getPrecio());
            
            // Valores para la parte de UPDATE si la carta ya existe
            ps.setDouble(11, c.getPrecio());
            ps.setString(12, c.getImagen());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar/actualizar carta " + c.getIdCarta() + ": " + e.getMessage());
            return false;
        } finally {
            cerrarRecursos(pool, con, ps, null, "insertarOActualizarCarta");
        }
    }
    
    // 1. Obtener cartas aplicando REGLAS DE LÍDER (Filtros y Paginación)
    public ArrayList<Carta> obtenerCatalogoFiltrado(String color, String tipo, int limite, int offset, Carta lider) throws SQLException {
        ArrayList<Carta> lista = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM CARTA WHERE 1=1 ");
            
            // --- REGLAS DEL JUEGO ---
            if (lider == null) {
                // Si no hay líder en sesión, OBLIGAMOS a que el catálogo solo muestre líderes
                sql.append("AND tipo = 'LIDER' ");
            } else {
                // Si ya hay líder, ocultamos los líderes del catálogo (ya no puedes elegir otro)
                sql.append("AND tipo != 'LIDER' ");
                
                // Forzamos a que solo salgan cartas compatibles con los colores del líder
                String[] coloresLider = lider.getColor().split(",");
                sql.append("AND (");
                for (int i = 0; i < coloresLider.length; i++) {
                    sql.append("color LIKE '%").append(coloresLider[i].trim()).append("%' ");
                    if (i < coloresLider.length - 1) sql.append("OR ");
                }
                sql.append("OR color = 'MULTICOLOR') ");
            }

            // Filtros manuales del usuario (Drop-downs)
            if (color != null && !color.isEmpty()) sql.append("AND color LIKE ? ");
            if (tipo != null && !tipo.isEmpty() && lider != null) sql.append("AND tipo = ? "); 
            
            sql.append("ORDER BY id_carta LIMIT ? OFFSET ?");
            ps = con.prepareStatement(sql.toString());
            
            int pIndex = 1;
            if (color != null && !color.isEmpty()) ps.setString(pIndex++, "%" + color + "%");
            if (tipo != null && !tipo.isEmpty() && lider != null) ps.setString(pIndex++, tipo);
            ps.setInt(pIndex++, limite);
            ps.setInt(pIndex++, offset);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                Carta c = new Carta();
                c.setIdCarta(rs.getString("id_carta"));
                c.setNombreCarta(rs.getString("nombre_carta"));
                c.setImagen(rs.getString("imagen"));
                c.setTipo(rs.getString("tipo"));
                c.setColor(rs.getString("color"));
                c.setCoste(rs.getInt("coste"));
                c.setPoder(rs.getInt("poder"));
                c.setPrecio(rs.getDouble("precio_mercado"));
                lista.add(c);
            }
        } finally {
            cerrarRecursos(pool, con, ps, rs, "obtenerCatalogoFiltrado");
        }
        return lista;
    }

    // 2. Contar cartas (Para que la paginación no se vuelva loca al filtrar)
    public int contarCartasFiltradas(String color, String tipo, Carta lider) throws SQLException {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total FROM CARTA WHERE 1=1 ");
            
            if (lider == null) {
                sql.append("AND tipo = 'LIDER' ");
            } else {
                sql.append("AND tipo != 'LIDER' ");
                String[] coloresLider = lider.getColor().split(",");
                sql.append("AND (");
                for (int i = 0; i < coloresLider.length; i++) {
                    sql.append("color LIKE '%").append(coloresLider[i].trim()).append("%' ");
                    if (i < coloresLider.length - 1) sql.append("OR ");
                }
                sql.append("OR color = 'MULTICOLOR') ");
            }

            if (color != null && !color.isEmpty()) sql.append("AND color LIKE ? ");
            if (tipo != null && !tipo.isEmpty() && lider != null) sql.append("AND tipo = ? ");
            
            ps = con.prepareStatement(sql.toString());
            int pIndex = 1;
            if (color != null && !color.isEmpty()) ps.setString(pIndex++, "%" + color + "%");
            if (tipo != null && !tipo.isEmpty() && lider != null) ps.setString(pIndex++, tipo);
            
            rs = ps.executeQuery();
            if (rs.next()) total = rs.getInt("total");
        } finally {
            cerrarRecursos(pool, con, ps, rs, "contarCartasFiltradas");
        }
        return total;
    }
}
