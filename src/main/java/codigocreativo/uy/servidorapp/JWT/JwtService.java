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

    public String generateToken(String email, Long userId, String perfil) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id_usuario", userId);
        claims.put("id_perfil", perfil);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 8L * 60 * 60 * 1000)) // 8 horas
                //.setExpiration(new Date(System.currentTimeMillis() + 10 * 1000)) // 10 segundos
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}