package ec.edu.ups.icc.fundamentos01.users.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;


public class UserMapper {


    public static UserModel toModelFromDTO(CreateUserDto dto) {
        UserModel user = new UserModel();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPasswordHash(hash(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }


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


    public static UserEntity toEntityFromModel(UserModel model) {
        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setEmail(model.getEmail());
        entity.setPasswordHash(model.getPasswordHash());
        return entity;
    }


    public static UserResponseDto toResponse(UserModel user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }

    /*
     public static String hash(String password) {
        if (password == null) {
            return null;
        }
        return Integer.toHexString(password.hashCode());
    }
    */
    public static String hash(String password) {
        if (password == null) {
            return null;
        }
        return Integer.toHexString(password.hashCode());
    }
}
