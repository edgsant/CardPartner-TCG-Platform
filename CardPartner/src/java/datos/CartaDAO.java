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
     * Busca cartas aplicando filtros opcionales.
     */
    public ArrayList<Carta> filtrarCartas(String color, String tipo, String arquetipo, String atributo, int limite, int offset) throws SQLException {
        ArrayList<Carta> listaCartas = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("SELECT * FROM CARTA WHERE 1=1");

        // Aplicamos FIND_IN_SET para soportar cartas multicolores
        if (color != null && !color.trim().isEmpty()) {
            sql.append(" AND FIND_IN_SET(?, color) > 0");
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append(" AND tipo = ?");
        }
        if (arquetipo != null && !arquetipo.trim().isEmpty()) {
            sql.append(" AND arquetipo = ?");
        }
        if (atributo != null && !atributo.trim().isEmpty()) {
            sql.append(" AND atributo_combate = ?");
        }

        sql.append(" ORDER BY id_carta ASC LIMIT ? OFFSET ?");

        try {
            ps = con.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (color != null && !color.trim().isEmpty()) {
                ps.setString(paramIndex++, color);
            }
            if (tipo != null && !tipo.trim().isEmpty()) {
                ps.setString(paramIndex++, tipo);
            }
            if (arquetipo != null && !arquetipo.trim().isEmpty()) {
                ps.setString(paramIndex++, arquetipo);
            }
            if (atributo != null && !atributo.trim().isEmpty()) {
                ps.setString(paramIndex++, atributo);
            }

            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex, offset);

            rs = ps.executeQuery();

            while (rs.next()) {
                listaCartas.add(mapearCarta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar cartas: " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, rs, "filtrarCartas");
        }
        return listaCartas;
    }

    /**
     * Cuenta el total de cartas que cumplen con los filtros.
     */
    public int contarTotalCartas(String color, String tipo, String arquetipo, String atributo) throws SQLException {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM CARTA WHERE 1=1");

        if (color != null && !color.trim().isEmpty()) {
            sql.append(" AND FIND_IN_SET(?, color) > 0");
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append(" AND tipo = ?");
        }
        if (arquetipo != null && !arquetipo.trim().isEmpty()) {
            sql.append(" AND arquetipo = ?");
        }
        if (atributo != null && !atributo.trim().isEmpty()) {
            sql.append(" AND atributo_combate = ?");
        }

        try {
            ps = con.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (color != null && !color.trim().isEmpty()) {
                ps.setString(paramIndex++, color);
            }
            if (tipo != null && !tipo.trim().isEmpty()) {
                ps.setString(paramIndex++, tipo);
            }
            if (arquetipo != null && !arquetipo.trim().isEmpty()) {
                ps.setString(paramIndex++, arquetipo);
            }
            if (atributo != null && !atributo.trim().isEmpty()) {
                ps.setString(paramIndex++, atributo);
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar el total de cartas filtradas: " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, rs, "contarTotalCartas");
        }
        return total;
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
        c.setPrecioMercado(rs.getDouble("precio_mercado"));
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
}
