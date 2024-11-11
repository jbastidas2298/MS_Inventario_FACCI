package com.facci.configuracion.seguridad;

import com.facci.configuracion.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepositorio userRepositorio;

    @Value("${spring.security.user.name}")
    private String adminUsername;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    public CustomUserDetailsService(UsuarioRepositorio userRepositorio) {
        this.userRepositorio = userRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals(adminUsername)) {
            return User.builder()
                    .username(adminUsername)
                    .password(adminPassword)
                    .roles("ADMIN")
                    .build();
        }

        return userRepositorio.findByNombreUsuario(username)
                .map(user -> User.builder()
                        .username(user.getNombreUsuario())
                        .password(user.getContrasena())
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
