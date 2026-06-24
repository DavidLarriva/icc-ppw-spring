package ec.edu.ups.icc.fundamentos01.users.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;

/*
 * Convierte entre los DTOs y el modelo de usuario.
 * Aquí se genera el passwordHash y la fecha de creación,
 * y se decide qué datos son seguros de devolver al cliente.
 *
 * Se agrega la conversión hacia/desde UserEntity porque ya se
 * trabaja con persistencia real en PostgreSQL.
 */
public class UserMapper {

    // De CreateUserDto a UserModel (el id lo asigna la base de datos)
    public static UserModel toModelFromDTO(CreateUserDto dto) {
        UserModel user = new UserModel();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPasswordHash(hash(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    // De UserEntity (devuelta por el repositorio) a UserModel
    public static UserModel toModelFromEntity(UserEntity entity) {
        UserModel user = new UserModel();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setPasswordHash(entity.getPasswordHash());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        user.setDeleted(entity.isDeleted());
        return user;
    }

    // De UserModel a UserEntity, antes de guardar en la base de datos
    public static UserEntity toEntityFromModel(UserModel model) {
        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setEmail(model.getEmail());
        entity.setPasswordHash(model.getPasswordHash());
        return entity;
    }

    // De UserModel a la respuesta pública (sin password ni passwordHash)
    public static UserResponseDto toResponse(UserModel user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }

    // Hash simple de ejemplo (no es seguro para producción)
    private static String hash(String password) {
        if (password == null) {
            return null;
        }
        return Integer.toHexString(password.hashCode());
    }
}
