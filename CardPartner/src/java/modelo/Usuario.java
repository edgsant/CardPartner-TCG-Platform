package modelo;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;

public class Usuario implements Serializable {
    
    private int idUsuario;
    private String apodoUsuario;
    private String email;
    private String password;
    private InputStream avatar; 
    private String rol;
    private Timestamp fechaRegistro;

    // Constructor vacío obligatorio
    public Usuario() {
        this.apodoUsuario = "";
        this.email = "";
        this.password = "";
        this.rol = "JUGADOR"; // Valor por defecto
    }

    // Constructor completo
    public Usuario(int idUsuario, String apodoUsuario, String email, String password, InputStream avatar, String rol, Timestamp fechaRegistro) {
        this.idUsuario = idUsuario;
        this.apodoUsuario = apodoUsuario;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getApodoUsuario() {
        return apodoUsuario;
    }

    public void setApodoUsuario(String apodoUsuario) {
        this.apodoUsuario = apodoUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public InputStream getAvatar() {
        return avatar;
    }

    public void setAvatar(InputStream avatar) {
        this.avatar = avatar;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setNombreUsuario(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setId(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
}