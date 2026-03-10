package SegundUM.Compraventas.pruebas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import SegundUM.Compraventas.dominio.Compraventa;
import SegundUM.Compraventas.repositorio.compraventa.RepositorioCompraventaMongo;
import SegundUM.Compraventas.rest.App;
import SegundUM.Compraventas.servicio.compraventa.ServicioCompraventa;

public class PruebaConexionMongo implements CommandLineRunner {

    private final ServicioCompraventa servicioCompraventa;
    private final RepositorioCompraventaMongo repositorio;

    // Inyectamos el Servicio (para la lógica) y el Repositorio (solo para el count final)
    @Autowired
    public PruebaConexionMongo(ServicioCompraventa servicioCompraventa, RepositorioCompraventaMongo repositorio) {
        this.servicioCompraventa = servicioCompraventa;
        this.repositorio = repositorio;
    }

    public void run(String... args) throws Exception {
    	
    	// iniciar contexto del main de SpringBoot
    	ConfigurableApplicationContext contexto =
    			SpringApplication.run(App.class, args);
    	
        System.out.println("=======================================================");
        System.out.println("⏳ Iniciando prueba de SERVICIO DE COMPRAVENTA...");
        System.out.println("=======================================================");

        // Los IDs para la prueba
        String idProducto = "063a33cc-356a-48b2-bc21-1db71655b1ca";
        String idComprador = "795a5956-8de7-43a9-b2b0-70ce8c91ae15";
        
        String emailComprador = "lucy.garcia@email.com";
        String claveComprador = "777888999";

        try {
            System.out.println("📡 Iniciando Proceso");
            
            // 1. Ejecutamos la lógica de negocio completa
            Compraventa guardada = servicioCompraventa.realizarCompra(idProducto, idComprador, emailComprador, claveComprador);

            // 2. Imprimimos el resultado por consola
            System.out.println("✅ ¡ÉXITO! Compraventa procesada y guardada.");
            System.out.println("👉 ID Generado en MongoDB : " + guardada.getId());
            System.out.println("👉 Título del Producto  : " + guardada.getTitulo() + " (" + guardada.getPrecio() + "€)");
            System.out.println("👉 Datos del Vendedor   : " + guardada.getNombreVendedor() + " [" + guardada.getIdVendedor() + "]");
            System.out.println("👉 Datos del Comprador  : " + guardada.getNombreComprador() + " [" + guardada.getIdComprador() + "]");

            // 3. Verificamos el conteo en la base de datos
            long totalDocumentos = repositorio.count();
            System.out.println("📊 Total de compraventas en la colección: " + totalDocumentos);

        } catch (Exception e) {
            System.err.println("❌ ERROR al realizar la compraventa.");
            System.err.println("⚠️ IMPORTANTE: ¿Están encendidos los microservicios de Productos (localhost:8080) y Usuarios (localhost:8081)?");
            System.err.println("Motivo del fallo: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=======================================================");
        
        contexto.close();
    }
}