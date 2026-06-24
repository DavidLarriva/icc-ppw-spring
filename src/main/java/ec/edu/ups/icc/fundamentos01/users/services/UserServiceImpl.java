package ec.edu.ups.icc.fundamentos01.users.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.dtos.ErrorResponseDto;
import ec.edu.ups.icc.fundamentos01.dtos.MessageResponseDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;

/*
 * Implementación del servicio de usuarios.
 * @Service le dice a Spring que cree y administre esta clase para poder
 * inyectarla donde se necesite (por ejemplo en el controlador).
 * Guarda los usuarios en memoria mientras la aplicación está encendida.
 */
@Service
public class UserServiceImpl implements UserService {

    private final List<UserModel> users = new ArrayList<>();
    private long currentId = 1;

    @Override
    public List<UserResponseDto> findAll() {
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public Object findOne(long id) {
        UserModel user = findById(id);
        if (user == null) {
            return new ErrorResponseDto("Usuario no encontrado con id " + id);
        }
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponseDto create(CreateUserDto dto) {
        UserModel user = UserMapper.toModel(dto);
        user.setId(currentId++);
        users.add(user);
        return UserMapper.toResponse(user);
    }

    @Override
    public Object update(long id, UpdateUserDto dto) {
        UserModel user = findById(id);
        if (user == null) {
            return new ErrorResponseDto("Usuario no encontrado con id " + id);
        }
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return UserMapper.toResponse(user);
    }

    @Override
    public Object partialUpdate(long id, PartialUpdateUserDto dto) {
        UserModel user = findById(id);
        if (user == null) {
            return new ErrorResponseDto("Usuario no encontrado con id " + id);
        }
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            user.setPassword(dto.getPassword());
        }
        return UserMapper.toResponse(user);
    }

    @Override
    public Object delete(long id) {
        boolean removed = users.removeIf(user -> user.getId() == id);
        if (!removed) {
            return new ErrorResponseDto("Usuario no encontrado con id " + id);
        }
        return new MessageResponseDto("Usuario eliminado correctamente");
    }

    // Lógica de búsqueda reutilizada por varios métodos
    private UserModel findById(long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
