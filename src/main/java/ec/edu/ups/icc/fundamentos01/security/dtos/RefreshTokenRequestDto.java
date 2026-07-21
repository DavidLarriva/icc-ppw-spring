package ec.edu.ups.icc.fundamentos01.security.dtos;

import jakarta.validation.constraints.NotBlank;

/*
 * DTO usado para recibir un refresh token desde el cliente.
 *
 * Se usa en POST /auth/refresh y POST /auth/logout.
 */
public class RefreshTokenRequestDto {

    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;

    public RefreshTokenRequestDto() {
    }

    public RefreshTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
