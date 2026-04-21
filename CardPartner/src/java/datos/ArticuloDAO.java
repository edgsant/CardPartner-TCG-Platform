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

    /**
     * Inserta un nuevo artículo y devuelve su ID autogenerado.
     * Devuelve 0 si hubo un error.
     */
    public int crearArticulo(Articulo a) {
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
        return idGenerado; 
    }

    /**
     * Devuelve una lista paginada con todos los artículos.
     */
    public ArrayList<Articulo> obtenerTodos(int limite, int offset) {
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
        return lista;
    }

    /**
     * Devuelve una lista paginada de artículos filtrada por categoría ("NOTICIA", "GUIA", "TORNEO").
     */
    public ArrayList<Articulo> obtenerPorCategoria(String categoria, int limite, int offset) {
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
        return lista;
    }

    /**
     * Busca un artículo concreto por su ID (para cuando haces clic en "Leer más").
     */
    public Articulo obtenerPorId(int idArticulo) {
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
        return articulo;
    }

    /**
     * Devuelve el total de artículos en la base de datos (para calcular páginas).
     */
    public int contarTodos() {
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
        return total;
    }

    /**
     * Devuelve el total de artículos de una categoría específica.
     */
    public int contarPorCategoria(String categoria) {
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
        return total;
    }

    /**
     * Método auxiliar privado para no repetir código. 
     * Convierte una fila de MariaDB en un objeto Articulo.
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
}