package com.facci.configuracion.controlador;

import com.facci.configuracion.enums.EnumCodigos;
import com.facci.configuracion.handler.CustomException;
import com.facci.configuracion.seguridad.JwtTokenProvider;
import com.facci.configuracion.servicio.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@RestController
public class SeguridadControlador {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    public SeguridadControlador(AuthenticationManager authenticationManager, UsuarioService usuarioService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @RequestMapping(value="/configuracion/login" , method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            String token = jwtTokenProvider.createToken(username,roles);
            Map<String, String> response = new HashMap<>();
            response.put("token", "Bearer " + token);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.error("Error de autenticación: ", e);
            throw new CustomException(EnumCodigos.ERROR_CREDENCIALES);
        }catch (AuthenticationException e) {
            log.error("Error de autenticación: ", e);
            throw new CustomException(EnumCodigos.ERROR_INICIO);
        }
    }
}
