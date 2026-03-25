package SegundUM.Compraventas.adaptadores.Retrofit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import SegundUM.Compraventas.puertos.PuertoAutenticacion;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Component
@ConditionalOnProperty(name="usuarios.adaptador", havingValue="retrofit")
public class AdaptadorAuthRetrofit implements PuertoAutenticacion {

    private static final Logger logger = LoggerFactory.getLogger(AdaptadorAuthRetrofit.class);

    private final ApiAuthRetrofit api;
    
    public AdaptadorAuthRetrofit(ApiAuthRetrofit api) {
        this.api = api;
    }
    
    @Override
    public String login(String email, String clave) {
        logger.info("Autenticando usuario {} via Retrofit", email);
        try {
            Response<ResponseBody> respuesta = api.login(email, clave).execute();

            if (!respuesta.isSuccessful()) {
                throw new RuntimeException("Credenciales inválidas. HTTP " + respuesta.code());
            }

            String token = respuesta.body().string();
            // El token llega como JSON string con comillas ("eyJ..."), hay que quitarlas
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }
            logger.debug("Autenticacion exitosa para {}", email);
            return token;

        } catch (Exception e) {
            logger.error("Error de autenticacion via Retrofit: {}", e.getMessage());
            throw new RuntimeException("Error en login a través de Retrofit: " + e.getMessage(), e);
        }
    }
}