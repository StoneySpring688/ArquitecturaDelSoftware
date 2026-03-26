package SegundUM.pasarela.config;

import SegundUM.pasarela.adaptadores.retrofit.UsuariosRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class RetrofitConfig {

    private static final Logger logger = LoggerFactory.getLogger(RetrofitConfig.class);

    @org.springframework.beans.factory.annotation.Value("${api.usuarios.url:http://localhost:8081/api/}")
    private String usuariosBaseUrl;

    @Bean
    public UsuariosRestClient usuariosRestClient() {
        logger.info("Configurando cliente Retrofit para Usuarios (URL: {})", usuariosBaseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(usuariosBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(UsuariosRestClient.class);
    }
}
