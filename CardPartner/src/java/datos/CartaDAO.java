package datos;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import modelo.Carta;


public class CartaDAO {

    // Consulta para buscar una carta específica (ej. al hacer clic en ella o añadirla a un mazo)
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM CARTA WHERE id_carta = ?";
    // Consulta paginada para la galería/catálogo general
    private static final String SQL_SELECT_CATALOGO_PAGINADO = "SELECT * FROM CARTA ORDER BY id_carta ASC LIMIT ? OFFSET ?";

    /**
     * Busca una carta específica por su código identificador (Ej: "OP01-001")
     * Devuelve el objeto Carta completo o null si no existe.
     */
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
                // OJO: Ajusta estos 'setters' a los nombres exactos que tengas en tu Carta.java
                cartaEncontrada.setIdCarta(rs.getString("id_carta"));
                cartaEncontrada.setNombreCarta(rs.getString("nombre_carta"));
                cartaEncontrada.setColor(rs.getString("color"));
                cartaEncontrada.setTipo(rs.getString("tipo")); // Lider, Personaje, Evento...
                cartaEncontrada.setCoste(rs.getInt("coste"));
                cartaEncontrada.setPoder(rs.getInt("poder"));
                // Si la imagen la guardas como texto (URL/ruta), es un String
                cartaEncontrada.setImagen(rs.getString("imagen"));
                
                cartaEncontrada.setArquetipo(rs.getString("arquetipo"));
                cartaEncontrada.setAtributoCarta(rs.getString("atributo_combate"));
                cartaEncontrada.setPrecioMercado(rs.getDouble("precio_mercado"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cartaEncontrada;
    }

    /**
     * Busca cartas aplicando filtros opcionales múltiples. Si un filtro viene a
     * null o vacío, se ignora.
     *
     * @param color
     * @param tipo
     * @param arquetipo
     * @param atributo
     * @param limite
     * @param offset
     * @return
     */
    public ArrayList<Carta> filtrarCartas(String color, String tipo, String arquetipo, String atributo, int limite, int offset) {
        ArrayList<Carta> listaCartas = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 1. Iniciamos con el truco del 1=1
        StringBuilder sql = new StringBuilder("SELECT * FROM CARTA WHERE 1=1");

        // 2. Construimos el SQL dinámico según los filtros que lleguen
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
            sql.append(" AND atributo_combate = ?");
        }

        // Añadimos siempre la paginación al final
        sql.append(" LIMIT ? OFFSET ?");

        try {
            ps = con.prepareStatement(sql.toString());

            // 3. Rellenamos las interrogaciones en el mismo orden exacto
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

            // Los dos últimos siempre son el límite y el salto
            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex, offset);

            // 4. Ejecutar consulta
            rs = ps.executeQuery();

            while (rs.next()) {
                Carta c = new Carta();
                c.setIdCarta(rs.getString("id_carta"));
                c.setNombreCarta(rs.getString("nombre_carta"));
                c.setColor(rs.getString("color"));
                c.setTipo(rs.getString("tipo"));
                // Asegúrate de que tu DTO Carta tiene estos setters
                c.setArquetipo(rs.getString("arquetipo"));
                c.setAtributoCarta(rs.getString("atributo_combate"));
                c.setCoste(rs.getInt("coste"));
                c.setPoder(rs.getInt("poder"));
                c.setImagen(rs.getString("imagen"));
                listaCartas.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listaCartas;
    }
    /**
     * Cuenta el total de cartas que cumplen con los filtros aplicados.
     * Útil para calcular el número de páginas en el frontend.
     */
    public int contarTotalCartas(String color, String tipo, String arquetipo, String atributo) {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 1. SQL dinámico similar al de filtrarCartas, pero con COUNT(*)
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM CARTA WHERE 1=1");

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
            sql.append(" AND atributo_combate = ?");
        }

        try {
            ps = con.prepareStatement(sql.toString());

            // 2. Rellenamos los parámetros en el mismo orden
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

            // 3. Ejecutar y obtener el número
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 4. Cierre de recursos siguiendo tu estructura
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return total;
    }
}
