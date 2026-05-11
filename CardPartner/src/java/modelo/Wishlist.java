package modelo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Wishlist implements Serializable {

    private int idUsuario;
    private String idCarta;
    private Timestamp fechaAñadido;
    private Carta carta;

    public Wishlist() {
    }

    public Wishlist(int idUsuario, String idCarta, Timestamp fechaAñadido) {
        this.idUsuario = idUsuario;
        this.idCarta = idCarta;
        this.fechaAñadido = fechaAñadido;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdCarta() {
        return idCarta;
    }

    public void setIdCarta(String idCarta) {
        this.idCarta = idCarta;
    }

    public Timestamp getFechaAñadido() {
        return fechaAñadido;
    }

    public void setFechaAñadido(Timestamp fechaAñadido) {
        this.fechaAñadido = fechaAñadido;
    }

    public Carta getCarta() {
        return carta;
    }

    public void setCarta(Carta carta) {
        this.carta = carta;
    }

}
