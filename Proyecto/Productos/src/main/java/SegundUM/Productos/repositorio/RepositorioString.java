package SegundUM.Productos.repositorio;

/**
 * Extensión de la interfaz repositorio para concretar
 * el uso de cadenas como identificadores
 */
@Deprecated(since = "SpringData migration", forRemoval = true)
public interface RepositorioString<T> extends Repositorio<T, String> {
}