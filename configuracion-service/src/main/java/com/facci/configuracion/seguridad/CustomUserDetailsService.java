package com.facci.configuracion.seguridad;

import com.facci.configuracion.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
                    .roles("ADMINISTRADOR")
                    .build();
        }

        return userRepositorio.findByNombreUsuario(username)
                .map(user -> User.builder()
                        .username(user.getNombreUsuario())
                        .password(user.getContrasena())
                        .authorities(Collections.singletonList(
                                new SimpleGrantedAuthority(user.getRolUsuario().name())
                        ))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
