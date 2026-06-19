/*
 * Implementación del servicio de usuarios.
 *
 * En esta clase se mueve la lógica que antes estaba dentro del controlador:
 * listar, buscar, crear, actualizar y eliminar usuarios.
 *
 * En esta práctica todavía no se usa repository ni base de datos.
 * Por eso se mantiene una lista en memoria dentro del servicio.
 */
@Service
public class UserServiceImpl implements UserService {

    private List<UserModel> users = new ArrayList<>();
    private Long currentId = 1L;

    /*
     * Retorna todos los usuarios registrados en memoria.
     *
     * Convierte cada UserModel a UserResponseDto para no exponer
     * datos internos como password o passwordHash.
     */
    @Override
    public List<UserResponseDto> findAll() {

        // Programación tradicional iterativa
        /*
        List<UserResponseDto> dtos = new ArrayList<>();

        for (UserModel user : users) {
            dtos.add(UserMapper.toResponse(user));
        }

        return dtos;
        */

        // Programación funcional
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    /*
     * Busca un usuario por id.
     *
     * Si el usuario existe, devuelve UserResponseDto.
     * Si no existe, devuelve ErrorResponseDto.
     */
    @Override
    public Object findOne(Long id) {

        // Programación tradicional iterativa
        /*
        for (UserModel user : users) {
            if (user.getId().equals(id)) {
                return UserMapper.toResponse(user);
            }
        }

        return new ErrorResponseDto("User not found");
        */

        // Programación funcional
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .map(user -> (Object) UserMapper.toResponse(user))
                .orElseGet(() -> new ErrorResponseDto("User not found"));
    }

    /*
     * Crea un nuevo usuario.
     *
     * Recibe un CreateUserDto, lo convierte a UserModel,
     * asigna un id generado en memoria y devuelve UserResponseDto.
     */
    @Override
    public UserResponseDto create(CreateUserDto dto) {

        UserModel user = UserMapper.toModel(dto);

        user.setId(currentId);
        currentId++;

        users.add(user);

        return UserMapper.toResponse(user);
    }

    /*
     * Actualiza completamente un usuario existente.
     *
     * En PUT se reemplazan los campos editables enviados en el DTO.
     * No se modifica el id ni createdAt.
     */
    @Override
    public Object update(Long id, UpdateUserDto dto) {

        // Programación tradicional iterativa
        /*
        for (UserModel user : users) {
            if (user.getId().equals(id)) {
                user.setName(dto.getName());
                user.setEmail(dto.getEmail());

                return UserMapper.toResponse(user);
            }
        }

        return new ErrorResponseDto("User not found");
        */

        // Programación funcional
        UserModel user = users.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return new ErrorResponseDto("User not found");
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return UserMapper.toResponse(user);
    }

    /*
     * Actualiza parcialmente un usuario existente.
     *
     * En PATCH solo se actualizan los campos que llegan en el DTO.
     * Los campos nulos se ignoran.
     */
    @Override
    public Object partialUpdate(Long id, PartialUpdateUserDto dto) {

        // Programación tradicional iterativa
        /*
        for (UserModel user : users) {
            if (user.getId().equals(id)) {

                if (dto.getName() != null) {
                    user.setName(dto.getName());
                }

                if (dto.getEmail() != null) {
                    user.setEmail(dto.getEmail());
                }

                return UserMapper.toResponse(user);
            }
        }

        return new ErrorResponseDto("User not found");
        */

        // Programación funcional
        UserModel user = users.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return new ErrorResponseDto("User not found");
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        return UserMapper.toResponse(user);
    }

    /*
     * Elimina un usuario por id.
     *
     * Si el usuario existe, se elimina de la lista en memoria.
     * Si no existe, se devuelve un DTO de error.
     */
    @Override
    public Object delete(Long id) {

        boolean removed = users.removeIf(user -> user.getId().equals(id));

        if (!removed) {
            return new ErrorResponseDto("User not found");
        }

        return new Object() {
            public String message = "Deleted successfully";
        };
    }
}