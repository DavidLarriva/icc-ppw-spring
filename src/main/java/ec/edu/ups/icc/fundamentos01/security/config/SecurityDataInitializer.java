package ec.edu.ups.icc.fundamentos01.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.icc.fundamentos01.security.entities.RoleEntity;
import ec.edu.ups.icc.fundamentos01.security.enums.RoleName;
import ec.edu.ups.icc.fundamentos01.security.repositories.RoleRepository;

/*
 * Crea los roles ROLE_USER y ROLE_ADMIN al arrancar la aplicación, si todavía
 * no existen en la base de datos.
 *
 * Sin esto, el primer registro fallaría: AuthService.register() intenta
 * asignar ROLE_USER, pero si esa fila no existe en la tabla roles, no hay
 * nada que asignar (violación de la relación ManyToMany con roles).
 */
@Component
public class SecurityDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SecurityDataInitializer.class);

    private final RoleRepository roleRepository;

    public SecurityDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createRoleIfNotExists(RoleName.ROLE_USER, "Usuario estándar con permisos básicos");
        createRoleIfNotExists(RoleName.ROLE_ADMIN, "Administrador con permisos completos");
    }

    private void createRoleIfNotExists(RoleName roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            RoleEntity role = new RoleEntity(roleName, description);
            roleRepository.save(role);
            logger.info("{} creado", roleName);
        } else {
            logger.info("{} ya existe", roleName);
        }
    }
}
