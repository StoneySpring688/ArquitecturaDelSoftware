package SegundUM.Compraventas.adaptadores.Retrofit;

import SegundUM.Compraventas.rest.dto.UsuarioDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiUsuariosRetrofit {
	
	@GET("{id}")
	Call<UsuarioDTO> getUsuario(
			@Path("id") String id,
			@Header("Authorization") String token);
}
