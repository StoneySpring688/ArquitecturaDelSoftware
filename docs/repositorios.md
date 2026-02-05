# Paquete Repositorio
las clases de este paquete buscan abstraer del sistema de persistencia mediante la inversión de dependencias.

## Interfaz Repositorio
Es el contrato que tendrá que seguir cualquier repositorio independientemente del tipo de persistencia, cubre las operaciones básicas para persistir información.
```java
public interface Repositorio<T, K> {
    
    K add(T entity) throws RepositorioException;
    
    void update(T entity) throws RepositorioException, EntidadNoEncontrada;
    
    void delete(T entity) throws RepositorioException, EntidadNoEncontrada;
    
    T getById(K id) throws RepositorioException, EntidadNoEncontrada;
    
    List<T> getAll() throws RepositorioException;
    
    List<K> getIds() throws RepositorioException;
}
```

### Concreciones del repositorio
Son interfaces que establecen que el contrato definido en la interfáz **Repositorio** debe realizarse con alguna restricción, como puede ser que la clave de la entidad sea un String o un Integer.

Ejemplo:
```java
public interface RepositorioString<T> extends Repositorio<T, String> {
}
```

### Implementaciones concretas del repositorio
Implementan la funcionalidad definida en el contrato de la interfaz **Repositorio** y son dependientes del sistema de persistencia, ya sea JPA o cualquier otro.

Ejemplo:
```java
public abstract class RepositorioJPA<T extends Identificable> implements RepositorioString<T> {
    
    public abstract Class<T> getClase();

    @Override
    public String add(T entity) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RepositorioException("Error al guardar la entidad", e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            EntityManagerHelper.closeEntityManager();
        }
        return entity.getId();
    }

    @Override
    public void update(T entity) throws RepositorioException, EntidadNoEncontrada {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            em.getTransaction().begin();
            
            T instancia = em.find(getClase(), entity.getId());
            if (instancia == null) {
                throw new EntidadNoEncontrada(entity.getId() + " no existe en el repositorio");
            }
            entity = em.merge(entity);          
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            throw new RepositorioException("Error al actualizar la entidad con id " + entity.getId(), e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public void delete(T entity) throws RepositorioException, EntidadNoEncontrada {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            em.getTransaction().begin();
            T instancia = em.find(getClase(), entity.getId());
            if (instancia == null) {
                throw new EntidadNoEncontrada(entity.getId() + " no existe en el repositorio");
            }
            em.remove(instancia);
            
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            throw new RepositorioException("Error al borrar la entidad con id " + entity.getId(), e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public T getById(String id) throws EntidadNoEncontrada, RepositorioException {
        try {   
            EntityManager em = EntityManagerHelper.getEntityManager();
                
            T instancia = em.find(getClase(), id);
            // Se hace el refresh para asegurarnos de que los datos vienen actualizados de la cache
            // sino podrían venir sucios de la cache aunque no tiene por qué.
            if (instancia == null) {
                throw new EntidadNoEncontrada(id + " no existe en el repositorio");             
            } else {
                em.refresh(instancia);
            }
            return instancia;

        } catch (RuntimeException e) {
            throw new RepositorioException("Error al recuperar la entidad con id " + id, e);
        }
        finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public List<T> getAll() throws RepositorioException {
        try {
            EntityManager em = EntityManagerHelper.getEntityManager();
        
            final String queryString = " SELECT t from " + getClase().getSimpleName() + " t ";

            TypedQuery<T> query = em.createQuery(queryString, getClase());

            query.setHint(QueryHints.REFRESH, HintValues.TRUE);

            return query.getResultList();

        } catch (RuntimeException e) {

            throw new RepositorioException("Error buscando todas las entidades de " + getClase().getSimpleName(), e);

        }
        finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getIds() throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            final String queryString = " SELECT t.id from " + getClase().getSimpleName() + " t ";

            Query query = em.createQuery(queryString);

            query.setHint(QueryHints.REFRESH, HintValues.TRUE);

            return query.getResultList();

        } catch (RuntimeException e) {

            throw new RepositorioException("Error buscando todos los ids de " + getClase().getSimpleName(), e);

        }
        finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
}
```

### Solucion para tests
Para probar la funcionalidad en tests se puede realizar un repositorio que no utilice un sistema de persistencia, si no que almacene los datos en memoria.

Ejemplo :
```java
public class RepositorioMemoria<T extends Identificable> implements RepositorioString<T> {
    private HashMap<String, T> entidades = new HashMap<>();

    private int id = 1;
    @Override
    public String add(T entity) {
        String id = String.valueOf(this.id++);
        entity.setId(id);
        this.entidades.put(id, entity);
        return id;
    }
    // los demás métodos también en memoria
```
```java
public class RepositorioEncuestasMemoria extends RepositorioMemoria<Encuesta> {
    public RepositorioEncuestasMemoria() {
        // Datos iniciales para pruebas
        Encuesta encuesta1 = // ...
        this.add(encuesta1);
    }
}
```

### Entity manager helper
Para facilitar ls gestión del EntityManager hay una implementación de un helper dentro del paquete **util**:
```java
public class EntityManagerHelper {
    
    private static final String PERSISTENCE_UNIT_NAME = "segundumUsuarios";
    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();
    
    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
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
```

## Inversión de dependencias
Se consigue mediante dos elementos clave:

### Factoria de repositorios
Permite obtener repositorios de la siguiente forma:
```java
Repositorio<Encuesta, String> repositorio = FactoriaRepositorios.getRepositorio(Encuesta.class);
```
Lo cual permite que se pueda desacoplar el dominio y la lógica de los servicios del sistema de persistencia.

Una implementación de factoria:
```java
public class FactoriaRepositorios {
	
	private static final String PROPERTIES = "repositorios.properties";
	
	
	@SuppressWarnings("unchecked")
	public static <T, K, R extends Repositorio<T, K>> R getRepositorio(Class<?> entidad) {
				
			
			try {				
					PropertiesReader properties = new PropertiesReader(PROPERTIES);		
					String clase = properties.getProperty(entidad.getName());
					return (R) Class.forName(clase).getConstructor().newInstance();
			}
			catch (Exception e) {
				
				e.printStackTrace(); // útil para depuración
				
				throw new RuntimeException("No se ha podido obtener el repositorio para la entidad: " + entidad.getName());
			}
			
	}
	
}
```

### Fichero de configuración
El otro elemento clave es el fichero de configuración, que permite decidir el tipo de persistencia a usar para una entidad concreta, sin necesidad de tocar el código.
Este fichero se encuentra en el directorio de **resources**.
```properties
SegundUM.Productos.dominio.Categoria=SegundUM.Productos.repositorio.categorias.RepositorioCategoriasJPA
SegundUM.Productos.dominio.Producto=SegundUM.Productos.repositorio.productos.RepositorioProductosJPA
```

### Properties Reader
Para evitar repetir la lógica de la lectura de los archivos de cofiguración, se proporciona la implementación de un helper.
```java
public class PropertiesReader {
    private Properties properties;

    public PropertiesReader(String propertyFileName) throws IOException {
        InputStream is = getClass().getClassLoader()
            .getResourceAsStream(propertyFileName);
        this.properties = new Properties();
        this.properties.load(is);
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }
}
```

## Repositorios AdHoc
Los repositorios específicos para un tipo de entidad, impementan operaciones concretas relativas a una entidad concreta.

 Por ejemplo obtener productos por vendedor en el repositorio adHoc de productos.