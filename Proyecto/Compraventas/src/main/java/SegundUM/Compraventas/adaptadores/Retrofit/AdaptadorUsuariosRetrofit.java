package SegundUM.Compraventas.adaptadores.Retrofit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import SegundUM.Compraventas.puertos.PuertoUsuarios;
import SegundUM.Compraventas.rest.dto.UsuarioDTO;
import retrofit2.Response;

@Component
@ConditionalOnProperty(name="usuarios.adaptador", havingValue="retrofit")
public class AdaptadorUsuariosRetrofit implements PuertoUsuarios {
	
	private final ApiUsuariosRetrofit api;
	
	public AdaptadorUsuariosRetrofit(ApiUsuariosRetrofit api) {
		this.api = api;
	}
	
	public UsuarioDTO obtenerDatosUsuario(String idUsuario, String token) {
		try {
			return api.getUsuario(idUsuario, token).execute().body();
		} catch (Exception e) {
			throw new RuntimeException("Error en Retrofit", e);
		}
	}
	
}
