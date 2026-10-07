package com.josethjax.biblioteca.config;

import com.josethjax.biblioteca.entity.EstadoUsuario;
import com.josethjax.biblioteca.entity.Rol;
import com.josethjax.biblioteca.entity.Usuario;
import com.josethjax.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea los usuarios semilla al arrancar la aplicacion si aun no existen.
 * Al usar BCryptPasswordEncoder en tiempo de ejecucion, los hashes siempre
 * coincidiran con las contrasenas en texto plano, sin importar el entorno.
 *
 * Credenciales:
 *   admin@biblioteca.com  / Admin123*   -> ADMIN
 *   biblio@biblioteca.com / Biblio123*  -> BIBLIOTECARIO
 *   lector@biblioteca.com / Lector123*  -> LECTOR
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder   passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        crearUsuarioSiNoExiste("Administrador", "admin@biblioteca.com",  "Admin123*",  Rol.ADMIN);
        crearUsuarioSiNoExiste("Bibliotecario", "biblio@biblioteca.com", "Biblio123*", Rol.BIBLIOTECARIO);
        crearUsuarioSiNoExiste("Lector",        "lector@biblioteca.com", "Lector123*", Rol.LECTOR);
        log.info("DataInitializer: usuarios semilla verificados correctamente.");
    }

    private void crearUsuarioSiNoExiste(String nombre, String email, String password, Rol rol) {
        if (usuarioRepository.existsByEmail(email)) {
            log.debug("DataInitializer: usuario '{}' ya existe, se omite.", email);
            return;
        }
        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .email(email)
                .password(passwordEncoder.encode(password))
                .estado(EstadoUsuario.ACTIVO)
                .rol(rol)
                .build();
        usuarioRepository.save(usuario);
        log.info("DataInitializer: usuario '{}' creado con rol {}.", email, rol);
    }
}