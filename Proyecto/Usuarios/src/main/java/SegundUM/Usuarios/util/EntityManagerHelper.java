package SegundUM.Usuarios.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper para gestionar EntityManager de JPA.
 *
 * Las propiedades de conexion se pueden sobreescribir con variables de entorno:
 *   DB_URL      -> javax.persistence.jdbc.url
 *   DB_USER     -> javax.persistence.jdbc.user
 *   DB_PASSWORD  -> javax.persistence.jdbc.password
 */
public class EntityManagerHelper {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerHelper.class);

    private static final String PERSISTENCE_UNIT_NAME = "segundumUsuarios";
    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();

    static {
        try {
            Map<String, String> overrides = new HashMap<>();
            if (System.getenv("DB_URL") != null) {
                overrides.put("javax.persistence.jdbc.url", System.getenv("DB_URL"));
            }
            if (System.getenv("DB_USER") != null) {
                overrides.put("javax.persistence.jdbc.user", System.getenv("DB_USER"));
            }
            if (System.getenv("DB_PASSWORD") != null) {
                overrides.put("javax.persistence.jdbc.password", System.getenv("DB_PASSWORD"));
            }
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, overrides);
        } catch (Exception e) {
            logger.error("Fallo al crear EntityManagerFactory", e);
            throw new ExceptionInInitializerError("Fallo al crear EntityManagerFactory");
        }
    }
    
    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
            threadLocal.set(em);
        }
        return em;
    }
    
    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
            threadLocal.set(null);
        }
    }
    
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}