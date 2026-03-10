package SegundUM.Compraventas.adaptadores.Spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import SegundUM.Compraventas.puertos.PuertoProductos;
import SegundUM.Compraventas.rest.dto.ProductoDTO;

@Component
@ConditionalOnProperty(name = "productos.adaptador", havingValue = "spring")
public class AdaptadorProductosSpring implements PuertoProductos {
	
	private final RestTemplate restTemplate;
	
	@Value("${api.productos.url}")
	private String urlBase;
	
	public AdaptadorProductosSpring(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@Override
	public ProductoDTO obtenerDatosProducto(String idProducto) {
		return restTemplate.getForObject(urlBase + idProducto, ProductoDTO.class);
	}
	
}
