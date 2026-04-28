package SegundUM.Productos.rest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Configuración de OpenAPI para habilitar la autenticación JWT en Swagger UI.
 * Permite usar el botón 'Authorize' para enviar el token Bearer en las cabeceras.
 */
@Configuration
public class OpenApiConfig {

    @org.springframework.beans.factory.annotation.Value("${api.url.gateway:http://localhost:8090}")
    private String gatewayUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addServersItem(new Server().url(gatewayUrl).description("Pasarela API Gateway"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomiser customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            Paths newPaths = new Paths();
            if (paths != null) {
                paths.forEach((key, value) -> {
                    if (key.startsWith("/api")) {
                        newPaths.addPathItem(key.replace("/api", ""), value);
                    } else {
                        newPaths.addPathItem(key, value);
                    }
                });
            }
            openApi.setPaths(newPaths);
        };
    }
}
