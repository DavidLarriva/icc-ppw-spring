package ec.edu.ups.icc.fundamentos01.users.dtos;

/*
 * DTO utilizado para devolver al cliente los datos públicos
 * de un usuario como respuesta de la API.
 *
 * No incluye password.
 * No incluye passwordHash.
 */
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;

    // Constructor vacío
    public UserResponseDto() {
    }

    // Constructor lleno
    public UserResponseDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
