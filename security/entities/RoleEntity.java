/**
 * ENTIDAD: Role (Rol de usuario)
 * 
 * Representa un rol en el sistema (ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR).
 * Se relaciona ManyToMany con usuarios → Un usuario puede tener múltiples roles.
 * 
 * Tabla en BD: roles
 * Tabla intermedia: user_roles (creada automáticamente por JPA)
 */
@Entity
@Table(name = "roles")  // Nombre de la tabla en PostgreSQL
public class RoleEntity extends BaseEntity {  // Hereda id, createdAt, updatedAt

    /**
     * Nombre del rol (enum para type-safety)
     * 
     * @Enumerated(EnumType.STRING): Guarda "ROLE_USER" en lugar de ordinal (0, 1, 2)
     * nullable = false: Campo obligatorio
     * unique = true: No pueden existir roles duplicados
     * length = 50: Máximo 50 caracteres en BD
     * 
     * Ejemplo en BD: "ROLE_USER", "ROLE_ADMIN"
     */
    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)  // Guardar nombre del enum, no el número
    private RoleName name;

    /**
     * Descripción del rol (opcional)
     * 
     * Ejemplo: "Usuario estándar con permisos básicos"
     */
    @Column(length = 200)
    private String description;

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoleEntity(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }

    

    
   
}