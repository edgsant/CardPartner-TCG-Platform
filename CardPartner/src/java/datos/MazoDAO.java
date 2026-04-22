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

    // Consulta que une el mazo con la carta líder para traer su imagen
    private static final String SQL_SELECT_POR_USUARIO
            = "SELECT m.*, c.nombre_carta, c.imagen, c.color "
            + "FROM MAZO m "
            + "INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta "
            + "WHERE m.id_usuario_creador = ? "
            + "ORDER BY m.fecha_creacion DESC";

    // Borra el mazo asegurando que pertenece a ese usuario
    private static final String SQL_DELETE_MAZO = "DELETE FROM MAZO WHERE id_mazo = ? AND id_usuario_creador = ?";

    private static final String SQL_SELECT_MAZO_POR_ID = "SELECT * FROM MAZO WHERE id_mazo = ?";

    // La consulta mágica que une la cantidad con los datos visuales de la carta
    private static final String SQL_SELECT_CARTAS_DE_MAZO
            = "SELECT cc.cantidad, c.* "
            + "FROM CONTIENE_CARTA cc "
            + "INNER JOIN CARTA c ON cc.id_carta = c.id_carta "
            + "WHERE cc.id_mazo = ?";

    private static final String SQL_SELECT_FAVORITOS
            = "SELECT m.*, c.nombre_carta, c.imagen, c.color "
            + "FROM MAZO m "
            + "INNER JOIN GUARDA_MAZO gm ON m.id_mazo = gm.id_mazo "
            + "INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta "
            + "WHERE gm.id_usuario = ?";

    private static final String SQL_INSERT_FAVORITO = "INSERT INTO GUARDA_MAZO (id_usuario, id_mazo) VALUES (?, ?)";
    private static final String SQL_DELETE_FAVORITO = "DELETE FROM GUARDA_MAZO WHERE id_usuario = ? AND id_mazo = ?";

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
     *
     * Busca mazos públicos en la comunidad aplicando filtros dinámicos. Soporta
     * coincidencias parciales en el nombre del mazo. Usa un INNER JOIN con la
     * carta líder para optimizar consultas y permitir el filtrado por el color
     * del líder.
     *
     * @param esOficial
     * @param minWinrate
     * @param nombreBuscado
     * @param idCartaLider
     * @param colorLider
     * @param limite
     * @param offset
     * @return
     */
    public ArrayList<Mazo> filtrarMazosPublicos(Boolean esOficial, Float minWinrate, String nombreBuscado, String idCartaLider, String colorLider, int limite, int offset) {
        ArrayList<Mazo> listaMazos = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 1. BASE DE LA CONSULTA CON INNER JOIN
        StringBuilder sql = new StringBuilder(
                "SELECT m.*, c.nombre_carta, c.imagen, c.color "
                + "FROM MAZO m "
                + "INNER JOIN CARTA c ON m.id_carta_lider = c.id_carta "
                + "WHERE m.es_publico = 1"
        );

        // 2. APLICAMOS FILTROS
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
            // Permite que un mazo "Rojo/Verde" salga si buscamos "Rojo"
            sql.append(" AND c.color LIKE ?");
        }

        sql.append(" ORDER BY m.fecha_creacion DESC LIMIT ? OFFSET ?");

        try {
            ps = con.prepareStatement(sql.toString());
            int paramIndex = 1;

            // 3. RELLENAMOS PARÁMETROS
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
                // Montamos el Mazo
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

                // Montamos la Carta Líder directamente desde la misma consulta
                modelo.Carta lider = new modelo.Carta();
                lider.setIdCarta(rs.getString("id_carta_lider"));
                lider.setNombreCarta(rs.getString("nombre_carta"));
                lider.setImagen(rs.getString("imagen"));
                lider.setColor(rs.getString("color"));

                m.setCartaLider(lider);
                listaMazos.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // ... (cierres habituales)
        }
        return listaMazos;
    }

    /**
     * Elimina un mazo de la base de datos. Importante: Asume que la base de
     * datos tiene "ON DELETE CASCADE" en la tabla CONTIENE_CARTA, de lo
     * contrario dará error de clave foránea.
     */
    public boolean borrarMazo(int idMazo, int idUsuarioCreador) {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(SQL_DELETE_MAZO);
            ps.setInt(1, idMazo);
            ps.setInt(2, idUsuarioCreador);

            int filasBorradas = ps.executeUpdate();
            if (filasBorradas > 0) {
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
     * Recupera las 50 cartas de un mazo específico junto con su cantidad. Se
     * usa SOLO cuando el usuario entra a ver el detalle de un mazo.
     *
     * @param idMazo
     * @return
     */
    public ArrayList<modelo.ContieneCarta> obtenerCartasDeUnMazo(int idMazo) {
        ArrayList<modelo.ContieneCarta> listaCartas = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_CARTAS_DE_MAZO);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();

            while (rs.next()) {
                // 1. Creamos el objeto puente (la cantidad)
                modelo.ContieneCarta cc = new modelo.ContieneCarta();
                cc.setIdMazo(idMazo);
                cc.setIdCarta(rs.getString("id_carta"));
                cc.setCantidad(rs.getInt("cantidad"));

                // 2. Creamos la carta real con su foto, poder, etc.
                modelo.Carta carta = new modelo.Carta();
                carta.setIdCarta(rs.getString("id_carta"));
                carta.setNombreCarta(rs.getString("nombre_carta"));
                carta.setImagen(rs.getString("imagen"));
                carta.setColor(rs.getString("color"));
                carta.setTipo(rs.getString("tipo"));
                carta.setCoste(rs.getInt("coste"));
                carta.setPoder(rs.getInt("poder"));

                // 3. Metemos la carta dentro del objeto puente
                cc.setCarta(carta); // Asegúrate de tener este atributo y setter en tu clase ContieneCarta

                listaCartas.add(cc);
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
     * Busca un mazo específico por su ID.
     *
     * @param idMazo
     * @return
     */
    public Mazo obtenerMazoPorId(int idMazo) {
        Mazo mazoEncontrado = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_MAZO_POR_ID);
            ps.setInt(1, idMazo);
            rs = ps.executeQuery();

            if (rs.next()) {
                mazoEncontrado = new Mazo();
                mazoEncontrado.setIdMazo(rs.getInt("id_mazo"));
                mazoEncontrado.setNombreMazo(rs.getString("nombre_mazo"));
                mazoEncontrado.setDescripcion(rs.getString("descripcion"));
                mazoEncontrado.setEsPublico(rs.getBoolean("es_publico"));
                mazoEncontrado.setEsOficial(rs.getBoolean("es_oficial"));
                mazoEncontrado.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                mazoEncontrado.setWinrate(rs.getFloat("winrate"));
                mazoEncontrado.setIdUsuarioCreador(rs.getInt("id_usuario_creador"));
                mazoEncontrado.setIdCartaLider(rs.getString("id_carta_lider"));
                // IMPORTANTE: Rellenar el objeto carta líder
            modelo.Carta lider = new modelo.Carta();
            lider.setIdCarta(rs.getString("id_carta_lider"));
            lider.setNombreCarta(rs.getString("nombre_carta"));
            lider.setImagen(rs.getString("imagen"));
            lider.setColor(rs.getString("color"));
            mazoEncontrado.setCartaLider(lider);
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
        return mazoEncontrado;
    }

    /**
     * Obtiene los mazos que el usuario ha guardado
     *
     * @param idUsuario
     * @return
     */
    public ArrayList<Mazo> obtenerMazosGuardados(int idUsuario) {
        ArrayList<Mazo> listaFavoritos = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_FAVORITOS);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            while (rs.next()) {
                Mazo m = new Mazo();
                m.setIdMazo(rs.getInt("id_mazo"));
                m.setNombreMazo(rs.getString("nombre_mazo"));
                // Rellenamos el RESTO de campos
                m.setDescripcion(rs.getString("descripcion"));
                m.setEsPublico(rs.getBoolean("es_publico"));
                m.setEsOficial(rs.getBoolean("es_oficial"));
                m.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                m.setWinrate(rs.getFloat("winrate"));
                m.setIdUsuarioCreador(rs.getInt("id_usuario_creador"));
                m.setIdCartaLider(rs.getString("id_carta_lider"));

                modelo.Carta lider = new modelo.Carta();
                lider.setIdCarta(rs.getString("id_carta_lider"));
                lider.setNombreCarta(rs.getString("nombre_carta"));
                lider.setImagen(rs.getString("imagen"));
                lider.setColor(rs.getString("color"));

                m.setCartaLider(lider);
                listaFavoritos.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // AHORA SÍ CERRAMOS LA CONEXIÓN
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listaFavoritos;
    }

    /**
     * Guarda un mazo en la lista de favoritos del usuario
     */
    public boolean añadirMazoFavorito(int idUsuario, int idMazo) {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(SQL_INSERT_FAVORITO);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMazo);
            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Si da error por clave duplicada, simplemente devolverá false
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
     * Elimina un mazo de la lista de favoritos del usuario
     */
    public boolean quitarMazoFavorito(int idUsuario, int idMazo) {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(SQL_DELETE_FAVORITO);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMazo);
            if (ps.executeUpdate() > 0) {
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
     * Obtiene todos los mazos creados por un usuario específico.
     * Incluye los datos de la carta líder mediante el INNER JOIN.
     */
    public ArrayList<Mazo> obtenerMazosPorUsuario(int idUsuario) {
        ArrayList<Mazo> listaMazos = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_POR_USUARIO);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            while (rs.next()) {
                // 1. Creamos el mazo con todos sus atributos
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

                // 2. Creamos la carta líder usando los datos del JOIN
                modelo.Carta lider = new modelo.Carta();
                lider.setIdCarta(rs.getString("id_carta_lider"));
                lider.setNombreCarta(rs.getString("nombre_carta"));
                lider.setImagen(rs.getString("imagen"));
                lider.setColor(rs.getString("color"));
                // 3. Guardamos la carta dentro del mazo
                m.setCartaLider(lider);

                listaMazos.add(m);
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
        return listaMazos;
    }
}
