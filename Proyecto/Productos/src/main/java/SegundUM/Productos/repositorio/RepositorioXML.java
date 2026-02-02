package SegundUM.Productos.repositorio;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * Esta clase la he porteado desde el proyecto {@link encuestas} así que con <i>id</i> se refiere al nombre del fichero XML.<br>
 * <b>Ejemplo : Electronica.xml</b> es el id que se le pasaría a los métodos para que lean el fichero desde el directorio <i>categoriasXML/</i>.
 * </p>
 * */
public abstract class RepositorioXML<T extends Identificable> implements RepositorioString<T> {
	
	Logger logger = LoggerFactory.getILoggerFactory().getLogger(RepositorioXML.class.getName());

	public final static String DIRECTORIO = "categoriasXML/";

	static {

		File directorio = new File(DIRECTORIO);

		if (!directorio.exists())
			directorio.mkdir();
	}

	// Método abstracto que se delega a los repositorio específicos
	public abstract Class<T> getClase();

	/*** Métodos de apoyo ***/

	protected String getDocumento(String id) {

		if (id.endsWith(".xml")) { // si es un xml concreto
			logger.debug("El id proporcionado es un nombre de fichero XML: " + id);
			return DIRECTORIO + id;
		}
		// Si no, asumimos que es generado por el repositorioXML
		return DIRECTORIO + getClase().getSimpleName() + "-" + id + ".xml";
	}

	protected boolean checkDocumento(String id) {

		final String documento = getDocumento(id);

		File fichero = new File(documento);
		
		logger.debug("Comprobando existencia del fichero: " + documento + " - Existe: " + fichero.exists());

		return fichero.exists();
	}

	protected void save(T entity) throws RepositorioException {

		final String documento = getDocumento(entity.getId());

		final File fichero = new File(documento);

		try {

			JAXBContext contexto = JAXBContext.newInstance(getClase());
			Marshaller marshaller = contexto.createMarshaller();

			marshaller.setProperty("jaxb.formatted.output", true);

			marshaller.marshal(entity, fichero);

		} catch (Exception e) {

			throw new RepositorioException("Error al guardar la entidad con id: " + entity.getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected T load(String id) throws RepositorioException, EntidadNoEncontrada {

		if (!checkDocumento(id))
			throw new EntidadNoEncontrada("La entidad no existe, id: " + id);

		final String documento = getDocumento(id);

		try {

			JAXBContext contexto = JAXBContext.newInstance(getClase());
			Unmarshaller unmarshaller = contexto.createUnmarshaller();

			return (T) unmarshaller.unmarshal(new File(documento));

		} catch (Exception e) {
			throw new RepositorioException("Error al cargar la entidad con id: " + id, e);
		}
	}

	/*** Fin métodos de apoyo ***/

	@Override
	public String add(T entity) throws RepositorioException {
		String id = UUID.randomUUID().toString();

		entity.setId(id);
		save(entity);

		return id;
	}

	@Override
	public void update(T entity) throws RepositorioException, EntidadNoEncontrada {
		if (!checkDocumento(entity.getId()))
			throw new EntidadNoEncontrada("La entidad no existe, id: " + entity.getId());

		save(entity);
	}

	@Override
	public void delete(T entity) throws RepositorioException, EntidadNoEncontrada {
		if (!checkDocumento(entity.getId()))
			throw new EntidadNoEncontrada("La entidad no existe, id: " + entity.getId());

		final String documento = getDocumento(entity.getId());

		File fichero = new File(documento);

		fichero.delete();

	}

	@Override
	public T getById(String id) throws RepositorioException, EntidadNoEncontrada {
		return load(id);
	}

	@Override
	public List<T> getAll() throws RepositorioException {
		LinkedList<T> resultado = new LinkedList<T>();

		for (String id : getIds()) {
			
			try {
				resultado.add(load(id));
			} catch (EntidadNoEncontrada e) {
				// no debería suceder
				e.printStackTrace();
			}
		}
		
		return resultado;
	}

	@Override
	public List<String> getIds() throws RepositorioException {
		LinkedList<String> resultado = new LinkedList<>();

		File directorio = new File(DIRECTORIO);

		File[] entidades = directorio.listFiles(f -> f.isFile() && f.getName().endsWith(".xml"));

		final String prefijo = getClase().getSimpleName() + "-";
		for (File file : entidades) {

			String id = file.getName().substring(prefijo.length(), file.getName().length() - 4);

			resultado.add(id);
		}

		return resultado;
	}

}
