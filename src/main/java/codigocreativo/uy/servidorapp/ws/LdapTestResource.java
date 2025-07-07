package codigocreativo.uy.servidorapp.ws;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Path("/auth/ldap")
public class LdapTestResource {

    public static class LdapCredentials {
        public String usuario;
        public String password;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response autenticarConLdap(LdapCredentials cred) {
        if (cred.usuario == null || cred.password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Credenciales faltantes").build();
        }

        try {
            autenticarLDAP(cred.usuario, cred.password);
            return Response.ok("Autenticación LDAP exitosa").build();
        } catch (NamingException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Autenticación fallida: " + e.getMessage()).build();
        }
    }

    private void autenticarLDAP(String usuario, String password) throws NamingException {
        String ldapUrl = System.getenv("LDAP_URL");
        String domain = System.getenv("LDAP_DOMAIN");

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        if (usuario.contains("@")) {
            env.put(Context.SECURITY_PRINCIPAL, usuario);
        } else {
            env.put(Context.SECURITY_PRINCIPAL, domain + "\\" + usuario);
        }

        env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext ctx = new InitialDirContext(env);
        ctx.close();
    }
}


