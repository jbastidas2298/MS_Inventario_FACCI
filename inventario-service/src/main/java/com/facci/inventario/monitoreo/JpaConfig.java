package com.facci.inventario.monitoreo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import javax.swing.text.html.parser.Entity;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> AuditorAwareHolder.getAuditor().or(() -> Optional.of("Sistema"));
	}
}
