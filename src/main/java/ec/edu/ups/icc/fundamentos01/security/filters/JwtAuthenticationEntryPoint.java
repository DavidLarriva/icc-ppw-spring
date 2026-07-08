package ec.edu.ups.icc.fundamentos01.security.filters;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

import ec.edu.ups.icc.fundamentos01.core.exceptions.response.ErrorResponse;

/*
 * Maneja los errores de autenticación (falta de token, token inválido o
 * expirado) antes de que la petición llegue a cualquier controlador.
 *
 * No se puede resolver esto con @RestControllerAdvice porque
 * AuthenticationException se lanza en la cadena de filtros de seguridad,
 * antes de que Spring MVC despache la petición a un controlador:
 *
 * Request -> Filtros -> ¿Autenticado? -> Controlador -> Response
 *                          |                |
 *              JwtAuthenticationEntryPoint  GlobalExceptionHandler
 *              (errores ANTES del controller) (errores EN el controller)
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /*
     * Se ejecuta automáticamente cuando Spring Security detecta que la
     * petición no está autenticada. Escribe la respuesta directamente en el
     * HttpServletResponse, reutilizando el mismo ErrorResponse que usa
     * GlobalExceptionHandler para mantener el formato de error consistente
     * en toda la API.
     */
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        logger.error("Error de autenticación: {}", authException.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Token de autenticación inválido o no proporcionado. "
                        + "Debe incluir un token válido en el header Authorization: Bearer <token>",
                request.getRequestURI()
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
