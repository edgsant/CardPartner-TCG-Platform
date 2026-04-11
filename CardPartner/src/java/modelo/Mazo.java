package modelo;

import java.util.ArrayList;
import java.util.List;

public class Mazo {
    private int id;
    private String nombre;
    private String descripcion;
    private String visibilidad; // "Público" o "Privado"
    private Usuario creador; // El dueño del mazo
    private Carta lider; // La carta principal
    private List<Carta> listaCartas; // Las 50 cartas del mazo

    public Mazo() {
        this.listaCartas = new ArrayList<>(); // Inicializamos la lista vacía
    }

    public Mazo(int id, String nombre, String descripcion, String visibilidad, Usuario creador, Carta lider) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.visibilidad = visibilidad;
        this.creador = creador;
        this.lider = lider;
        this.listaCartas = new ArrayList<>();
    }

    // Método de utilidad para añadir una carta fácilmente
    public void añadirCarta(Carta c) {
        if(this.listaCartas.size() < 50) {
            this.listaCartas.add(c);
        }
    }

    // --- GETTERS Y SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getVisibilidad() { return visibilidad; }
    public void setVisibilidad(String visibilidad) { this.visibilidad = visibilidad; }

    public Usuario getCreador() { return creador; }
    public void setCreador(Usuario creador) { this.creador = creador; }

    public Carta getLider() { return lider; }
    public void setLider(Carta lider) { this.lider = lider; }

    public List<Carta> getListaCartas() { return listaCartas; }
    public void setListaCartas(List<Carta> listaCartas) { this.listaCartas = listaCartas; }
}