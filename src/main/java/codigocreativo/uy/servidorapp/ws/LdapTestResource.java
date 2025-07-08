package codigocreativo.uy.servidorapp.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import codigocreativo.uy.servidorapp.JWT.LdapService;

@Path("/ldap")
public class LdapTestResource {

    private final LdapService ldapService = new LdapService();

    @GET
    @Path("/check-user/{usuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkUser(@PathParam("usuario") String usuario) {
        boolean existe = ldapService.usuarioExiste(usuario);
        return Response.status(existe ? 200 : 404)
                .entity("{\"usuarioExiste\": " + existe + "}")
                .build();
    }

    // Proximamente: autenticacion contra el AD (bind directo)

}



