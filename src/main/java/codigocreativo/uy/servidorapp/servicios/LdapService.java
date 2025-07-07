package codigocreativo.uy.servidorapp.servicios;

import jakarta.ejb.Stateless;
import io.github.cdimascio.dotenv.Dotenv;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class LdapService {

    private static final Logger LOGGER = Logger.getLogger(LdapService.class.getName());

    public boolean authenticate(String username, String password) {
        Dotenv dotenv = Dotenv.load();
        String ldapUrl = dotenv.get("LDAP_URL");
        String domain = dotenv.get("LDAP_DOMAIN");

        String userPrincipal = username;
        if (username.contains("@")) {
            userPrincipal = domain + "\\" + username.split("@")[0];
        }

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
            return true;
        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, "LDAP login failed for {0}: {1}", new Object[]{userPrincipal, e.getMessage()});
            return false;
        }
    }
}

