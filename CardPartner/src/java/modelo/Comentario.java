package modelo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Comentario implements Serializable {
    
    private int idComentario;
    private String contenido;
    private Timestamp fechaCreacion;
    private Timestamp fechaEdicion;
    private Usuario usuarioAutor;
    private int idMazoDestino;

    public Comentario() {
    }

    // Constructor sin fechas (se autogeneran en la BD)
    public Comentario(int idComentario, String contenido, Usuario usuarioAutor, int idMazoDestino) {
        this.idComentario = idComentario;
        this.contenido = contenido;
        this.usuarioAutor = usuarioAutor;
        this.idMazoDestino = idMazoDestino;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
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

    public int getIdMazoDestino() {
        return idMazoDestino;
    }

    public void setIdMazoDestino(int idMazoDestino) {
        this.idMazoDestino = idMazoDestino;
    }

    public Usuario getUsuarioAutor() {
        return usuarioAutor;
    }

    public void setUsuarioAutor(Usuario usuarioAutor) {
        this.usuarioAutor = usuarioAutor;
    }

    
}