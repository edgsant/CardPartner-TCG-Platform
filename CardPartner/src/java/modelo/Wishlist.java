package modelo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Wishlist implements Serializable {
    private int idUsuario;
    private String idCarta;
    private Timestamp fechaAñadido;

    public Wishlist() {}

    // Getters y Setters...
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getIdCarta() { return idCarta; }
    public void setIdCarta(String idCarta) { this.idCarta = idCarta; }
}