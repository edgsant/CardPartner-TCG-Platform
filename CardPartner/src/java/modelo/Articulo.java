package modelo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Articulo implements Serializable {
    private int idArticulo;
    private String titulo;
    private String descripcion;
    private String categoria; // "NOTICIA", "GUIA" o "TORNEO"
    private Timestamp fechaCreacion;
    private Timestamp fechaEdicion;
    private String enlaceExterno;
    private String portada;
    private int idAdminAutor;

    public Articulo() {}

    public Articulo(int idArticulo, String titulo, String descripcion, String categoria, 
                    Timestamp fechaCreacion, Timestamp fechaEdicion, String enlaceExterno, 
                    String portada, int idAdminAutor) {
        this.idArticulo = idArticulo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fechaCreacion = fechaCreacion;
        this.fechaEdicion = fechaEdicion;
        this.enlaceExterno = enlaceExterno;
        this.portada = portada;
        this.idAdminAutor = idAdminAutor;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Timestamp getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Timestamp fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public String getEnlaceExterno() {
        return enlaceExterno;
    }

    public void setEnlaceExterno(String enlaceExterno) {
        this.enlaceExterno = enlaceExterno;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public int getIdAdminAutor() {
        return idAdminAutor;
    }

    public void setIdAdminAutor(int idAdminAutor) {
        this.idAdminAutor = idAdminAutor;
    }

    
}