package SegundUM.Usuarios.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper para gestionar EntityManager de JPA.
 */
public class EntityManagerHelper {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerHelper.class);

    private static final String PERSISTENCE_UNIT_NAME = "segundumUsuarios";
    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();
    
    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
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