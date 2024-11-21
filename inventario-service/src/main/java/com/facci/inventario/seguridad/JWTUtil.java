package com.facci.inventario.seguridad;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JWTUtil {

    private final Key secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public JWTUtil(@Value("${jwt.secret}") String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Token no soportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Token mal formado: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Firma inválida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Token nulo o vacío: " + e.getMessage());
        }
        return false;
    }


    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        return (List<String>) Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token).getBody().get("roles");
    }
}
