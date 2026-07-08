package ec.edu.ups.icc.fundamentos01.security.enums;

/*
 * Nombres de rol soportados por la aplicación.
 *
 * Se usa enum en vez de String suelto para evitar typos ("ADMIN" vs "ADMNI")
 * y para que solo existan valores válidos.
 */
public enum RoleName {
    ROLE_USER("Usuario estándar con permisos básicos"),
    ROLE_ADMIN("Administrador con permisos completos");

    private final String description;

    RoleName(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
