package ec.edu.ups.icc.fundamentos01.security.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.edu.ups.icc.fundamentos01.security.entities.RefreshTokenEntity;

/*
 * Repositorio encargado de gestionar la persistencia de refresh tokens.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /*
     * Busca un refresh token activo por su valor. Se usa en /auth/refresh y
     * /auth/logout.
     */
    Optional<RefreshTokenEntity> findByTokenAndRevokedFalse(String token);

    /*
     * Refresh tokens activos de un usuario. Se usa en login para revocar los
     * anteriores y dejar una sola sesión activa.
     */
    List<RefreshTokenEntity> findByUser_IdAndRevokedFalse(Long userId);
}
