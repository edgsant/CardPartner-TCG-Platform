package datos;


import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Articulo;

public class ArticuloDAO {

    // Constantes SQL
    private static final String SQL_INSERT = "INSERT INTO ARTICULO (titulo, descripcion, categoria, enlace_externo, portada, id_admin_autor) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM ARTICULO ORDER BY fecha_creacion DESC";
    private static final String SQL_SELECT_BY_CATEGORIA = "SELECT * FROM ARTICULO WHERE categoria = ? ORDER BY fecha_creacion DESC";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM ARTICULO WHERE id_articulo = ?";

    /**
     * Inserta un nuevo artículo y devuelve su ID autogenerado.
     * Devuelve 0 si hubo un error.
     */
    public int crearArticulo(Articulo a) {
        int idGenerado = 0;
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null; // Necesitamos esto para leer el ID nuevo

        try {
            // Le indicamos a MariaDB que queremos recuperar la llave generada
            ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, a.getTitulo());
            ps.setString(2, a.getDescripcion());
            ps.setString(3, a.getCategoria());
            ps.setString(4, a.getEnlaceExterno());
            ps.setString(5, a.getPortada());
            ps.setInt(6, a.getIdAdminAutor());

            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Si se insertó bien, capturamos el ID nuevo
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
        return idGenerado; // Devuelve el ID (ej: 5) o 0 si falló
    }
    /**
     * Devuxelve una lista con todos los artículos, ordenados del más nuevo al más viejo.
     */
    public List<Articulo> obtenerTodos() {
        List<Articulo> lista = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_ALL);
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
     * Devuelve los artículos filtrados por su categoría ("NOTICIA", "GUIA", "TORNEO").
     * Ideal para inyectarlos en secciones específicas de tu index.jsp.
     */
    public List<Articulo> obtenerPorCategoria(String categoria) {
        List<Articulo> lista = new ArrayList<>();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(SQL_SELECT_BY_CATEGORIA);
            ps.setString(1, categoria);
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