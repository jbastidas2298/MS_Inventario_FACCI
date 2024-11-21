package com.facci.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Crear un interceptor para agregar encabezados personalizados
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("X-API-KEY", "$2a$10$vl2reM1tzZoGoMwRxymMneiNjyUqWiCdT4.fFMZbL2nFZNSZo8csy");
            return execution.execute(request, body);
        };

        // Agregar el interceptor a la lista de interceptores del RestTemplate
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(interceptor);
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
        request.getHeaders().add("X-API-KEY", "$2a$10$vl2reM1tzZoGoMwRxymMneiNjyUqWiCdT4.fFMZbL2nFZNSZo8csy");
        return execution.execute(request, body);
    };
}
