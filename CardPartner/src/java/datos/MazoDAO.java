package datos;

import java.io.InputStream;
import java.io.OutputStream;
import modelo.Mazo;
import modelo.ContieneCarta;
import modelo.GuardaMazo;
import modelo.Valoracion;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author edgar
 */
public class MazoDAO {

    // Para crear el mazo base
    private static final String SQL_INSERT_MAZO = "INSERT INTO MAZO (nombre_mazo, descripcion, es_publico, es_oficial, id_usuario_creador, id_carta_lider) VALUES (?, ?, ?, ?, ?, ?)";

    // Para meter las cartas dentro del mazo
    private static final String SQL_INSERT_CARTA_EN_MAZO = "INSERT INTO CONTIENE_CARTA (id_mazo, id_carta, cantidad) VALUES (?, ?, ?)";

    // Para listar los mazos de la comunidad PAGINADO para que no explote al consultar cientos de elementos
    private static final String SQL_SELECT_PUBLICOS_PAGINADO
            = "SELECT * FROM MAZO WHERE es_publico = 1 ORDER BY fecha_creacion DESC LIMIT ? OFFSET ?";

    /**
     * Este método es idéntico a registrar un usuario. La misión es insertar la
     * cabecera del mazo (nombre, creador, líder) y recuperar el ID autogenerado
     * para poder usarlo en el siguiente paso
     *
     * @param mazo
     * @return
     */
    public int crearMazo(Mazo mazo) {
        int idMazoGenerado = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Pedimos que nos devuelva el ID del mazo
            ps = con.prepareStatement(SQL_INSERT_MAZO, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, mazo.getNombreMazo());
            ps.setString(2, mazo.getDescripcion());
            ps.setBoolean(3, mazo.isEsPublico()); // JDBC convierte boolean a TINYINT(1) automáticamente
            ps.setBoolean(4, mazo.isEsOficial());
            ps.setInt(5, mazo.getIdUsuarioCreador());
            ps.setString(6, mazo.getIdCartaLider());

            ps.executeUpdate();

            // Recuperamos el ID que MariaDB acaba de crear
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idMazoGenerado = rs.getInt(1);
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

        return idMazoGenerado;
    }

    /**
     * Aquí viene algo nuevo e importante. Un mazo tiene 50 cartas. Si hacéis un
     * bucle for y llamáis a ps.executeUpdate() 50 veces, obligaréis a Java a
     * viajar por la red hasta MariaDB 50 veces seguidas. Eso es lentísimo.
     *
     * La solución profesional se llama Batch Processing (Por Lotes). Consiste
     * en preparar los 50 INSERTs, meterlos en una "caja", y enviárselos a
     * MariaDB en un solo viaje.
     *
     * @param idMazo
     * @param listaCartas
     * @return
     */
    public boolean añadirCartasAMazo(int idMazo, ArrayList<ContieneCarta> listaCartas) {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(SQL_INSERT_CARTA_EN_MAZO);

            // Recorremos la lista de cartas que el usuario ha elegido
            for (ContieneCarta cc : listaCartas) {
                ps.setInt(1, idMazo);           // El ID del mazo que acabamos de crear
                ps.setString(2, cc.getIdCarta()); // El ID de la carta (ej. "OP01-001")
                ps.setInt(3, cc.getCantidad());   // Cuántas copias (ej. 4)

                // ¡MAGIA! En lugar de ejecutarlo, lo añadimos al lote (Batch)
                ps.addBatch();
            }

            // Ejecutamos todos los INSERTS de golpe
            int[] resultados = ps.executeBatch();

            // Si el array tiene datos, es que todo fue bien
            if (resultados.length > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exito;
    }

    /**
     * Busca mazos públicos en la comunidad aplicando filtros dinámicos. Soporta
     * coincidencias parciales en el nombre del mazo.
     *
     * @param esOficial
     * @param minWinrate
     * @param nombreBuscado
     * @param idCartaLider
     * @param limite
     * @param offset
     * @return
     */
    public ArrayList<Mazo> filtrarMazosPublicos(Boolean esOficial, Float minWinrate, String nombreBuscado, String idCartaLider, int limite, int offset) {
        ArrayList<Mazo> listaMazos = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 1. Nuestra base: SIEMPRE deben ser públicos (Esto actúa como nuestro 1=1)
        StringBuilder sql = new StringBuilder("SELECT * FROM MAZO WHERE es_publico = 1");

        // 2. Construimos el SQL dinámico
        if (esOficial != null) {
            sql.append(" AND es_oficial = ?");
        }
        if (minWinrate != null) {
            // Para el winrate tiene sentido buscar los que tengan "igual o más" que lo pedido
            sql.append(" AND winrate >= ?");
        }
        if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) {
            // Usamos LIKE para búsquedas parciales
            sql.append(" AND nombre_mazo LIKE ?");
        }
        if (idCartaLider != null && !idCartaLider.trim().isEmpty()) {
            sql.append(" AND id_carta_lider = ?");
        }

        // Ordenamos por los más nuevos y añadimos paginación
        sql.append(" ORDER BY fecha_creacion DESC LIMIT ? OFFSET ?");

        try {
            ps = con.prepareStatement(sql.toString());
            int paramIndex = 1;

            // 3. Rellenamos los parámetros
            if (esOficial != null) {
                // JDBC sabe traducir el Boolean de Java al TINYINT(1) de MariaDB
                ps.setBoolean(paramIndex++, esOficial);
            }
            if (minWinrate != null) {
                ps.setFloat(paramIndex++, minWinrate);
            }
            if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) {
                // TRUCO PRO: Añadimos los '%' aquí, NO en el String de SQL. 
                // Así evitamos la inyección SQL.
                ps.setString(paramIndex++, "%" + nombreBuscado.trim() + "%");
            }
            if (idCartaLider != null && !idCartaLider.trim().isEmpty()) {
                ps.setString(paramIndex++, idCartaLider);
            }

            // Parámetros de paginación
            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex, offset);

            // 4. Ejecutar
            rs = ps.executeQuery();

            while (rs.next()) {
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

                listaMazos.add(m);
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

        return listaMazos;
    }
}
