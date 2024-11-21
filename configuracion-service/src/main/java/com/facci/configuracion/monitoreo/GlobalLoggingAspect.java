package com.facci.configuracion.monitoreo;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class GlobalLoggingAspect {

    @Before("execution(* com.facci.configuracion..*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("Ejecutando el Método: {}", methodName);
    }
    @Before("execution(* com.facci.configuracion..*(..))")
    public void logAfterMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("Saliendo del Método: {}", methodName);
    }
}
