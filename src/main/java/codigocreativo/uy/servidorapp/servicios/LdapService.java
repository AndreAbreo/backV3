package codigocreativo.uy.servidorapp.servicios;

import jakarta.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Stateless
public class LdapService {

    public boolean authenticate(String username, String password) {
        // Si viene en formato mail, se convierte a dominio
        String userPrincipal = username;
        if (username.contains("@hospital.local")) {
            userPrincipal = "HOSPITAL\\" + username.split("@")[0];
        }

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://192.168.100.35:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
            return true;
        } catch (NamingException e) {
            System.out.println("LDAP login failed for " + userPrincipal + ": " + e.getMessage());
            return false;
        }
    }
}

