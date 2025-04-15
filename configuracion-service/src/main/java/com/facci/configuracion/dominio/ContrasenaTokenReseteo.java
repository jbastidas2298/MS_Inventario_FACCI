package com.facci.configuracion.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContrasenaTokenReseteo extends EntidadBase {
    private String token;

    @ManyToOne
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario user;

    private LocalDateTime expiryDate;

}