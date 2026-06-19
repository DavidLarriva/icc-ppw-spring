package ec.edu.ups.icc.fundamentos01.users.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;

/*
 * Convierte entre los DTOs y el modelo de usuario.
 * Aquí se genera el passwordHash y la fecha de creación,
 * y se decide qué datos son seguros de devolver al cliente.
 */
public class UserMapper {

    // De CreateUserDto a UserModel (el id lo asigna el controlador)
    public UserModel toModel(CreateUserDto dto) {
        UserModel user = new UserModel();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPasswordHash(hash(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    // De UserModel a la respuesta pública (sin password ni passwordHash)
    public UserResponseDto toResponse(UserModel user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }

    // Hash simple de ejemplo (no es seguro para producción)
    private String hash(String password) {
        if (password == null) {
            return null;
        }
        return Integer.toHexString(password.hashCode());
    }
}
