package modelo;

import java.io.Serializable;

public class Carta implements Serializable {
    
    private String idCarta;
    private String nombreCarta;
    private String imagen;
    private String tipo;
    private String arquetipo;
    private String color; // Guardará el SET de MariaDB (Ej: "ROJO,VERDE")
    private int coste;
    private int poder;
    private String atributoCombate;
    private double precioMercado;

    public Carta() {
    }

    public Carta(String idCarta, String nombreCarta, String imagen, String tipo, String arquetipo, String color, int coste, int poder, String atributoCombate, double precioMercado) {
        this.idCarta = idCarta;
        this.nombreCarta = nombreCarta;
        this.imagen = imagen;
        this.tipo = tipo;
        this.arquetipo = arquetipo;
        this.color = color;
        this.coste = coste;
        this.poder = poder;
        this.atributoCombate = atributoCombate;
        this.precioMercado = precioMercado;
    }

    public String getIdCarta() {
        return idCarta;
    }

    public void setIdCarta(String idCarta) {
        this.idCarta = idCarta;
    }

    public String getNombreCarta() {
        return nombreCarta;
    }

    public void setNombreCarta(String nombreCarta) {
        this.nombreCarta = nombreCarta;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getArquetipo() {
        return arquetipo;
    }

    public void setArquetipo(String arquetipo) {
        this.arquetipo = arquetipo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCoste() {
        return coste;
    }

    public void setCoste(int coste) {
        this.coste = coste;
    }

    public int getPoder() {
        return poder;
    }

    public void setPoder(int poder) {
        this.poder = poder;
    }

    public String getAtributoCombate() {
        return atributoCombate;
    }

    public void setAtributoCombate(String atributoCombate) {
        this.atributoCombate = atributoCombate;
    }

    public double getPrecioMercado() {
        return precioMercado;
    }

    public void setPrecioMercado(double precioMercado) {
        this.precioMercado = precioMercado;
    }

    
}