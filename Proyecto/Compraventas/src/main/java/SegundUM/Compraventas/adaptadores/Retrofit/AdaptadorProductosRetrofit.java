package SegundUM.Compraventas.adaptadores.Retrofit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import SegundUM.Compraventas.puertos.PuertoProductos;
import SegundUM.Compraventas.rest.dto.ProductoDTO;

@Component
@ConditionalOnProperty(name="productos.adaptador", havingValue="retrofit")
public class AdaptadorProductosRetrofit implements PuertoProductos {

	private static final Logger logger = LoggerFactory.getLogger(AdaptadorProductosRetrofit.class);

	private final ApiProductosRetrofit api;
	
	public AdaptadorProductosRetrofit(ApiProductosRetrofit api) {
		this.api = api;
	}

	@Override
	public ProductoDTO obtenerDatosProducto(String idProducto) {
		logger.info("Consultando datos del producto {} via Retrofit", idProducto);
		try {
			ProductoDTO producto = api.getProducto(idProducto).execute().body();
			logger.debug("Producto {} obtenido correctamente", idProducto);
			return producto;
		} catch (Exception e) {
			logger.error("Error al obtener producto {} via Retrofit: {}", idProducto, e.getMessage());
			throw new RuntimeException("Error en Retrofit", e);
		}
	}
	
}
