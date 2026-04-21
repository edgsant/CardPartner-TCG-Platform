package datos;

import java.sql.*;
import java.util.ArrayList;
import modelo.Carta;

public class CartaDAO {

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM CARTA WHERE id_carta = ?";

    public Carta obtenerCartaPorId(String idCarta) {
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
                cartaEncontrada = new Carta();
                cartaEncontrada.setIdCarta(rs.getString("id_carta"));
                cartaEncontrada.setNombreCarta(rs.getString("nombre_carta"));
                cartaEncontrada.setColor(rs.getString("color"));
                cartaEncontrada.setTipo(rs.getString("tipo"));
                cartaEncontrada.setCoste(rs.getInt("coste"));
                cartaEncontrada.setPoder(rs.getInt("poder"));
                cartaEncontrada.setImagen(rs.getString("imagen"));
                cartaEncontrada.setAtributoCarta(rs.getString("atributo_combate")); // CORREGIDO
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cartaEncontrada;
    }

    public ArrayList<Carta> filtrarCartas(String color, String tipo, String arquetipo, String atributo, int limite, int offset) {
        ArrayList<Carta> listaCartas = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("SELECT * FROM CARTA WHERE 1=1");

        if (color != null && !color.trim().isEmpty()) {
            sql.append(" AND color = ?");
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append(" AND tipo = ?");
        }
        if (arquetipo != null && !arquetipo.trim().isEmpty()) {
            sql.append(" AND arquetipo = ?");
        }
        if (atributo != null && !atributo.trim().isEmpty()) {
            sql.append(" AND atributo_combate = ?"); // CORREGIDO
        }

        sql.append(" LIMIT ? OFFSET ?");

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
                Carta c = new Carta();
                c.setIdCarta(rs.getString("id_carta"));
                c.setNombreCarta(rs.getString("nombre_carta"));
                c.setColor(rs.getString("color"));
                c.setTipo(rs.getString("tipo"));
                c.setArquetipo(rs.getString("arquetipo"));
                c.setAtributoCarta(rs.getString("atributo_combate")); // CORREGIDO
                c.setCoste(rs.getInt("coste"));
                c.setPoder(rs.getInt("poder"));
                c.setImagen(rs.getString("imagen"));

                listaCartas.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listaCartas;
    }
}