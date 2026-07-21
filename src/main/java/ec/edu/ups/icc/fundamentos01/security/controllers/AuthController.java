package ec.edu.ups.icc.fundamentos01.security.controllers;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.security.dtos.AuthResponseDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.LoginRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RefreshTokenRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RegisterRequestDto;
import ec.edu.ups.icc.fundamentos01.security.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

/*
 * Endpoints públicos de autenticación.
 *
 * POST /auth/login    -> valida credenciales, devuelve access + refresh token
 * POST /auth/register -> crea un usuario nuevo, devuelve access + refresh token
 * POST /auth/refresh  -> valida un refresh token y devuelve un par nuevo (rotación)
 * POST /auth/logout   -> revoca el refresh token recibido
 *
 * Los cuatro quedan fuera de la protección de JWT (SecurityConfig los marca con
 * .requestMatchers("/auth/**").permitAll()). /refresh y /logout no se validan
 * con access token: se validan con el propio refresh token, en RefreshTokenService.
 *
 * @SecurityRequirements (vacío) en cada método le dice a Swagger que estos
 * endpoints NO necesitan el candado bearerAuth, aunque OpenApiConfig lo pida
 * por defecto para el resto de la API.
 */
@Tag(name = "autenticación", description = "login, registro y renovación de tokens con JWT")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "iniciar sesión")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "login correcto, devuelve access token y refresh token",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "email o contraseña incorrectos"),
            @ApiResponse(responseCode = "400", description = "datos de entrada inválidos")
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "registrar un usuario nuevo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "usuario creado, devuelve access token y refresh token",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "el email ya está registrado"),
            @ApiResponse(responseCode = "400", description = "datos de entrada inválidos")
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        AuthResponseDto response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "renovar access token usando el refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "tokens nuevos (rotación: el refresh token anterior queda revocado)",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "refresh token inválido, expirado, revocado o ya usado")
    })
    @SecurityRequirements
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        AuthResponseDto response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "cerrar sesión (revoca el refresh token)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "sesión cerrada, el refresh token ya no sirve"),
            @ApiResponse(responseCode = "400", description = "refresh token inválido o ya revocado")
    })
    @SecurityRequirements
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        authService.logout(request);
    }
}
