package datos;

import java.sql.*;
import java.util.ArrayList;
import modelo.Articulo;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Articulo. Se encarga de
 * gestionar todas las operaciones CRUD y de búsqueda en la base de datos
 * relacionadas con las noticias, guías y torneos, incluyendo la lógica de
 * paginación. Esta clase delega la gestión de conexiones a ConnectionPool.
 */
public class ArticuloDAO {

    // Constantes SQL actualizadas con paginación y conteos
    private static final String SQL_INSERT = "INSERT INTO ARTICULO (titulo, descripcion, categoria, enlace_externo, portada, id_admin_autor) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM ARTICULO WHERE id_articulo = ?";

    // Consultas paginadas
    private static final String SQL_SELECT_ALL_PAGINADO = "SELECT * FROM ARTICULO ORDER BY fecha_creacion DESC LIMIT ? OFFSET ?";
    private static final String SQL_SELECT_BY_CATEGORIA_PAGINADO = "SELECT * FROM ARTICULO WHERE categoria = ? ORDER BY fecha_creacion DESC LIMIT ? OFFSET ?";

    // Consultas para contar el total (útil para saber cuántos botones de página dibujar)
    private static final String SQL_COUNT_ALL = "SELECT COUNT(*) FROM ARTICULO";
    private static final String SQL_COUNT_BY_CATEGORIA = "SELECT COUNT(*) FROM ARTICULO WHERE categoria = ?";

    // Consultas para ajustarse al CRUD (Create, Read, Update, Delete)
    private static final String SQL_UPDATE = "UPDATE ARTICULO SET titulo = ?, descripcion = ?, categoria = ?, enlace_externo = ?, portada = ?, fecha_edicion = CURRENT_TIMESTAMP WHERE id_articulo = ?";
    private static final String SQL_DELETE = "DELETE FROM ARTICULO WHERE id_articulo = ?";

    // Nueva funcionalidad para posible busqueda de articulos
    private static final String SQL_SEARCH_PAGINADO = "SELECT * FROM ARTICULO WHERE titulo LIKE ? OR descripcion LIKE ? ORDER BY fecha_creacion DESC LIMIT ? OFFSET ?";
    private static final String SQL_COUNT_SEARCH = "SELECT COUNT(*) FROM ARTICULO WHERE titulo LIKE ? OR descripcion LIKE ?";

    /**
     * Inserta un nuevo artículo en la base de datos.
     *
     * * @param a El objeto Articulo que contiene los datos a insertar.
     * @return El ID numérico autogenerado del artículo si la inserción fue
     * exitosa; 0 si falló.
     * @throws SQLException Si ocurre un error de acceso a la base de datos o en
     * la sintaxis SQL.
     */
    public int crearArticulo(Articulo a) throws SQLException {
        int idGenerado = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, a.getTitulo());
            ps.setString(2, a.getDescripcion());
            ps.setString(3, a.getCategoria());
            ps.setString(4, a.getEnlaceExterno());
            ps.setString(5, a.getPortada());
            ps.setInt(6, a.getIdAdminAutor());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear el artículo " + a.getTitulo() + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return idGenerado;
    }

    /**
     * Recupera una lista paginada de todos los artículos ordenados por fecha de
     * creación descendente (los más nuevos primero).
     *
     * * @param limite El número máximo de artículos a recuperar (tamaño de la
     * página).
     * @param offset El número de artículos a ignorar desde el inicio
     * (desplazamiento).
     * @return Un ArrayList con los artículos encontrados. Estará vacío si no
     * hay resultados.
     * @throws SQLException Si ocurre un error durante la consulta a la base de
     * datos.
     */
    public ArrayList<Articulo> obtenerTodos(int limite, int offset) throws SQLException {
        ArrayList<Articulo> lista = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_ALL_PAGINADO);
            ps.setInt(1, limite);
            ps.setInt(2, offset);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearArticulo(rs));
            }
        } catch (SQLException e) {
            // Logueamos el CONTEXTO de la búsqueda, no el objeto
            System.err.println("Error al obtener la lista de artículos (Límite: " + limite + ", Offset: " + offset + "): " + e.getMessage());
            // Lanzamos la excepción al Servlet
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return lista;
    }

    /**
     * Recupera una lista paginada de artículos filtrada por una categoría
     * específica.
     *
     * * @param categoria La categoría a filtrar (ej: "NOTICIA", "GUIA" o
     * "TORNEO").
     * @param limite El número máximo de artículos a recuperar.
     * @param offset El número de artículos a ignorar desde el inicio.
     * @return Un ArrayList con los artículos que coinciden con la categoría
     * indicada.
     * @throws SQLException Si ocurre un error durante la consulta a la base de
     * datos.
     */
    public ArrayList<Articulo> obtenerPorCategoria(String categoria, int limite, int offset) throws SQLException {
        ArrayList<Articulo> lista = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_BY_CATEGORIA_PAGINADO);
            ps.setString(1, categoria);
            ps.setInt(2, limite);
            ps.setInt(3, offset);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearArticulo(rs));
            }
        } catch (SQLException e) {
            // Logueamos el CONTEXTO de la búsqueda, no el objeto
            System.err.println("Error al obtener la lista de artículos (Categoría: " + categoria + ", Límite: " + limite + ", Offset: " + offset + "): " + e.getMessage());
            // Lanzamos la excepción al Servlet
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return lista;
    }

    /**
     * Recupera un artículo específico de la base de datos utilizando su
     * identificador único. Ideal para cargar la vista de detalle de un artículo
     * completo.
     *
     * * @param idArticulo El ID numérico del artículo a buscar.
     * @return El objeto Articulo si se encuentra; null si no existe ningún
     * artículo con ese ID.
     * @throws SQLException Si ocurre un error durante la consulta a la base de
     * datos.
     */
    public Articulo obtenerPorId(int idArticulo) throws SQLException {
        Articulo articulo = null;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, idArticulo);
            rs = ps.executeQuery();

            if (rs.next()) {
                articulo = mapearArticulo(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener artículo con ID " + idArticulo + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return articulo;
    }

    /**
     * Cuenta el número total de artículos almacenados en la base de datos. Dato
     * esencial para poder calcular matemáticamente el número total de páginas
     * en el frontend.
     *
     * * @return El número total de artículos existentes.
     * @throws SQLException Si ocurre un error durante el recuento en la base de
     * datos.
     */
    public int contarTodos() throws SQLException {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_COUNT_ALL);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            // Logueamos el CONTEXTO de la búsqueda, no el objeto
            System.err.println("Error al contar la lista de artículos (contados: " + total + " ): " + e.getMessage());
            // Lanzamos la excepción al Servlet
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return total;
    }

    /**
     * Cuenta el número total de artículos que pertenecen a una categoría
     * específica.
     *
     * * @param categoria La categoría a contar (ej: "NOTICIA", "GUIA" o
     * "TORNEO").
     * @return El número total de artículos de esa categoría en concreto.
     * @throws SQLException Si ocurre un error durante el recuento en la base de
     * datos.
     */
    public int contarPorCategoria(String categoria) throws SQLException {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_COUNT_BY_CATEGORIA);
            ps.setString(1, categoria);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            // Logueamos el CONTEXTO de la búsqueda, no el objeto
            System.err.println("Error al contar la lista de artículos (Categoría: " + categoria + ", contados: " + total + " ): " + e.getMessage());
            // Lanzamos la excepción al Servlet
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return total;
    }

    /**
     * Actualiza los datos de un artículo existente en la base de datos. Nota:
     * La 'fecha_edicion' se actualiza automáticamente mediante las reglas del
     * motor SQL.
     *
     * * @param a El objeto Articulo con los datos modificados y su ID original
     * intacto.
     * @return true si la actualización modificó la base de datos con éxito;
     * false en caso contrario.
     * @throws SQLException Si ocurre un error de acceso a la base de datos o en
     * la instrucción de actualización.
     */
    public boolean actualizarArticulo(Articulo a) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(SQL_UPDATE);
            ps.setString(1, a.getTitulo());
            ps.setString(2, a.getDescripcion());
            ps.setString(3, a.getCategoria());
            ps.setString(4, a.getEnlaceExterno());
            ps.setString(5, a.getPortada());
            ps.setInt(6, a.getIdArticulo());

            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el artículo ID " + a.getIdArticulo() + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Elimina de forma permanente un artículo de la base de datos mediante su
     * identificador.
     *
     * * @param idArticulo El ID numérico del artículo que se desea eliminar.
     * @return true si el artículo fue eliminado exitosamente; false si no se
     * encontró.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    public boolean borrarArticulo(int idArticulo) throws SQLException {
        boolean exito = false;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(SQL_DELETE);
            ps.setInt(1, idArticulo);
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al borrar el artículo ID " + idArticulo + ": " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, null);
        }
        return exito;
    }

    /**
     * Realiza una búsqueda de artículos donde el título o la descripción
     * contengan la palabra clave introducida. Los resultados se devuelven
     * paginados y ordenados por fecha descendente.
     *
     * * @param keyword La palabra o frase a buscar dentro de los campos de
     * texto del artículo.
     * @param limite El número máximo de artículos a recuperar.
     * @param offset El número de artículos a ignorar desde el inicio.
     * @return Un ArrayList con los artículos que contienen la palabra clave.
     * @throws SQLException Si ocurre un error durante la búsqueda en la base de
     * datos.
     */
    public ArrayList<Articulo> buscarArticulos(String keyword, int limite, int offset) throws SQLException {
        ArrayList<Articulo> lista = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SEARCH_PAGINADO);
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setInt(3, limite);
            ps.setInt(4, offset);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearArticulo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en búsqueda de artículos (keyword: " + keyword + "): " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return lista;
    }

    /**
     * Cuenta el número total de artículos que coinciden con una palabra clave
     * en su título o descripción. Obligatorio para implementar correctamente
     * los botones de paginación tras una búsqueda.
     *
     * * @param keyword La palabra o frase buscada.
     * @return El número total de resultados que coinciden con la búsqueda.
     * @throws SQLException Si ocurre un error durante el recuento.
     */
    public int contarPorBusqueda(String keyword) throws SQLException {
        int total = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_COUNT_SEARCH);
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar resultados de búsqueda: " + e.getMessage());
            throw e;
        } finally {
            cerrarRecursos(con, ps, rs);
        }
        return total;
    }

    /**
     * Método auxiliar que transforma la fila actual de un ResultSet en un
     * objeto Articulo. Centraliza el mapeo relacional-objeto (ORM manual) para
     * evitar código duplicado.
     *
     * * @param rs El ResultSet posicionado en la fila actual que se desea
     * mapear.
     * @return Un objeto Articulo poblado con los datos extraídos de la fila.
     * @throws SQLException Si ocurre un error al extraer los datos de la
     * columna.
     */
    private Articulo mapearArticulo(ResultSet rs) throws SQLException {
        Articulo a = new Articulo();
        a.setIdArticulo(rs.getInt("id_articulo"));
        a.setTitulo(rs.getString("titulo"));
        a.setDescripcion(rs.getString("descripcion"));
        a.setCategoria(rs.getString("categoria"));
        a.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        a.setFechaEdicion(rs.getTimestamp("fecha_edicion"));
        a.setEnlaceExterno(rs.getString("enlace_externo"));
        a.setPortada(rs.getString("portada"));
        a.setIdAdminAutor(rs.getInt("id_admin_autor"));
        return a;
    }

    /**
     * Método auxiliar para cerrar de forma segura los recursos de JDBC.
     * Intercepta posibles excepciones de cierre para garantizar que la conexión
     * siempre sea devuelta al ConnectionPool, previniendo fugas de memoria y
     * caídas del servidor.
     *
     * * @param con La conexión a la base de datos a liberar.
     * @param ps El PreparedStatement a cerrar.
     * @param rs El ResultSet a cerrar.
     */
    private void cerrarRecursos(Connection con, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement: " + e.getMessage());
            }
        }
        if (con != null) {
            ConnectionPool.getInstance().freeConnection(con);
        }
    }
}
