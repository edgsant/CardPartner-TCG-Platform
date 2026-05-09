package controladores;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeminiAPI {

    // SUSTITUYE ESTO POR TU CLAVE DE GROQ (Empieza por gsk_)
    private static final String API_KEY = "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"; 
    
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public static String traducir(String texto, String tipo) {
        if (texto == null || texto.trim().isEmpty()) return texto;

        try {
            URL url = java.net.URI.create(API_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY.trim()); 
            conn.setDoOutput(true);

            String instruccion = switch (tipo) {
                case "ARTICULO" -> "Translate this news article about TCG to English professionally. ONLY output the translated text: ";
                case "MAZO" -> "Translate this deck strategy to English using TCG jargon (Ramp, Mill, OTK, etc.). ONLY output the translated text: ";
                case "COMENTARIO" -> "Translate this user comment to English naturally. ONLY output the translated text: ";
                default -> "Translate the following text to English. ONLY output the translated text: ";
            };

            // Limpieza ULTRA estricta para evitar el error 400 en Groq
            String textoLimpio = texto.replace("\\", "\\\\")
                                      .replace("\"", "\\\"")
                                      .replace("\n", " ")
                                      .replace("\r", " ")
                                      .replace("\t", " ");

            String prompt = instruccion + textoLimpio;

            // Formato JSON usando el modelo actual más rápido
            String jsonInputString = "{\"model\": \"llama-3.1-8b-instant\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) response.append(line.trim());
                
                return extraerTexto(response.toString());
            } else {
                System.err.println("--- ERROR GROQ API --- HTTP: " + responseCode);
                
                // NUEVO CHIVATO PARA GROQ
                try {
                    BufferedReader errorBr = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorBr.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    System.err.println("Detalle de Groq: " + errorResponse.toString());
                } catch (Exception ex) {
                    System.err.println("No se pudo leer el detalle del error de Groq.");
                }
                
                return texto; 
            }
        } catch (Exception e) {
            System.err.println("Excepción en API: " + e.getMessage());
            return texto;
        }
    }

    private static String extraerTexto(String json) {
        try {
            int posContent = json.indexOf("\"content\":");
            if (posContent == -1) return "Error de lectura de IA.";
            
            int inicioValor = json.indexOf("\"", posContent + 10) + 1;
            int fin = inicioValor;
            while (fin < json.length()) {
                if (json.charAt(fin) == '"' && json.charAt(fin - 1) != '\\') break;
                fin++;
            }
            
            return json.substring(inicioValor, fin).replace("\\n", "<br>").replace("\\\"", "\"");
        } catch (Exception e) { 
            return "Error al procesar JSON de IA"; 
        }
    }
}