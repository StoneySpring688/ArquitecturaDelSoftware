package SegundUM.Usuarios;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import SegundUM.Usuarios.controller.ControllerUsuarios;
import SegundUM.Usuarios.dominio.Usuario;

public class ProgramaPruebaUsuarios {

    private static final Logger logger = LoggerFactory.getLogger(ProgramaPruebaUsuarios.class);

    public static void main(String[] args) {
        logger.info("=== INICIO PRUEBAS MICROSERVICIO USUARIOS ===");

        ControllerUsuarios controller = new ControllerUsuarios();

        // DATOS DE PRUEBA
        String email = "lucy.garcia@email.com";
        String pass = "secreto123";

        // ---------------------------------------------------------------
        // 1. REGISTRO DE USUARIO
        // ---------------------------------------------------------------
        logger.info("\n--- 1. Prueba de Registro ---");
        String idUsuario = controller.registrarUsuario(
            email, "Lucy", "García", pass, 
            LocalDate.of(1995, 5, 20), "666111222"
        );

        if (idUsuario != null) {
            logger.info("✅ Usuario registrado con ID: {}", idUsuario);
        } else {
            logger.error("❌ Falló el registro.");
            return; // Si falla esto, no seguimos
        }

        // ---------------------------------------------------------------
        // 2. PRUEBA DE DUPLICADOS (Debe fallar)
        // ---------------------------------------------------------------
        logger.info("\n--- 2. Prueba de Email Duplicado ---");
        String idDuplicado = controller.registrarUsuario(
            email, "Impostor", "García", "otraClave", LocalDate.now(), null
        );
        
        if (idDuplicado == null) {
            logger.info("✅ Correcto: El sistema bloqueó el email duplicado.");
        } else {
            logger.error("❌ Error: Se permitió registrar un email duplicado.");
        }

        // ---------------------------------------------------------------
        // 3. LOGIN INCORRECTO
        // ---------------------------------------------------------------
        logger.info("\n--- 3. Prueba Login Incorrecto ---");
        Usuario uFail = controller.login(email, "clave_falsa");
        if (uFail == null) {
            logger.info("✅ Correcto: Login rechazado con clave errónea.");
        }

        // ---------------------------------------------------------------
        // 4. LOGIN CORRECTO
        // ---------------------------------------------------------------
        logger.info("\n--- 4. Prueba Login Correcto ---");
        Usuario uLogueado = controller.login(email, pass);
        if (uLogueado != null) {
            logger.info("✅ Login exitoso. Bienvenido/a {} {}", uLogueado.getNombre(), uLogueado.getApellidos());
        } else {
            logger.error("❌ Error: Login fallido con credenciales correctas.");
        }

        // ---------------------------------------------------------------
        // 5. MODIFICACIÓN DE DATOS
        // ---------------------------------------------------------------
        logger.info("\n--- 5. Prueba Modificación de Datos ---");
        String newpass = "777888999";
        boolean modificado = controller.modificarDatosPersonales(idUsuario, "Laura", "Perez", newpass, LocalDate.of(1995, 5, 20), "686123456");
        
        if (modificado) {
            logger.info("✅ Datos modificados.");

            // Verificamos recuperando el usuario de nuevo (simulando refresco)
            Usuario uActualizado = controller.login(email, newpass);
            if (uActualizado != null) {
                logger.info("   -> Nuevo nombre en BD: {}", uActualizado.getNombre());
                logger.info("   -> Nuevo teléfono en BD: {}", uActualizado.getTelefono());

                if (uActualizado.getNombre().equals("Laura") && "686123456".equals(uActualizado.getTelefono())) {
                    logger.info("✅ Verificación de persistencia correcta.");
                }
            } else {
                logger.error("❌ No se pudo verificar: login con nueva clave falló.");
            }
        } else {
            logger.error("❌ Falló la modificación.");
        }

        logger.info("\n=== FIN DE PRUEBAS ===");
    }
}