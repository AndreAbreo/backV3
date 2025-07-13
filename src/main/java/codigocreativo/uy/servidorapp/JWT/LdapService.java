package codigocreativo.uy.servidorapp.JWT;

import javax.naming.*;
import javax.naming.directory.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

public class LdapService {

    private final String ldapURL;
    private final String baseDN;
    private final String adminUser;
    private final String adminPassword;

    public LdapService() {
        Properties props = new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("ldap.properties");
            if (input == null) {
                throw new RuntimeException("❌ No se encontró ldap.properties en resources");
            }
            props.load(input);


        } catch (IOException e) {
            throw new RuntimeException("❌ Error cargando .env: " + e.getMessage());
        }

        ldapURL = props.getProperty("LDAP_URL");
        baseDN = props.getProperty("LDAP_BASE_DN");
        adminUser = props.getProperty("LDAP_ADMIN_USER");
        adminPassword = props.getProperty("LDAP_ADMIN_PASS");
    }

    public boolean usuarioExistePorPrincipal(String userPrincipalName) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, adminUser);
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);

        try {
            DirContext ctx = new InitialDirContext(env);
            String searchFilter = "(&(userPrincipalName=" + userPrincipalName + ")(memberOf=CN=Grupo_App_Users,DC=hospital,DC=local))";

            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(baseDN, searchFilter, controls);
            ctx.close();

            return results.hasMore();
        } catch (NamingException e) {
            System.err.println("⛔ Error buscando en LDAP: " + e.getMessage());
            return false;
        }
    }

}


