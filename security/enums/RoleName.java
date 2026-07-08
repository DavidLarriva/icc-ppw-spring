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