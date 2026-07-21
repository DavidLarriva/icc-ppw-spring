package ec.edu.ups.icc.fundamentos01.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/*
 * Configura la documentación OpenAPI/Swagger de la API.
 *
 * Además de los datos básicos (título, versión), registra el esquema de
 * seguridad "bearerAuth" para que Swagger UI muestre el botón Authorize y
 * mande el header Authorization: Bearer <token> en cada request de prueba.
 */
@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("API de programacion y plataformas web")
                .version("1.0.0")
                .description("API REST para la gestión de usuarios y roles en una aplicación Spring Boot.");

        Server localServer = new Server()
                .url("/api")
                .description("Servidor local");

        SecurityScheme bearerScheme = new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Ingrese el JWT generado en /auth/login");

        Components components = new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, bearerScheme);

        return new OpenAPI()
                .info(info)
                .addServersItem(localServer)
                .components(components)
                // Marca TODOS los endpoints como protegidos por defecto en la UI
                // (el candado). Los que en verdad son públicos (login, register,
                // swagger, actuator/health) igual funcionan sin token: esto solo
                // afecta lo que Swagger UI muestra, no la seguridad real, que la
                // sigue decidiendo SecurityConfig.
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
