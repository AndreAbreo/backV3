package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.LoginRequest;
import codigocreativo.uy.servidorapp.DTO.LoginResponse;
import codigocreativo.uy.servidorapp.DTO.UsuarioDto;
import codigocreativo.uy.servidorapp.JWT.JwtService;
import codigocreativo.uy.servidorapp.DTOMappers.UsuarioMapper;
import codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext;
import codigocreativo.uy.servidorapp.entidades.Usuario;
import codigocreativo.uy.servidorapp.enumerados.Estados;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import java.util.Hashtable;

@Stateless
public class UsuarioBean {

    @PersistenceContext(unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    private JwtService jwtService;

    @Inject
    private UsuarioMapper usuarioMapper;

    public LoginResponse login(LoginRequest req) {
        if (req == null || req.getUsuario() == null || req.getUsuario().isEmpty()
                || req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new WebApplicationException("Usuario y contraseña requeridos", Response.Status.BAD_REQUEST);
        }

        if (req.getUsuario().contains("@") || req.getUsuario().contains("\\")) {
            try {
                autenticarLDAP(req.getUsuario(), req.getPassword());
            } catch (NamingException e) {
                throw new WebApplicationException("Credenciales inválidas o usuario no pertenece al dominio", Response.Status.UNAUTHORIZED);
            }
        }

        Usuario user = em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :usuario", Usuario.class)
                .setParameter("usuario", req.getUsuario())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new WebApplicationException("Usuario no encontrado", Response.Status.UNAUTHORIZED));

        if (!Estados.ACTIVO.equals(user.getEstado())) {
            throw new WebApplicationException("Cuenta inactiva", Response.Status.UNAUTHORIZED);
        }

        UsuarioDto dto = usuarioMapper.toDto(user, new CycleAvoidingMappingContext());
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getIdPerfil().getNombrePerfil());
        dto.setContrasenia(null);
        return new LoginResponse(token, dto);
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



