# Paquete Servicio
Las clases de este paquete y sus subpaquetes implementan la lógica de los casos de uso.

## Inversión de dependencias
Se consigue mediante dos elementos clave:

### Factoria de servicios
Permite obtener servcios de la siguiente forma:
```java
ServicioProductos servicioProductos = FactoriaServicios.getServicio(ServicioProductos.class);
```
Lo cual permite que se pueda desacoplar el dominio y el backend.

Una implementación de factoria:
```java
public class FactoriaServicios {
	
	private static final String PROPERTIES = "servicios.properties";
	
	private static Map<Class<?>, Object> servicios = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static <T> T getServicio(Class<T> servicio) {
				
			
			try {
				if (servicios.containsKey(servicio)) {
					return (T) servicios.get(servicio);
				}
				else {
					PropertiesReader properties = new PropertiesReader(PROPERTIES);			
					String clase = properties.getProperty(servicio.getName());
					
					T servicioInstancia = (T) Class.forName(clase).getConstructor().newInstance();
					servicios.put(servicio, servicioInstancia);
					return servicioInstancia;
				}
				
			}
			catch (Exception e) {
				
				e.printStackTrace(); // útil para depuración
				
				throw new RuntimeException("No se ha podido obtener la implementación del servicio: " + servicio.getName());
			}
			
	}
	
}
```
Esta implementación utiliza una suerte de singleton.

### Fichero de configuración
El otro elemento clave es el fichero de configuración, que permite decidir el backend a usar para una entidad concreta, sin necesidad de tocar el código.
Este fichero se encuentra en el directorio de **resources**.
```properties
SegundUM.Productos.servicio.categorias.ServicioCategorias=SegundUM.Productos.servicio.categorias.ServicioCategoriasImpl
SegundUM.Productos.servicio.productos.ServicioProductos=SegundUM.Productos.servicio.productos.ServicioProductosImpl
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

## Implementaciones de servicios

Se necesita de una interfáz que defina el contrato que debe cumplir la implementación del servicio.

Ejemplo:
```java
public interface ServicioProductos {

    /**
     * Alta de producto. Devuelve id generado.
     */
    String altaProducto(String titulo, String descripcion, BigDecimal precio,
                        EstadoProducto estado, String categoriaId, boolean envioDisponible,
                        String vendedorId) throws ServicioException;

    /**
     * Asigna lugar de recogida al producto.
     */
    void asignarLugarRecogida(String productoId, String descripcion, Double longitud, Double latitud) throws ServicioException;

    /**
     * Modifica precio y/o descripción del producto. Parámetros nulos no se modifican.
     */
    void modificarProducto(String productoId, BigDecimal nuevoPrecio, String nuevaDescripcion) throws ServicioException;

    /**
     * Incrementa en 1 el contador de visualizaciones.
     */
    void anadirVisualizacion(String productoId) throws ServicioException;
    
    /**
     * Modifica precio y/o descripción de un producto.
     * Verifica que el usuario solicitante sea el propietario.
     */
    void modificarProducto(String idProducto, String nuevaDescripcion, BigDecimal nuevoPrecio, String idUsuarioSolicitante) throws ServicioException;

    /**
     * Historial del mes de un vendedor: devuelve resumen ordenado por visualizaciones (desc).
     */
    List<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor) throws ServicioException;
    
    /**
     * Historial del mes de: devuelve resumen ordenado por visualizaciones (desc).
     */
    List<ResumenProducto> historialMes(int mes, int anio) throws ServicioException;

    /**
     * Buscar productos con los criterios opcionales.
     */
    List<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo) throws ServicioException;
    
    /**
     * Recupera los productos publicados por un vendedor específico.
     */
    List<Producto> getProductosPorVendedor(String vendedorId) throws ServicioException;
}
```

Y una implementación que concrete en contrato definido en la interfaz (es posibe tenr varias y cambiar entre ellas con el fichero de configuración).

Ejemplo:
```java
public class ServicioProductosImpl implements ServicioProductos {

	private final Logger logger = LoggerFactory.getLogger(ServicioProductosImpl.class);
	
    private final RepositorioProductos repositorioProductos;
    private final RepositorioCategorias repositorioCategorias;

    public ServicioProductosImpl() {
        this.repositorioProductos = FactoriaRepositorios.getRepositorio(Producto.class);
        this.repositorioCategorias = FactoriaRepositorios.getRepositorio(Categoria.class);
    }

    @Override
    public String altaProducto(String titulo, String descripcion, BigDecimal precio, EstadoProducto estado,
                               String categoriaId, boolean envioDisponible, String vendedorId) throws ServicioException {
        try {
            // VERIFICACIÓN: Obtener categoría y verificar que existe
            Categoria categoria;
            try {
            	logger.info("Obteniendo categoría con ID: " + categoriaId);
                categoria = repositorioCategorias.getById(categoriaId);
            } catch (EntidadNoEncontrada e) {
            	logger.error("Categoría con ID " + categoriaId + " no encontrada", e);
                throw new ServicioException("La categoría con ID " + categoriaId + " no existe en el sistema", e);
            }

            String id = UUID.randomUUID().toString();

            Producto p = new Producto(id, titulo, descripcion, precio, estado, categoria, envioDisponible, vendedorId);

            return repositorioProductos.add(p);
        } catch (RepositorioException e) {
        	logger.error("Error al dar de alta el producto", e);
            throw new ServicioException("Error al dar de alta el producto", e);
        }
    }
    // ...
}
```
