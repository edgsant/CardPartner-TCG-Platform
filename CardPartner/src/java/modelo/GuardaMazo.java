package modelo;

import java.io.Serializable;

public class GuardaMazo implements Serializable {
    private int idUsuario;
    private int idMazo;

    public GuardaMazo() {}

    public GuardaMazo(int idUsuario, int idMazo) {
        this.idUsuario = idUsuario;
        this.idMazo = idMazo;
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

    
}