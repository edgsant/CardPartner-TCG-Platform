package modelo;

public class Carta {
    private String idCarta; // Ejemplo: "OP01-120"
    private String nombre;
    private String rareza;
    private String color;
    private String tipo; // Líder, Personaje, Evento...
    private int coste;
    private int poder;
    private String efecto;
    private String imagenUrl; // Ruta de la imagen, ej: "/img/op01-120.jpg"

    public Carta() {
    }

    public Carta(String idCarta, String nombre, String rareza, String color, String tipo, int coste, int poder, String efecto, String imagenUrl) {
        this.idCarta = idCarta;
        this.nombre = nombre;
        this.rareza = rareza;
        this.color = color;
        this.tipo = tipo;
        this.coste = coste;
        this.poder = poder;
        this.efecto = efecto;
        this.imagenUrl = imagenUrl;
    }

    // --- GETTERS Y SETTERS ---
    public String getIdCarta() { return idCarta; }
    public void setIdCarta(String idCarta) { this.idCarta = idCarta; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRareza() { return rareza; }
    public void setRareza(String rareza) { this.rareza = rareza; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCoste() { return coste; }
    public void setCoste(int coste) { this.coste = coste; }

    public int getPoder() { return poder; }
    public void setPoder(int poder) { this.poder = poder; }

    public String getEfecto() { return efecto; }
    public void setEfecto(String efecto) { this.efecto = efecto; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}