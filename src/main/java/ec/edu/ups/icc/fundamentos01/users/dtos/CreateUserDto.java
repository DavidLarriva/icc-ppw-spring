package ec.edu.ups.icc.fundamentos01.users.dtos;

/*
 * DTO utilizado para recibir del cliente los datos necesarios
 * para crear un nuevo usuario en la API.
 *
 * Incluye password (en texto plano; se hashea en el servidor).
 * No incluye id (lo genera el servidor).
 * No incluye passwordHash.
 */
public class CreateUserDto {

    private String name;
    private String email;
    private String password;

    // Constructor vacío
    public CreateUserDto() {
    }

    // Constructor lleno
    public CreateUserDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters y setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
