package codigocreativo.uy.servidorapp.JWT;

import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtTokenFilter implements ContainerRequestFilter {

    private static final Dotenv dotenv = Dotenv.load();
    private final String secret = dotenv.get("JWT_SECRET");

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("Ejecutando JwtTokenFilter...");
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        String path = requestContext.getUriInfo().getPath();
        System.out.println("Obteniendo path: " + path);

        // Permitir acceso sin token JWT a endpoints específicos
        if (path.startsWith("/usuarios/login") || path.startsWith("/usuarios/google-login") ||
                path.startsWith("/usuarios/crear") || path.startsWith("/api/status")) {
            return;  // Permitir acceso sin token JWT
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("No se encontró encabezado de autorización o no comienza con 'Bearer'");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            if (!isTokenValid(token)) {
                System.out.println("Token inválido.");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        } catch (Exception e) {
            System.out.println("Error al validar el token: " + e.getMessage());
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();


            // Validar si el id_usuario existe en el token
            Long userId = claims.get("id_usuario", Long.class);
            if (userId == null) {
                System.out.println("id_usuario no presente en el token.");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println("Excepción en validación de token: " + e.getMessage());
            return false;
        }
    }
}