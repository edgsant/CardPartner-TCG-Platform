package modelo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Valoracion implements Serializable {
    private int idUsuario;
    private int idMazo;
    private float puntuacion;
    private Timestamp fechaValoracion;

    public Valoracion() {}

    public Valoracion(int idUsuario, int idMazo, float puntuacion, Timestamp fechaValoracion) {
        this.idUsuario = idUsuario;
        this.idMazo = idMazo;
        this.puntuacion = puntuacion;
        this.fechaValoracion = fechaValoracion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdMazo() {
        return idMazo;
    }

    public void setIdMazo(int idMazo) {
        this.idMazo = idMazo;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Timestamp getFechaValoracion() {
        return fechaValoracion;
    }

    public void setFechaValoracion(Timestamp fechaValoracion) {
        this.fechaValoracion = fechaValoracion;
    }

    
}