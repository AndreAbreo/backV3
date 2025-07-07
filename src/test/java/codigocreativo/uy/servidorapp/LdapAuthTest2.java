package codigocreativo.uy.servidorapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.*;

public class LdapAuthTest2 {

    static Dotenv dotenv;

    @BeforeAll
    public static void setup() {
        dotenv = Dotenv.configure()
                .filename(".env.test")
                .ignoreIfMissing()
                .load();
    }

    @Test
    public void testLdapAuth_OK() {
        Hashtable<String, String> env = buildLdapEnv(
                dotenv.get("LDAP_EMAIL"),
                dotenv.get("LDAP_PASSWORD")
        );

        assertDoesNotThrow(() -> {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
        });
    }

    @Test
    public void testLdapAuth_Fallo() {
        Hashtable<String, String> env = buildLdapEnv(
                dotenv.get("LDAP_EMAIL"),
                "claveIncorrecta"
        );

        assertThrows(NamingException.class, () -> {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
        });
    }

    private Hashtable<String, String> buildLdapEnv(String user, String pass) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, dotenv.get("LDAP_URL"));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, pass);
        return env;
    }
}
