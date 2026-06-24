package ec.edu.ups.icc.fundamentos01.users.services;

import java.util.List;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

/*
 * Contrato del servicio de usuarios.
 * Aquí va la lógica de negocio; el controlador solo delega en estos métodos.
 */
public interface UserService {

    List<UserResponseDto> findAll();

    Object findOne(long id);

    UserResponseDto create(CreateUserDto dto);

    Object update(long id, UpdateUserDto dto);

    Object partialUpdate(long id, PartialUpdateUserDto dto);

    Object delete(long id);
}
