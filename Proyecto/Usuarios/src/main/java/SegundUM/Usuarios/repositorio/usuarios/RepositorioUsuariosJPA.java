package SegundUM.Usuarios.repositorio.usuarios;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import SegundUM.Usuarios.dominio.Usuario;
import SegundUM.Usuarios.repositorio.EntidadNoEncontrada;
import SegundUM.Usuarios.repositorio.RepositorioException;
import SegundUM.Usuarios.repositorio.RepositorioJPA;
import SegundUM.Usuarios.util.EntityManagerHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementación JPA del repositorio de usuarios.
 */
public class RepositorioUsuariosJPA extends RepositorioJPA<Usuario> implements RepositorioUsuarios {

    private static final Logger logger = LoggerFactory.getLogger(RepositorioUsuariosJPA.class);

    @Override
    public Class<Usuario> getClase() {
        return Usuario.class;
    }
    
    @Override
    public Usuario getByEmail(String email) throws RepositorioException, EntidadNoEncontrada {
        logger.debug("Buscando usuario por email: {}", email);
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email", 
                Usuario.class
            );
            query.setParameter("email", email);
            
            List<Usuario> usuarios = query.getResultList();
            
            if (usuarios.isEmpty()) {
                throw new EntidadNoEncontrada("Usuario con email " + email + " no encontrado");
            }
            
            return usuarios.get(0);
        } catch (EntidadNoEncontrada e) {
            throw e;
        } catch (Exception e) {
            throw new RepositorioException("Error al buscar usuario por email " + email, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public boolean existeEmail(String email) throws RepositorioException {
        logger.debug("Verificando existencia de email: {}", email);
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", 
                Long.class
            );
            query.setParameter("email", email);
            
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RepositorioException("Error al verificar existencia de email " + email, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
        }

        @Override
        public Usuario getByIdGitHub(String idGitHub) throws RepositorioException, EntidadNoEncontrada {
        logger.debug("Buscando usuario por GitHub ID: {}", idGitHub);
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.idGitHub = :idGitHub", 
                Usuario.class
            );
            query.setParameter("idGitHub", idGitHub);

            List<Usuario> usuarios = query.getResultList();

            if (usuarios.isEmpty()) {
                throw new EntidadNoEncontrada("Usuario con idGitHub " + idGitHub + " no encontrado");
            }

            return usuarios.get(0);
        } catch (EntidadNoEncontrada e) {
            throw e;
        } catch (Exception e) {
            throw new RepositorioException("Error al buscar usuario por idGitHub " + idGitHub, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
        }
        }