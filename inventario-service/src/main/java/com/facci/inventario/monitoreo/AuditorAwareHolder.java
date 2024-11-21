package com.facci.inventario.monitoreo;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareHolder {

    private static final ThreadLocal<String> currentAuditor = new ThreadLocal<>();

    public static void setAuditor(String auditor) {
        currentAuditor.set(auditor);
    }

    public static Optional<String> getAuditor() {
        return Optional.ofNullable(currentAuditor.get());
    }

    public static void clear() {
        currentAuditor.remove();
    }
}
