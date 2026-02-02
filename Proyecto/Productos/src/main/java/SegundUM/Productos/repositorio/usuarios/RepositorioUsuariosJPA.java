package SegundUM.Productos.repositorio.usuarios;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import SegundUM.Productos.dominio.Usuario;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.RepositorioException;
import SegundUM.Productos.repositorio.RepositorioJPA;
import SegundUM.Productos.utils.EntityManagerHelper;



public class RepositorioUsuariosJPA extends RepositorioJPA<Usuario> implements RepositorioUsuarios {
    
    @Override
    public Class<Usuario> getClase() {
        return Usuario.class;
    }
    
    @Override
    public Usuario getByEmail(String email) throws RepositorioException, EntidadNoEncontrada {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            // Esta query ataca a la tabla 'usuarios_replica' (mapeada en la clase Usuario local)
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email", 
                Usuario.class
            );
            query.setParameter("email", email);
            
            List<Usuario> usuarios = query.getResultList();
            
            if (usuarios.isEmpty()) {
                throw new EntidadNoEncontrada("Usuario réplica no encontrado para el email: " + email);
            }
            
            return usuarios.get(0);
        } catch (EntidadNoEncontrada e) {
            throw e;
        } catch (Exception e) {
            throw new RepositorioException("Error buscando usuario réplica por email", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
}