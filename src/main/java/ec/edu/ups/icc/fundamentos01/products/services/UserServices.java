/*
 * Servicio que define las operaciones disponibles
 * para la gestión de usuarios.
 *
 * En esta interfaz se declaran las acciones del módulo,
 * pero no se implementa la lógica.
 */
public interface UserService 
{

    List<UserResponseDto> findAll();

    Object findOne(Long id);

    UserResponseDto create(CreateUserDto dto);

    Object update(Long id, UpdateUserDto dto);

    Object partialUpdate(Long id, PartialUpdateUserDto dto);

    Object delete(Long id);
}