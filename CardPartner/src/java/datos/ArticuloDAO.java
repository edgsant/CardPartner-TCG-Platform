package datos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Articulo;

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
     * Inserta un nuevo artículo y devuelve su ID autogenerado. Devuelve 0 si
     * hubo un error.
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
     * Devuelve una lista paginada con todos los artículos.
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
     * Devuelve una lista paginada de artículos filtrada por categoría
     * ("NOTICIA", "GUIA", "TORNEO").
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
     * Busca un artículo concreto por su ID (para cuando haces clic en "Leer
     * más").
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
     * Devuelve el total de artículos en la base de datos (para calcular
     * páginas).
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
     * Devuelve el total de artículos de una categoría específica.
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
     * Actualiza los datos de un artículo existente.
     *
     * @return true si se actualizó correctamente.
     * @throws SQLException si hay error en la base de datos.
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
     * Elimina un artículo de la base de datos.
     *
     * @return true si se eliminó correctamente.
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
     * Busca artículos por título o descripción con soporte para paginación.
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
     * Cuenta el total de resultados para una búsqueda (necesario para la
     * paginación del buscador).
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
     * Método auxiliar privado para no repetir código. Convierte una fila de
     * MariaDB en un objeto Articulo.
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
     * Método auxiliar para cerrar recursos de forma segura. Garantiza que la
     * conexión se libere incluso si ResultSet o Statement fallan.
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
