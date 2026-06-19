package ec.edu.ups.icc.fundamentos01.users.dtos;

/*
 * DTO utilizado para recibir del cliente los datos de un usuario
 * en una actualización completa (PUT).
 *
 * Reemplaza todos los campos editables del usuario.
 * Incluye password (en texto plano; se hashea en el servidor).
 * No incluye id (se toma de la ruta).
 * No incluye passwordHash.
 */
public class UpdateUserDto {

    private String name;
    private String email;
    private String password;

    // Constructor vacío
    public UpdateUserDto() {
    }

    // Constructor lleno
    public UpdateUserDto(String name, String email, String password) {
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
