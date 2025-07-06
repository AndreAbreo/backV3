package codigocreativo.uy.servidorapp.servicios;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ejb.Stateless;

import jakarta.naming.Context;
import jakarta.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Stateless
public class LdapService {
    private static final Dotenv dotenv = Dotenv.load();
    private final String url = dotenv.get("LDAP_URL", "ldap://192.168.1.40:389");
    private final String domain = dotenv.get("LDAP_DOMAIN", "hospital.local");

    public boolean authenticate(String user, String pass) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, user + "@" + domain);
        env.put(Context.SECURITY_CREDENTIALS, pass);

        try {
            new InitialDirContext(env).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
