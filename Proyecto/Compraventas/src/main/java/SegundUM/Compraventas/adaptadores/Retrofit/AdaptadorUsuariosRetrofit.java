package SegundUM.Compraventas.adaptadores.Retrofit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import SegundUM.Compraventas.puertos.PuertoUsuarios;
import SegundUM.Compraventas.rest.dto.UsuarioDTO;
import retrofit2.Response;

@Component
@ConditionalOnProperty(name="usuarios.adaptador", havingValue="retrofit")
public class AdaptadorUsuariosRetrofit implements PuertoUsuarios {

	private static final Logger logger = LoggerFactory.getLogger(AdaptadorUsuariosRetrofit.class);

	private final ApiUsuariosRetrofit api;
	
	public AdaptadorUsuariosRetrofit(ApiUsuariosRetrofit api) {
		this.api = api;
	}
	
	public UsuarioDTO obtenerDatosUsuario(String idUsuario, String token) {
		logger.info("Consultando datos del usuario {} via Retrofit", idUsuario);
		try {
			UsuarioDTO usuario = api.getUsuario(idUsuario, token).execute().body();
			logger.debug("Usuario {} obtenido correctamente", idUsuario);
			return usuario;
		} catch (Exception e) {
			logger.error("Error al obtener usuario {} via Retrofit: {}", idUsuario, e.getMessage());
			throw new RuntimeException("Error en Retrofit", e);
		}
	}
	
}
