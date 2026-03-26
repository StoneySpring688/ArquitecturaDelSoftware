package SegundUM.pasarela.adaptadores.retrofit;

import SegundUM.pasarela.puertos.PuertoUsuarios;
import SegundUM.pasarela.rest.dto.UsuarioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;

@Component
public class AdaptadorUsuariosRetrofit implements PuertoUsuarios {

    private static final Logger logger = LoggerFactory.getLogger(AdaptadorUsuariosRetrofit.class);

    private final UsuariosRestClient restClient;

    @Autowired
    public AdaptadorUsuariosRetrofit(UsuariosRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public UsuarioDTO verificarCredenciales(String email, String clave) {
        logger.info("Verificando credenciales para email: {}", email);
        try {
            Response<UsuarioDTO> response = restClient.verificarCredenciales(email, clave).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            logger.error("Error al verificar credenciales via Retrofit: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio Usuarios mediante Retrofit", e);
        }
        return null;
    }

    @Override
    public UsuarioDTO verificarGitHub(String idGitHub) {
        logger.info("Verificando GitHub ID: {}", idGitHub);
        try {
            Response<UsuarioDTO> response = restClient.verificarGitHub(idGitHub).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            logger.error("Error al verificar GitHub ID via Retrofit: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio Usuarios (GitHub) mediante Retrofit", e);
        }
        return null;
    }

    @Override
    public UsuarioDTO registrarUsuarioGitHub(String idGitHub, String nombre, String email) {
        try {
            Response<UsuarioDTO> response = restClient.registrarUsuarioGitHub(idGitHub, nombre, email).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al registrar usuario GitHub mediante Retrofit", e);
        }
        return null;
    }
}
