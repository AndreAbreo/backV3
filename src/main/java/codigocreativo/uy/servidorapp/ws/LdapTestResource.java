package codigocreativo.uy.servidorapp.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import codigocreativo.uy.servidorapp.JWT.LdapService;

@Path("/ldap")
public class LdapTestResource {

    private final LdapService ldapService;

    public LdapTestResource() {
        try {
            ldapService = new LdapService();
        } catch (RuntimeException e) {
            throw new WebApplicationException("Error inicializando LdapService: " + e.getMessage(), 500);
        }
    }

    @GET
    @Path("/check-user/{usuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkUser(@PathParam("usuario") String usuario) {
        try {
            // Se busca por userPrincipalName (correo completo)
            boolean existe = ldapService.usuarioExistePorPrincipal(usuario);
            return Response.status(existe ? 200 : 404)
                    .entity("{\"usuarioExiste\": " + existe + "}")
                    .build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity("{\"error\": \"Fallo en verificacion LDAP: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    // Proximamente: autenticacion contra el AD (bind directo)

}




