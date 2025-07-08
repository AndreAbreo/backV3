package codigocreativo.uy.servidorapp;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LdapUserCheck {
    public static void main(String[] args) {
        // Configuración LDAP
        String ldapURL = "ldap://192.168.1.41:389";
        String baseDN = "dc=hospital,dc=local";
        String userToSearch = "EmpleadoComun"; // Cambiar por el usuario a probar

        // Credenciales del usuario lector
        String adminUser = "Administrador@hospital.local";
        String adminPassword = "Admin1234!"; // <-- Asegurate de que esté correcta

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, adminUser);
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);

        try {
            DirContext ctx = new InitialDirContext(env);

            // Filtro para buscar por sAMAccountName
            String searchFilter = "(sAMAccountName=" + userToSearch + ")";
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(baseDN, searchFilter, controls);

            if (results.hasMore()) {
                SearchResult result = results.next();
                System.out.println("✅ Usuario encontrado: " + userToSearch);
                System.out.println("DistinguishedName: " + result.getNameInNamespace());
            } else {
                System.out.println("❌ Usuario NO encontrado: " + userToSearch);
            }

            ctx.close();
        } catch (Exception e) {
            System.err.println("⛔ Error de conexión o búsqueda: " + e.getMessage());
        }
    }
}

