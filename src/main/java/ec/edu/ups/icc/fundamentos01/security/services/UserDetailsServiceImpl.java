package ec.edu.ups.icc.fundamentos01.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

/*
 * Carga usuarios desde la base de datos para que Spring Security los use
 * al autenticar. Es el punto de entrada que conecta nuestra tabla users
 * con el mecanismo de autenticación de Spring.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Spring Security llama a este método pasándole lo que el usuario mandó
     * como "username" — en nuestro caso, el email.
     *
     * @Transactional(readOnly = true) porque es una consulta de solo lectura
     * y, además, mantiene la sesión de Hibernate abierta mientras se arma
     * UserDetailsImpl (necesario para leer user.getRoles() sin problemas).
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email));

        return UserDetailsImpl.build(user);
    }
}
