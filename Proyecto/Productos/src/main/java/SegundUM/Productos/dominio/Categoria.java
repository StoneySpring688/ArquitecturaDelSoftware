package SegundUM.Productos.dominio;

import javax.persistence.*;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import SegundUM.Productos.repositorio.Identificable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@XmlRootElement(name = "categoria")
@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria implements Identificable {

	@Id
	@XmlAttribute
	private String id;

	@Column(nullable = false)
	@XmlElement
	private String nombre;

	@Column(length = 1000)
	@XmlElement
	private String descripcion;

	@Lob
	@XmlAttribute
	private String ruta;

	@OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL, orphanRemoval = true)
	@XmlElement(name = "categoria")
	@JsonIgnore
	private List<Categoria> subcategorias = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "categoria_padre_id")
	@XmlTransient
	@JsonIgnore
	private Categoria categoriaPadre;

	@OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
	@XmlTransient
	@JsonIgnore
	private List<Producto> productos = new ArrayList<>();

	// Constructor por defecto para JPA y JAXB
	protected Categoria() {}

	public Categoria(String id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	// se supone que JAXB ejecuta esto despues de deserializar cada categoría
	void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		// Establecer la relación bidireccional para cada subcategoría
		if (this.subcategorias != null) {
			for (Categoria subcategoria : this.subcategorias) {
				subcategoria.categoriaPadre = this;
			}
		}
	}

	// Método para añadir subcategoría
	public void addSubcategoria(Categoria subcategoria) {
		if (subcategorias == null) {
			subcategorias = new ArrayList<>();
		}
		subcategorias.add(subcategoria);
		subcategoria.setCategoriaPadre(this);
	}

	// Método para obtener todas las categorías descendientes (recursivo)
	public List<Categoria> obtenerDescendientes() {
		List<Categoria> descendientes = new ArrayList<>();
		if (subcategorias != null) {
			for (Categoria subcategoria : subcategorias) {
				descendientes.add(subcategoria);
				descendientes.addAll(subcategoria.obtenerDescendientes());
			}
		}
		return descendientes;
	}

	// Método para verificar si es raíz
	public boolean esRaiz() {
		return categoriaPadre == null;
	}

	// Getters y setters (Implementa Identificable)
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	public List<Categoria> getSubcategorias() {
		return subcategorias;
	}

	public void setSubcategorias(List<Categoria> subcategorias) {
		this.subcategorias = subcategorias;
		if (subcategorias != null) {
			for (Categoria sub : subcategorias) {
				sub.setCategoriaPadre(this);
			}
		}
	}

	public Categoria getCategoriaPadre() {
		return categoriaPadre;
	}

	public void setCategoriaPadre(Categoria categoriaPadre) {
		this.categoriaPadre = categoriaPadre;
	}

	public List<Producto> getProductos() {
		return productos;
	}

	@Override
	public String toString() {
		String toString = "Categoria{id=" + id + ", nombre=" + nombre  + ", categoria_padre=" + 
				(categoriaPadre != null ? categoriaPadre.getId() : "null") + 
				", subcategoriasCount=" + subcategorias.size() + "}";

		/*
		 * for (Categoria sub : getSubcategorias()) { toString += "\n  Subcategoria: " +
		 * sub.toString().replace("\n", "\n  "); }
		 */

		return toString;
	}

}