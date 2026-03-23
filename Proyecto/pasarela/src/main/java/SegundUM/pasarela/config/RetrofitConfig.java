package SegundUM.pasarela.config;

import SegundUM.pasarela.adaptadores.retrofit.UsuariosRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class RetrofitConfig {

    private static final String USUARIOS_BASE_URL = "http://localhost:8081/api/";

    @Bean
    public UsuariosRestClient usuariosRestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(USUARIOS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(UsuariosRestClient.class);
    }
}
