package com.facci.inventario.monitoreo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null || !authentication.isAuthenticated()) {
				return Optional.of("Sistema");
			}

			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				return Optional.of(((UserDetails) principal).getUsername());
			} else if (principal instanceof String) {
				return Optional.of((String) principal);
			}

			return Optional.of("Sistema");
		};
	}

}
