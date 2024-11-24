package com.facci.inventario.Configuracion;

import com.facci.inventario.dto.UsuarioDTO;
import com.facci.inventario.servicio.UsuarioSesionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConfiguracionService {

    private final RestTemplate restTemplate;
    private final UsuarioSesionService usuarioSesionService;

    @Value("${configuracion-service.url}")
    private String configuracionServiceUrl;

    public ConfiguracionService(RestTemplate restTemplate, UsuarioSesionService usuarioSesionService) {
        this.restTemplate = restTemplate;
        this.usuarioSesionService = usuarioSesionService;
    }

    public UsuarioDTO consultarUsuario(Long id) {
        String url = configuracionServiceUrl + "/configuraciones/" + id;
        String token = usuarioSesionService.obtenerTokenActual().orElseThrow(() -> new RuntimeException("No se encontró un token en la sesión actual"));

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

    public UsuarioDTO buscarPorNombreUsuario(String nombreUsuario) {
        String url = configuracionServiceUrl + "/configuraciones/nombreUsuario/" + nombreUsuario;
        String token = usuarioSesionService.obtenerTokenActual().orElseThrow(() -> new RuntimeException("No se encontró un token en la sesión actual"));

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


}