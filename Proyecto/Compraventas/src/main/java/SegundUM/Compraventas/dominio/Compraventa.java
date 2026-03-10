package SegundUM.Compraventas.dominio;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "compraventas")
public class Compraventa {
	@Id
	private String id;
	private String idProducto;
	private String titulo;
	private String recogida;
	private int precio;
	
	private String idVendedor;
	private String nombreVendedor;
	private String idComprador;
	private String nombreComprador;
	private LocalDateTime fechaYHora;
	
	public Compraventa(){}
	
	public Compraventa(String id, String idProducto, String titulo, String recogida, int precio, String idVendedor,
			String nombreVendedor, String idComprador, String nombreComprador, LocalDateTime fechaYHora) {
		this.id = id;
		this.idProducto = idProducto;
		this.titulo = titulo;
		this.recogida = recogida;
		this.precio = precio;
		this.idVendedor = idVendedor;
		this.nombreVendedor = nombreVendedor;
		this.idComprador = idComprador;
		this.nombreComprador = nombreComprador;
		this.fechaYHora = fechaYHora;
	}

	public String getId() {
		return id;
	}

	public String getIdProducto() {
		return idProducto;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getRecogida() {
		return recogida;
	}

	public int getPrecio() {
		return precio;
	}

	public String getIdVendedor() {
		return idVendedor;
	}

	public String getNombreVendedor() {
		return nombreVendedor;
	}

	public String getIdComprador() {
		return idComprador;
	}

	public String getNombreComprador() {
		return nombreComprador;
	}

	public LocalDateTime getFechaYHora() {
		return fechaYHora;
	}
	
	
}
