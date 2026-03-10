package SegundUM.Compraventas.adaptadores.Retrofit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import SegundUM.Compraventas.puertos.PuertoProductos;
import SegundUM.Compraventas.rest.dto.ProductoDTO;

@Component
@ConditionalOnProperty(name="productos.adaptador", havingValue="retrofit")
public class AdaptadorProductosRetrofit implements PuertoProductos {
	
	private final ApiProductosRetrofit api;
	
	public AdaptadorProductosRetrofit(ApiProductosRetrofit api) {
		this.api = api;
	}

	@Override
	public ProductoDTO obtenerDatosProducto(String idProducto) {
		try {
			return api.getProducto(idProducto).execute().body();
		} catch (Exception e) {
			throw new RuntimeException("Error en Retrofit", e);
		}
	}
	
}
