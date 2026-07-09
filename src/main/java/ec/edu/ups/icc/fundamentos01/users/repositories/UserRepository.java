package ec.edu.ups.icc.fundamentos01.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

/*
 * Repositorio encargado de gestionar la persistencia
 * de usuarios usando Spring Data JPA.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByIdAndDeletedFalse(Long id);

    /*
     * Usado en el login: un usuario eliminado lógicamente no puede autenticarse.
     */
    Optional<UserEntity> findByEmailAndDeletedFalse(String email);

    /*
     * Usado en el registro para validar duplicados sin cargar la entidad completa.
     */
    boolean existsByEmail(String email);
}
