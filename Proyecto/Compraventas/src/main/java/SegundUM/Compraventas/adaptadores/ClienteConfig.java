package SegundUM.Compraventas.adaptadores;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import SegundUM.Compraventas.adaptadores.Retrofit.ApiAuthRetrofit;
import SegundUM.Compraventas.adaptadores.Retrofit.ApiProductosRetrofit;
import SegundUM.Compraventas.adaptadores.Retrofit.ApiUsuariosRetrofit;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class ClienteConfig {

    /**
     * Rest Template Bean para Spring 
     **/
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Retrofit Bean para api de Productos
     **/
    @Value("${api.productos.url}")
    private String productosBaseUrl;
    
    @Bean
    public ApiProductosRetrofit apiProductosRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(productosBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        return retrofit.create(ApiProductosRetrofit.class);
    }
    
    
    /**
	 * Retrofit Bean para api de Usuarios
	 **/
    @Value("${api.usuarios.url}")
    private String usuariosBaseUrl;
    
    @Bean
    public ApiUsuariosRetrofit apiUsuariosRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(usuariosBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        return retrofit.create(ApiUsuariosRetrofit.class);
    }
    
    /**
     * Retrofit Bean para api de Auth (login)
     **/
    @Value("${api.usuarios.auth.url}")
    private String usuariosAuthUrl;
    
    @Bean
    public ApiAuthRetrofit apiAuthRetrofit() {
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(usuariosAuthUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        return retrofit.create(ApiAuthRetrofit.class);
    }
    
}
