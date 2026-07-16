package ec.edu.ups.icc.fundamentos01.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ec.edu.ups.icc.fundamentos01.security.filters.JwtAuthenticationEntryPoint;
import ec.edu.ups.icc.fundamentos01.security.filters.JwtAuthenticationFilter;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsServiceImpl;

/*
 * Configuración central de Spring Security.
 *
 * Define cómo se codifican las contraseñas (BCrypt), cómo se validan las
 * credenciales (DaoAuthenticationProvider), y qué rutas quedan públicas y
 * cuáles requieren un JWT válido (SecurityFilterChain).
 *
 * @EnableMethodSecurity(prePostEnabled = true) habilita @PreAuthorize sobre
 * métodos de controllers/services para prácticas futuras (roles/ownership).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
            JwtAuthenticationEntryPoint unauthorizedHandler,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /*
     * BCrypt aplica salt automáticamente: la misma contraseña genera un hash
     * distinto cada vez que se codifica, así que dos usuarios con la misma
     * contraseña nunca tienen el mismo passwordHash en la base de datos.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Conecta UserDetailsServiceImpl (cómo buscar al usuario) con
     * PasswordEncoder (cómo comparar la contraseña) para que Spring Security
     * pueda validar credenciales durante el login.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protege sesiones basadas en cookies; con JWT stateless no aplica.
                .csrf(AbstractHttpConfigurer::disable)

                // Cuando falta el token o es inválido, responde con nuestro ErrorResponse (401).
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )

                // Sin sesión de servidor: cada request se autentica solo con su JWT.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/status/**").permitAll()
                        // StatusController está mapeado como GET /api/status (práctica 1),
                        // así que con el context-path /api la ruta real es /api/status.
                        .requestMatchers("/api/status/**").permitAll()

                        // Cuando un requestMatcher de más abajo (ej. hasRole en /actuator/**)
                        // deniega el acceso, Spring hace un forward interno a /error para
                        // renderizar la respuesta. Sin este permitAll, ese forward vuelve
                        // a pasar por el filtro de seguridad y, como nuestro JwtAuthenticationFilter
                        // no persiste el contexto en el SecurityContextRepository, /error
                        // se ve como anónimo y devuelve 401 en vez del 403 real.
                        .requestMatchers("/error").permitAll()

                        // Health check público (lo consulta el HEALTHCHECK de Docker,
                        // balanceadores de carga, etc.); el resto de Actuator
                        // (metrics, info) expone detalles internos, así que solo ADMIN.
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                       // .requestMatchers("/users/**").permitAll()

                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        // Nuestro filtro corre antes que el filtro de login por usuario/contraseña de Spring.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
