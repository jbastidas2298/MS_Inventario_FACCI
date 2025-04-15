package com.facci.configuracion.servicio;
import com.facci.comun.dto.UsuarioDTO;
import com.facci.configuracion.dominio.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${correo.perfil}")
    private String perfilCorreo;

    @Value("${correo.portal}")
    private String portalUrl;

    public void envioCredenciales(UsuarioDTO usuario, String contraseña) {
        log.info("Enviando correo a {}", usuario.getCorreo());
        try {
            var mensaje = credenciales(usuario.getNombreCompleto(), usuario.getNombreUsuario(), contraseña, usuario.getCorreo());
            String sql = "EXEC msdb.dbo.sp_send_dbmail " +
                    "@profile_name = ?, " +
                    "@recipients = ?, " +
                    "@subject = ?, " +
                    "@body = ?, " +
                    "@body_format = 'HTML'";

            jdbcTemplate.update(sql, perfilCorreo, usuario.getCorreo(), "Bienvenido al Sistema de Gestión de Inventario", mensaje);
            log.info("Correo enviado correctamente");
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el cuerpo del correo", e);
        }
    }

    private String credenciales(String nombreCompleto, String nombreUsuario, String contrasena, String correo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("templates/credenciales-template.html")))) {

            String template = reader.lines().collect(Collectors.joining("\n"));

            template = template.replace("{{nombreCompleto}}", nombreCompleto);
            template = template.replace("{{nombreUsuario}}", nombreUsuario);
            template = template.replace("{{contrasena}}", contrasena);
            template = template.replace("{{correo}}", correo);
            template = template.replace("{{portalUrl}}", portalUrl);

            return template;
        }
    }

    public void envioRestablecerClave(Usuario usuario, String token) {
        log.info("Enviando  de restableciemiento de contraseña a correo a {}", usuario.getCorreo());
        try {
            var mensaje = restablecerClave(usuario.getNombreCompleto(),token);
            String sql = "EXEC msdb.dbo.sp_send_dbmail " +
                    "@profile_name = ?, " +
                    "@recipients = ?, " +
                    "@subject = ?, " +
                    "@body = ?, " +
                    "@body_format = 'HTML'";

            jdbcTemplate.update(sql, perfilCorreo, usuario.getCorreo(), "Bienvenido al Sistema de Gestión de Inventario", mensaje);
            log.info("Correo enviado correctamente");
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el cuerpo del correo", e);
        }
    }

    private String restablecerClave(String nombreCompleto, String token) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("templates/restablecerClave-template.html")))) {

            String template = reader.lines().collect(Collectors.joining("\n"));
            String resetLink = portalUrl.replace("/login", "") + "/restablecerContrasena/" + token;

            template = template.replace("{{nombreCompleto}}", nombreCompleto);
            template = template.replace("{{portalUrl}}", resetLink);

            return template;
        }
    }
}
