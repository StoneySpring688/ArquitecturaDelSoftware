package SegundUM.Productos;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import SegundUM.Productos.controller.ControllerProductos;
import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.rest.App;

@Deprecated(forRemoval = true, since = "fix_BrokenEndPoints")
public class ProgramaPruebaProductos {

    // Inicializamos el Logger
    private static final Logger logger = LoggerFactory.getLogger(ProgramaPruebaProductos.class);

    public static void main(String[] args) {
    	
    	// iniciar contexto del main de SpringBoot
    	ConfigurableApplicationContext contexto =
    			SpringApplication.run(App.class, args);
    	
        logger.info("=== INICIO DE PRUEBAS DEL MICROSERVICIO DE PRODUCTOS ===");

        // 1. Instanciamos el Controlador
        ControllerProductos controller = contexto.getBean(ControllerProductos.class);

        // ----------------------------------------------------------------
        // PASO 1: Carga Masiva de Categorías
        // ----------------------------------------------------------------
        logger.info("--- 1. Inicializando Sistema (Cargando Categorías) ---");
        int totalCargados = controller.cargarTodasLasCategorias(); //
        
        if (totalCargados == 0) {
            logger.warn("⚠️ ALERTA: No se cargaron categorías. Las pruebas siguientes podrían fallar.");
        } else {
            logger.info("✅ Sistema inicializado con {} ficheros de categorías.", totalCargados);
        }

        // ----------------------------------------------------------------
        // PASO 2: Alta de Producto
        // ----------------------------------------------------------------
        logger.info("--- 2. Publicando Producto ---");
        
        String nombreCategoriaBuscada = "Arte y ocio"; 
        String idCat = controller.buscarIdCategoria(nombreCategoriaBuscada);
        
        String vendedorId = "user-juan-123";
        
        String idProducto = controller.darAltaProducto( //
            "Caballete de Pintura", 
            "Caballete de madera de haya.", 
            new BigDecimal("50.00"), 
            EstadoProducto.NUEVO, 
            idCat, 
            true, 
            vendedorId
        );

        if (idProducto != null) {
            logger.info("✅ Producto creado exitosamente. ID: {}", idProducto);

            // ----------------------------------------------------------------
            // PASO 3: Prueba de Seguridad (Modificación No Autorizada)
            // ----------------------------------------------------------------
            logger.info("--- 3. Probando Seguridad (Hacker intenta modificar) ---");
            boolean resultadoHacker = controller.modificarProducto( //
                idProducto, 
                "Hacked Description", 
                BigDecimal.ZERO, 
                "hacker-id" // Usuario incorrecto
            );

            if (!resultadoHacker) {
                logger.info("✅ Seguridad correcta: El controlador rechazó la modificación no autorizada.");
            } else {
                logger.error("❌ FALLO DE SEGURIDAD: El controlador permitió la modificación.");
            }

            // ----------------------------------------------------------------
            // PASO 4: Modificación Autorizada (Dueño)
            // ----------------------------------------------------------------
            logger.info("--- 4. Modificación Autorizada (Dueño) ---");
            boolean resultadoDueño = controller.modificarProducto(
                idProducto, 
                "Caballete de madera (Rebajado)", 
                new BigDecimal("40.00"), 
                vendedorId // Usuario correcto
            );
            
            if (resultadoDueño) {
                logger.info("✅ Modificación realizada correctamente por el dueño.");
            } else {
                logger.error("❌ Error: No se pudo modificar el producto siendo el dueño.");
            }

            // ----------------------------------------------------------------
            // PASO 5: Asociar Lugar de Recogida
            // ----------------------------------------------------------------
            logger.info("--- 5. Asociando lugar de recogida ---");
            boolean recogidaAsignada = controller.asociarLugarRecogida( //
                idProducto, 
                "Plaza Mayor, Madrid", 
                40.4168, 
                -3.7038
            );
            if (recogidaAsignada) logger.info("✅ Lugar de recogida asignado.");

            // ----------------------------------------------------------------
            // PASO 6: Registrar Visualizaciones
            // ----------------------------------------------------------------
            logger.info("--- 6. Simulando visitas al producto ---");
            controller.registrarVisualizacionProducto(idProducto); //
            controller.registrarVisualizacionProducto(idProducto);
            logger.info("✅ Se han registrado 2 visualizaciones.");

            // ----------------------------------------------------------------
            // PASO 7: Obtener Resumen Mensual
            // ----------------------------------------------------------------
            logger.info("--- 7. Consultando Resumen Mensual ---");
            List<ResumenProducto> resumen = controller.obtenerResumenMensual( //
                java.time.LocalDate.now().getMonthValue(), 
                java.time.LocalDate.now().getYear()
            );
            
            logger.info("📊 Resumen del mes ({} productos encontrados):", resumen.size());
            for (ResumenProducto rp : resumen) {
                logger.info("   -> Producto: {} | Vistas: {}", rp.getTitulo(), rp.getVisualizaciones());
            }
            
            // ----------------------------------------------------------------
            // PASO 8: Buscar Productos con Filtros
            // ----------------------------------------------------------------
            logger.info("--- 8. Buscando productos (Filtro: Precio < 100) ---");
            List<Producto> resultados = controller.buscarProductos( //
                null, 
                null, 
                null, 
                new BigDecimal("100.00")
            );
            logger.info("🔍 Búsqueda completada. {} productos encontrados.", resultados.size());

        } else {
            logger.error("❌ Fallo al crear el producto (Ver logs anteriores del controlador).");
        }
        
        logger.info("=== FIN DE PRUEBAS ===");
        
        // Cerrar el contexto de Spring Boot al finalizar las pruebas
        contexto.close();
    }
}