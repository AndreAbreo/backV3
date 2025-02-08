package codigocreativo.uy.servidorapp.JWT;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.JwtException;
import jakarta.ejb.Stateless;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class JwtService {

    private static final Dotenv dotenv = Dotenv.load();
    private final String secret = dotenv.get("JWT_SECRET");

    public String generateToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id_usuario", userId); // Mantener solo el ID del usuario

        return Jwts.builder()
                .setClaims(claims) // Agregar datos adicionales al token
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)) // 30 días
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}