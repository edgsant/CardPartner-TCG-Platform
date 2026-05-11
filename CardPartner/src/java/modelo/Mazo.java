package modelo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Mazo implements Serializable {

    private int idMazo;
    private String nombreMazo;
    private String descripcion;
    private Timestamp fechaCreacion;
    private boolean esPublico;
    private boolean esOficial;
    private float winrate;
    private int idUsuarioCreador;
    private Usuario usuarioCreador;
    private String idCartaLider;
    private Carta cartaLider;

    public Mazo() {
        this.esPublico = true;
        this.esOficial = false;
        this.winrate = 0.0f;
    }

    public Mazo(int idMazo, String nombreMazo, String descripcion, Timestamp fechaCreacion, boolean esPublico, boolean esOficial, float winrate, int idUsuarioCreador, Usuario usuarioCreador, String idCartaLider, Carta cartaLider) {
        this.idMazo = idMazo;
        this.nombreMazo = nombreMazo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.esPublico = esPublico;
        this.esOficial = esOficial;
        this.winrate = winrate;
        this.idUsuarioCreador = idUsuarioCreador;
        this.usuarioCreador = usuarioCreador;
        this.idCartaLider = idCartaLider;
        this.cartaLider = cartaLider;
    }

    public Carta getCartaLider() {
        return cartaLider;
    }

    public void setCartaLider(Carta cartaLider) {
        this.cartaLider = cartaLider;
    }

    public int getIdMazo() {
        return idMazo;
    }

    public void setIdMazo(int idMazo) {
        this.idMazo = idMazo;
    }

    public String getNombreMazo() {
        return nombreMazo;
    }

    public void setNombreMazo(String nombreMazo) {
        this.nombreMazo = nombreMazo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isEsPublico() {
        return esPublico;
    }

    public void setEsPublico(boolean esPublico) {
        this.esPublico = esPublico;
    }

    public boolean isEsOficial() {
        return esOficial;
    }

    public void setEsOficial(boolean esOficial) {
        this.esOficial = esOficial;
    }

    public float getWinrate() {
        return winrate;
    }

    public void setWinrate(float winrate) {
        this.winrate = winrate;
    }

    public int getIdUsuarioCreador() {
        return idUsuarioCreador;
    }

    public void setIdUsuarioCreador(int idUsuarioCreador) {
        this.idUsuarioCreador = idUsuarioCreador;
    }

    public String getIdCartaLider() {
        return idCartaLider;
    }

    public void setIdCartaLider(String idCartaLider) {
        this.idCartaLider = idCartaLider;
    }

    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Usuario usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

}
