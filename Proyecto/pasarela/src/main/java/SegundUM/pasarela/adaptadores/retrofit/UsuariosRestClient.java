package SegundUM.pasarela.adaptadores.retrofit;

import SegundUM.pasarela.rest.dto.UsuarioDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsuariosRestClient {

    @GET("usuarios/verificar")
    Call<UsuarioDTO> verificarCredenciales(
            @Query("email") String email,
            @Query("clave") String clave);
}
