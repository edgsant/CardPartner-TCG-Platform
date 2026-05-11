package datos;

import java.sql.*;
import java.util.ArrayList;
import modelo.Carta;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Carta. Gestiona el catálogo de
 * cartas, permitiendo búsquedas simples, inserciones/actualizaciones masivas
 * (Upsert) y búsquedas complejas con múltiples filtros dinámicos y reglas de
 * juego (como la restricción de colores por líder).
 */
public class CartaDAO {

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM CARTA WHERE id_carta = ?";

    /**
     * Busca y recupera una carta específica utilizando su código identificador
     * único.
     *
     * @param idCarta El identificador alfanumérico de la carta (ej:
     * "OP01-001").
     * @return Un objeto Carta con todos sus datos, o null si no se encuentra en
     * la base de datos.
     * @throws SQLException Si ocurre un error de acceso a la base de datos.
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
     * Realiza una búsqueda avanzada de cartas aplicando múltiples filtros
     * simultáneos y paginación. Construye la consulta SQL de forma dinámica
     * dependiendo de los parámetros que no sean nulos.
     *
     * @param nombre Palabra clave para buscar coincidencias en nombre, tipo o
     * atributo.
     * @param colores Array con los colores permitidos para filtrar (ej:
     * ["ROJO", "VERDE"]).
     * @param tipos Array con los tipos de carta permitidos (ej: ["PERSONAJE",
     * "EVENTO"]).
     * @param atributos Array con los atributos de combate permitidos (ej:
     * ["SLASH", "STRIKE"]).
     * @param limite Número máximo de resultados a devolver (tamaño de página).
     * @param offset Número de resultados a saltar desde el inicio.
     * @return Un ArrayList de cartas que cumplen con todos los filtros
     * especificados.
     * @throws SQLException Si ocurre un error en la construcción o ejecución de
     * la consulta SQL.
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
     * Cuenta el número total de cartas que coinciden con los filtros múltiples
     * especificados. Esencial para calcular el número total de páginas en la
     * interfaz del usuario.
     *
     * @param nombre Palabra clave a buscar en nombre, tipo o atributo.
     * @param colores Array con los colores filtrados.
     * @param tipos Array con los tipos filtrados.
     * @param atributos Array con los atributos filtrados.
     * @return El número total de cartas que coinciden con los criterios de
     * búsqueda.
     * @throws SQLException Si ocurre un error al realizar el conteo en la base
     * de datos.
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

    /**
     * Inserta una nueva carta en la base de datos. Si la carta ya existe
     * (duplicidad de clave primaria), actualiza sus campos volátiles como el
     * precio de mercado y la imagen (Upsert). Ideal para sincronizar datos
     * masivos desde una API externa.
     *
     * @param c El objeto Carta con los datos a insertar o actualizar.
     * @return true si la operación afectó a alguna fila con éxito.
     * @throws SQLException Si ocurre un error de integridad de datos o de
     * conexión.
     */
    public boolean insertarOActualizarCarta(Carta c) throws SQLException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO CARTA (id_carta, nombre_carta, imagen, tipo, arquetipo, color, coste, poder, atributo_combate, precio_mercado) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE precio_mercado = ?, imagen = ?";

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
            throw e;
        } finally {
            cerrarRecursos(pool, con, ps, null, "insertarOActualizarCarta");
        }
    }

    /**
     * Recupera el catálogo de cartas aplicando filtros y reglas específicas del
     * juego de construcción de mazos. Si se proporciona un líder, el catálogo
     * se restringe automáticamente a las cartas que coincidan con la identidad
     * de color de dicho líder y oculta a otros líderes.
     *
     * @param color Filtro adicional de color seleccionado por el usuario.
     * @param tipo Filtro adicional de tipo de carta seleccionado por el
     * usuario.
     * @param limite Número máximo de cartas a devolver.
     * @param offset Número de resultados a saltar.
     * @param lider El objeto Carta que representa al líder actual del mazo
     * (puede ser null).
     * @return Un ArrayList de cartas válidas para ser añadidas al mazo del
     * líder actual.
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * consulta.
     */
    public ArrayList<Carta> obtenerCatalogoFiltrado(String color, String tipo, int limite, int offset, Carta lider) throws SQLException {
        ArrayList<Carta> lista = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM CARTA WHERE 1=1 ");
            String[] coloresLider = null; // Para guardar los colores y usarlos en el PreparedStatement

            // --- REGLAS DEL JUEGO ---
            if (lider == null) {
                // Si no hay líder en sesión, OBLIGAMOS a que el catálogo solo muestre líderes
                sql.append("AND tipo = 'LIDER' ");
            } else {
                // Si ya hay líder, ocultamos los líderes del catálogo (ya no puedes elegir otro)
                sql.append("AND tipo != 'LIDER' ");

                // Forzamos a que solo salgan cartas compatibles con los colores del líder
                coloresLider = lider.getColor().split(",");
                sql.append("AND (");
                for (int i = 0; i < coloresLider.length; i++) {
                    sql.append("color LIKE ? "); // 🌟 CORREGIDO: Usamos ? de seguridad
                    if (i < coloresLider.length - 1) {
                        sql.append("OR ");
                    }
                }
                sql.append("OR color = 'MULTICOLOR') ");
            }

            // Filtros manuales del usuario (Drop-downs)
            if (color != null && !color.isEmpty()) {
                sql.append("AND color LIKE ? ");
            }
            if (tipo != null && !tipo.isEmpty() && lider != null) {
                sql.append("AND tipo = ? ");
            }

            sql.append("ORDER BY id_carta LIMIT ? OFFSET ?");
            ps = con.prepareStatement(sql.toString());

            int pIndex = 1;
            if (coloresLider != null) {
                for (String c : coloresLider) {
                    ps.setString(pIndex++, "%" + c.trim() + "%");
                }
            }
            if (color != null && !color.isEmpty()) {
                ps.setString(pIndex++, "%" + color + "%");
            }
            if (tipo != null && !tipo.isEmpty() && lider != null) {
                ps.setString(pIndex++, tipo);
            }
            ps.setInt(pIndex++, limite);
            ps.setInt(pIndex++, offset);

            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearCarta(rs));
            }
        } finally {
            cerrarRecursos(pool, con, ps, rs, "obtenerCatalogoFiltrado");
        }
        return lista;
    }

    /**
     * Cuenta el total de cartas disponibles aplicando las reglas de
     * construcción de mazo según el líder actual. Necesario para mantener la
     * consistencia de la paginación al construir un mazo.
     *
     * @param color Filtro adicional de color seleccionado por el usuario.
     * @param tipo Filtro adicional de tipo de carta seleccionado por el
     * usuario.
     * @param lider El objeto Carta que representa al líder actual del mazo.
     * @return El número total de cartas válidas bajo estos criterios.
     * @throws SQLException Si ocurre un error en el conteo.
     */
    public int contarCartasFiltradas(String color, String tipo, Carta lider) throws SQLException {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total FROM CARTA WHERE 1=1 ");
            String[] coloresLider = null; // Para guardar los colores y usarlos en el PreparedStatement

            if (lider == null) {
                sql.append("AND tipo = 'LIDER' ");
            } else {
                sql.append("AND tipo != 'LIDER' ");
                coloresLider = lider.getColor().split(",");
                sql.append("AND (");
                for (int i = 0; i < coloresLider.length; i++) {
                    sql.append("color LIKE ? "); // 🌟 CORREGIDO: Usamos ? de seguridad
                    if (i < coloresLider.length - 1) {
                        sql.append("OR ");
                    }
                }
                sql.append("OR color = 'MULTICOLOR') ");
            }

            if (color != null && !color.isEmpty()) {
                sql.append("AND color LIKE ? ");
            }
            if (tipo != null && !tipo.isEmpty() && lider != null) {
                sql.append("AND tipo = ? ");
            }

            ps = con.prepareStatement(sql.toString());
            int pIndex = 1;

            if (coloresLider != null) {
                for (String c : coloresLider) {
                    ps.setString(pIndex++, "%" + c.trim() + "%");
                }
            }
            if (color != null && !color.isEmpty()) {
                ps.setString(pIndex++, "%" + color + "%");
            }
            if (tipo != null && !tipo.isEmpty() && lider != null) {
                ps.setString(pIndex++, tipo);
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } finally {
            cerrarRecursos(pool, con, ps, rs, "contarCartasFiltradas");
        }
        return total;
    }

    // --- MÉTODOS AUXILIARES PARA NO REPETIR CÓDIGO ---
    /**
     * Método auxiliar que construye dinámicamente los fragmentos "WHERE" de una
     * consulta SQL basándose en los arrays de filtros proporcionados.
     *
     * @param sql El StringBuilder que contiene la consulta SQL en construcción.
     * @param nombre El texto a buscar globalmente.
     * @param colores Array de colores a incluir.
     * @param tipos Array de tipos a incluir.
     * @param atributos Array de atributos a incluir.
     */
    private void construirFiltrosDinamicos(StringBuilder sql, String nombre, String[] colores, String[] tipos, String[] atributos) {
        // NUEVO: Buscador Global (Nombre OR Atributo OR Tipo)
        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND (nombre_carta LIKE ? OR atributo_combate LIKE ? OR tipo LIKE ?)");
        }
        if (colores != null && colores.length > 0) {
            sql.append(" AND (");
            for (int i = 0; i < colores.length; i++) {
                sql.append("FIND_IN_SET(?, color) > 0");
                if (i < colores.length - 1) {
                    sql.append(" OR ");
                }
            }
            sql.append(")");
        }
        if (tipos != null && tipos.length > 0) {
            sql.append(" AND tipo IN (");
            for (int i = 0; i < tipos.length; i++) {
                sql.append("?");
                if (i < tipos.length - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
        }
        if (atributos != null && atributos.length > 0) {
            sql.append(" AND atributo_combate IN (");
            for (int i = 0; i < atributos.length; i++) {
                sql.append("?");
                if (i < atributos.length - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
        }
    }

    /**
     * Método auxiliar que inyecta los valores de los filtros dinámicos en un
     * PreparedStatement de forma segura, respetando el orden generado por
     * construirFiltrosDinamicos.
     *
     * @param ps El PreparedStatement donde se inyectarán los parámetros.
     * @param nombre El texto de búsqueda global.
     * @param colores Array de colores seleccionados.
     * @param tipos Array de tipos seleccionados.
     * @param atributos Array de atributos seleccionados.
     * @param startIndex El índice inicial (1-based) desde el que empezar a
     * asignar en el PreparedStatement.
     * @return El siguiente índice disponible tras asignar todos los parámetros.
     * @throws SQLException Si hay un error de asignación de tipos en el
     * PreparedStatement.
     */
    private int asignarParametrosDinamicos(PreparedStatement ps, String nombre, String[] colores, String[] tipos, String[] atributos, int startIndex) throws SQLException {
        if (nombre != null && !nombre.trim().isEmpty()) {
            String busqueda = "%" + nombre.trim() + "%";
            ps.setString(startIndex++, busqueda); // Para nombre_carta
            ps.setString(startIndex++, busqueda); // Para atributo_combate
            ps.setString(startIndex++, busqueda); // Para tipo
        }
        if (colores != null) {
            for (String c : colores) {
                ps.setString(startIndex++, c);
            }
        }
        if (tipos != null) {
            for (String t : tipos) {
                ps.setString(startIndex++, t);
            }
        }
        if (atributos != null) {
            for (String a : atributos) {
                ps.setString(startIndex++, a);
            }
        }
        return startIndex;
    }

    /**
     * Método auxiliar que transforma la fila actual de un ResultSet en un
     * objeto Carta poblado. Centraliza la lógica de extracción de columnas para
     * evitar repetición de código.
     *
     * @param rs El ResultSet posicionado en la fila que se desea extraer.
     * @return El objeto Carta instanciado con los datos de la base de datos.
     * @throws SQLException Si alguna columna solicitada no existe o hay error
     * de lectura.
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
     * Centraliza y asegura el cierre de los recursos JDBC (ResultSet,
     * PreparedStatement y Connection) utilizando bloques try-catch individuales
     * para evitar que un fallo en el cierre de un recurso impida liberar la
     * conexión al pool.
     *
     * @param pool La instancia del ConnectionPool gestor.
     * @param con La conexión a liberar.
     * @param ps El PreparedStatement a cerrar.
     * @param rs El ResultSet a cerrar.
     * @param nombreMetodo Nombre del método invocador (para facilitar el
     * rastreo en los logs de error).
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
