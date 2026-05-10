package controladores;

import datos.CartaDAO;
import modelo.Carta;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class OptcgAPI {

    private static final String API_URL = "https://www.optcgapi.com/api/allSetCards/";

    public static String sincronizarBaseDeDatos() {
        try {
            System.out.println("Iniciando conexión con OPTCG API (Versión Definitiva con Artes Alternativos)...");
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() != 200) {
                return "Error de conexión: HTTP " + conn.getResponseCode();
            }

            // 1. Leer el JSON gigante
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder responseStr = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseStr.append(line);
            }
            br.close();

            // 2. Parsear el texto con GSON
            JsonArray jsonArray = JsonParser.parseString(responseStr.toString()).getAsJsonArray();
            CartaDAO cartaDAO = new CartaDAO();
            int cartasInsertadas = 0;

            // NUEVO: Un "cerebro" temporal para recordar qué cartas hemos visto ya hoy (Artes Alternativos)
            HashMap<String, Integer> conteoCartas = new HashMap<>();

            // 3. Recorrer el Array
            for (JsonElement element : jsonArray) {
                JsonObject jsonCarta = element.getAsJsonObject();

                // Evitar cartas sin ID o valores nulos de la API
                if (!jsonCarta.has("card_set_id") || jsonCarta.get("card_set_id").isJsonNull()) {
                    continue;
                }
                String idBase = jsonCarta.get("card_set_id").getAsString();
                if (idBase.equals("NULL") || idBase.isEmpty()) {
                    continue;
                }

                // Lógica de Artes Alternativos
                int apariciones = conteoCartas.getOrDefault(idBase, 0);
                String idFinal = idBase;
                String nombreOriginal = obtenerTexto(jsonCarta, "card_name", "Desconocido");
                String nombreFinal = nombreOriginal;

                if (apariciones > 0) {
                    // Si ya existe en esta pasada, le ponemos el sufijo "_p1", "_p2", etc.
                    idFinal = idBase + "_p" + apariciones;
                    nombreFinal = nombreOriginal + " (Arte Alternativo " + apariciones + ")";
                }
                
                // Sumamos 1 al contador para esta carta específica
                conteoCartas.put(idBase, apariciones + 1);

                Carta carta = new Carta();
                carta.setIdCarta(idFinal); // Guardamos el ID modificado
                carta.setNombreCarta(nombreFinal); // Guardamos el nombre con el sufijo
                
                carta.setImagen(obtenerTexto(jsonCarta, "card_image", null));
                carta.setArquetipo(obtenerTexto(jsonCarta, "sub_types", null));
                
                // Traducción y mapeo
                carta.setTipo(mapearTipo(obtenerTexto(jsonCarta, "card_type", "")));
                carta.setColor(mapearColor(obtenerTexto(jsonCarta, "card_color", "")));
                carta.setAtributoCarta(mapearAtributo(obtenerTexto(jsonCarta, "attribute", "")));

                // Conversión segura de números
                carta.setCoste(obtenerEnteroSeguro(obtenerTexto(jsonCarta, "card_cost", "0")));
                carta.setPoder(obtenerEnteroSeguro(obtenerTexto(jsonCarta, "card_power", "0")));
                carta.setPrecio((float) obtenerNumero(jsonCarta, "market_price", 0.0));

                // 4. Guardar en Base de Datos
                cartaDAO.insertarOActualizarCarta(carta);
                cartasInsertadas++;
            }

            return "¡Éxito! Se han sincronizado " + cartasInsertadas + " cartas desde la API incluyendo Artes Alternativos.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error crítico durante la sincronización: " + e.getMessage();
        }
    }

    // ==========================================
    // ESCUDOS ANTIMISSILES (Métodos de Seguridad)
    // ==========================================
    
    // Extrae texto de forma segura comprobando que no sea "null" literal
    private static String obtenerTexto(JsonObject json, String clave, String valorPorDefecto) {
        if (json.has(clave) && !json.get(clave).isJsonNull()) {
            return json.get(clave).getAsString();
        }
        return valorPorDefecto;
    }

    // Extrae números con decimales de forma segura
    private static double obtenerNumero(JsonObject json, String clave, double valorPorDefecto) {
        if (json.has(clave) && !json.get(clave).isJsonNull()) {
            try { return json.get(clave).getAsDouble(); } 
            catch (Exception e) { return valorPorDefecto; }
        }
        return valorPorDefecto;
    }

    // Evita que el servidor colapse si la API mete letras en los campos de poder o coste
    private static int obtenerEnteroSeguro(String valor) {
        if (valor == null || valor.equals("NULL") || valor.isEmpty()) return 0;
        try {
            // Extrae solo los números
            return Integer.parseInt(valor.replaceAll("[^0-9-]", ""));
        } catch (NumberFormatException e) {
            return 0; 
        }
    }

    // ==========================================
    // MÉTODOS DE TRADUCCIÓN A NUESTRA BD
    // ==========================================

    private static String mapearTipo(String tipoEn) {
        if (tipoEn == null || tipoEn.isEmpty()) return "PERSONAJE";
        return switch (tipoEn.toUpperCase()) {
            case "LEADER" -> "LIDER";
            case "CHARACTER" -> "PERSONAJE";
            case "EVENT" -> "EVENTO";
            case "STAGE" -> "ESCENARIO";
            default -> "PERSONAJE";
        };
    }

    private static String mapearColor(String colorEn) {
        if (colorEn == null || colorEn.equals("NULL") || colorEn.isEmpty()) return "MULTICOLOR";
        
        // Si la carta tiene dos colores (Ej: Red/Green), la definimos como MULTICOLOR
        // Esto evita los avisos de "Data truncated" de MariaDB
        if (colorEn.contains("/")) {
            return "MULTICOLOR";
        }
        
        colorEn = colorEn.toUpperCase()
                .replace("RED", "ROJO")
                .replace("GREEN", "VERDE")
                .replace("BLUE", "AZUL")
                .replace("YELLOW", "AMARILLO")
                .replace("PURPLE", "MORADO")
                .replace("BLACK", "NEGRO")
                .replace(" ", "")
                .trim(); 
                
        return colorEn;
    }

    private static String mapearAtributo(String attrEn) {
        if (attrEn == null || attrEn.equals("NULL") || attrEn.isEmpty()) return null;
        
        // Nos aseguramos de que solo pasen los atributos que tu base de datos conoce
        String upper = attrEn.toUpperCase().trim();
        if (upper.equals("SLASH") || upper.equals("STRIKE") || upper.equals("RANGED") || 
            upper.equals("SPECIAL") || upper.equals("WISDOM")) {
            return upper;
        }
        
        return null; 
    }
}