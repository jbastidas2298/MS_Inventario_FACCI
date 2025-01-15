package com.facci.inventario.Configuracion;

import com.facci.comun.dto.UsuarioAreaDTO;
import com.facci.comun.dto.UsuarioDTO;
import com.facci.comun.enums.TipoRelacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ConfiguracionService {

    private final RestTemplate restTemplate;

    @Value("${configuracion-service.url}")
    private String configuracionServiceUrl;

    public ConfiguracionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UsuarioDTO consultarUsuario(Long id) {
        log.info("Consultando usuario a configuracion");
        String url = configuracionServiceUrl + "/configuraciones/" + id;
        String token = obtenerTokenActual().orElseThrow(() -> new RuntimeException("No se encontró un token en la sesión actual"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UsuarioDTO.class
        );

        return response.getBody();
    }

    public UsuarioAreaDTO consultarUsuarioArea(Long id, TipoRelacion tipoRelacion) {
        String url = configuracionServiceUrl + "/configuraciones/" + id +"/"+tipoRelacion;
        String token = obtenerTokenActual().orElseThrow(() -> new RuntimeException("No se encontró un token en la sesión actual"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UsuarioAreaDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UsuarioAreaDTO.class
        );

        return response.getBody();
    }

    public List<UsuarioAreaDTO> consultarUsuarioAreaTodos() {
        String url = configuracionServiceUrl + "/configuraciones/usuarioArea";
        String token = obtenerTokenActual().orElseThrow(() -> new RuntimeException("No se encontró un token en la sesión actual"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Usa ParameterizedTypeReference para manejar listas genéricas
        ResponseEntity<List<UsuarioAreaDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<UsuarioAreaDTO>>() {}
        );

        return response.getBody();
    }

    public UsuarioDTO buscarPorNombreUsuario(String nombreUsuario) {
        String url = configuracionServiceUrl + "/configuraciones/nombreUsuario/" + nombreUsuario;
        String token = obtenerTokenActual().orElseThrow(() -> new RuntimeException("No se encontró un token en la sesión actual"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UsuarioDTO.class
        );

        return response.getBody();
    }

    public Optional<String> obtenerTokenActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication instanceof UsernamePasswordAuthenticationToken) {
            return Optional.ofNullable((String) authentication.getCredentials());
        }

        return Optional.empty();
    }

}