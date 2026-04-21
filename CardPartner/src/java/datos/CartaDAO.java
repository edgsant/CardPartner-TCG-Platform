package datos;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import modelo.Carta;

/**
 *
 * @author edgar
 */
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
     * Busca cartas y ademas puede aplicar nfiltros opcionales. Si un filtro viene a null o
     * vacío, se ignora.
     * @param color 
     * @param tipo
     * @param limite
     * @param offset
     * @return 
     */
    public ArrayList<Carta> filtrarCartas(String color, String tipo, int limite, int offset) {
        ArrayList<Carta> listaCartas = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 1. El truco del 1=1 nos permite encadenar 'AND' fácilmente después
        StringBuilder sql = new StringBuilder("SELECT * FROM CARTA WHERE 1=1");

        // 2. Comprobamos qué filtros ha usado el usuario y construimos el SQL
        if (color != null && !color.trim().isEmpty()) {
            sql.append(" AND color = ?");
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append(" AND tipo = ?");
        }

        // Añadimos siempre la paginación al final
        sql.append(" LIMIT ? OFFSET ?");

        try {
            ps = con.prepareStatement(sql.toString());

            // 3. Rellenar las interrogaciones (El reto de los índices variables)
            int paramIndex = 1; // Un contador para saber por qué interrogación vamos

            if (color != null && !color.trim().isEmpty()) {
                ps.setString(paramIndex++, color);
            }
            if (tipo != null && !tipo.trim().isEmpty()) {
                ps.setString(paramIndex++, tipo);
            }

            // Los dos últimos siempre son los de paginación
            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex, offset);

            // 4. Ejecutar
            rs = ps.executeQuery();

            while (rs.next()) {
                Carta c = new Carta();
                // ... rellenar el objeto DTO como ya sabes ...
                c.setIdCarta(rs.getString("id_carta"));
                // ...
                listaCartas.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            /* Limpieza habitual */
        }
        return listaCartas;
    }

}
