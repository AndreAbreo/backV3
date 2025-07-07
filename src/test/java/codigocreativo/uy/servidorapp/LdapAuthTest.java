package codigocreativo.uy.servidorapp;

import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.*;

public class LdapAuthTest {

    @Test
    public void testLdapAuth_OK() {
        String ldapUrl = "ldap://192.168.1.40:389"; // o System.getenv("LDAP_URL");
        String email = "EmpleadoComun@hospital.local";
        String password = "Admin1234!";

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, email);
        env.put(Context.SECURITY_CREDENTIALS, password);

        assertDoesNotThrow(() -> {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
        });
    }

    @Test
    public void testLdapAuth_Fallo() {
        String ldapUrl = "ldap://192.168.1.40:389";
        String email = "EmpleadoComun@hospital.local";
        String password = "claveIncorrecta";

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, email);
        env.put(Context.SECURITY_CREDENTIALS, password);

        assertThrows(NamingException.class, () -> {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
        });
    }
}

