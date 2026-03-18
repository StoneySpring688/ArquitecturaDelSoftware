package SegundUM.Compraventas.adaptadores.Retrofit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import SegundUM.Compraventas.puertos.PuertoAutenticacion;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Component
@ConditionalOnProperty(name="usuarios.adaptador", havingValue="retrofit")
public class AdaptadorAuthRetrofit implements PuertoAutenticacion {
    
    private final ApiAuthRetrofit api;
    
    public AdaptadorAuthRetrofit(ApiAuthRetrofit api) {
        this.api = api;
    }
    
    @Override
    public String login(String email, String clave) {
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
            return token;
            
        } catch (Exception e) {
            throw new RuntimeException("Error en login a través de Retrofit: " + e.getMessage(), e);
        }
    }
}