package controladores;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class ActualizadorAutomatico implements ServletContextListener {

    private ScheduledExecutorService planificador;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[SISTEMA] El servidor ha arrancado. Configurando tareas en segundo plano...");
        
        // Creamos un hilo de trabajo independiente para no bloquear la web
        planificador = Executors.newSingleThreadScheduledExecutor();
        
        // Configuramos la tarea:
        // Parámetro 1: Lo que queremos que haga (llamar a tu API)
        // Parámetro 2: Retraso inicial (ej: esperar 1 minuto antes de la primera vez)
        // Parámetro 3: Cada cuánto tiempo se repite (ej: 24)
        // Parámetro 4: Unidad de tiempo (Horas)
        
        // Código nuevo para PRUEBAS: Espera 0 minutos (inmediato), repite cada 1 minuto
        planificador.scheduleAtFixedRate(() -> {
            try {
                System.out.println("[ACTUALIZADOR] Iniciando sincronización de precios y cartas...");
                String resultado = OptcgAPI.sincronizarBaseDeDatos();
                System.out.println("[ACTUALIZADOR] " + resultado);
            } catch (Exception e) {
                System.err.println("[ACTUALIZADOR] Fallo al sincronizar: " + e.getMessage());
            }
        }, 1, 24, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Si apagamos Tomcat, matamos el hilo limpiamente
        if (planificador != null) {
            planificador.shutdownNow();
            System.out.println("[SISTEMA] Tareas en segundo plano detenidas.");
        }
    }
}