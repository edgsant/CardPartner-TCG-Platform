package modelo;

import java.io.Serializable;

public class ContieneCarta implements Serializable {
    private int idMazo;
    private String idCarta;
    private int cantidad;
    private Carta carta;

    public ContieneCarta() {}

    public ContieneCarta(int idMazo, String idCarta, int cantidad) {
        this.idMazo = idMazo;
        this.idCarta = idCarta;
        this.cantidad = cantidad;
    }

    public int getIdMazo() {
        return idMazo;
    }

    public void setIdMazo(int idMazo) {
        this.idMazo = idMazo;
    }

    public String getIdCarta() {
        return idCarta;
    }

    public void setIdCarta(String idCarta) {
        this.idCarta = idCarta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Carta getCarta() {
        return carta;
    }

    public void setCarta(Carta carta) {
        this.carta = carta;
    }

    
    
}