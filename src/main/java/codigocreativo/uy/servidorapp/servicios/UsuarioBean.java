package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.LoginRequest;
import codigocreativo.uy.servidorapp.DTO.LoginResponse;
import codigocreativo.uy.servidorapp.DTO.UsuarioDto;
import codigocreativo.uy.servidorapp.JWT.JwtService;
import codigocreativo.uy.servidorapp.DTOMappers.UsuarioMapper;
import codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext;
import codigocreativo.uy.servidorapp.entidades.Usuario;
import codigocreativo.uy.servidorapp.enumerados.Estados;
import codigocreativo.uy.servidorapp.servicios.UsuarioRemote;

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
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Hashtable;
import java.util.List;

@Stateless
public class UsuarioBean implements UsuarioRemote {

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

    @Override
    public void crearUsuario(UsuarioDto u) {
        em.persist(usuarioMapper.toEntity(u, new CycleAvoidingMappingContext()));
        em.flush();
    }

    @Override
    public void modificarUsuario(UsuarioDto u) {
        em.merge(usuarioMapper.toEntity(u, new CycleAvoidingMappingContext()));
        em.flush();
    }

    @Override
    public void eliminarUsuario(UsuarioDto u) {
        Usuario entity = usuarioMapper.toEntity(u, new CycleAvoidingMappingContext());
        entity.setEstado(Estados.INACTIVO);
        em.merge(entity);
        em.flush();
    }

    @Override
    public UsuarioDto obtenerUsuario(Long id) {
        return usuarioMapper.toDto(em.find(Usuario.class, id), new CycleAvoidingMappingContext());
    }

    @Override
    public UsuarioDto obtenerUsuarioDto(Long id) {
        return obtenerUsuario(id);
    }

    @Override
    public UsuarioDto obtenerUsuarioPorCI(String ci) {
        Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.cedula = :ci", Usuario.class)
                .setParameter("ci", ci)
                .getResultStream()
                .findFirst()
                .orElse(null);
        return usuario != null ? usuarioMapper.toDto(usuario, new CycleAvoidingMappingContext()) : null;
    }

    @Override
    public List<UsuarioDto> obtenerUsuarios() {
        return usuarioMapper.toDto(
                em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList(),
                new CycleAvoidingMappingContext());
    }

    @Override
    public List<UsuarioDto> obtenerUsuariosFiltrado(String filtro, Object valor) {
        return usuarioMapper.toDto(
                em.createQuery("SELECT u FROM Usuario u WHERE u." + filtro + " = :valor", Usuario.class)
                        .setParameter("valor", valor)
                        .getResultList(),
                new CycleAvoidingMappingContext());
    }

    @Override
    public UsuarioDto login(String usuario, String password) {
        if (usuario == null || usuario.isEmpty() || password == null || password.isEmpty()) {
            throw new WebApplicationException("Usuario y contraseña requeridos", Response.Status.BAD_REQUEST);
        }

        if (usuario.contains("@") || usuario.contains("\\")) {
            try {
                autenticarLDAP(usuario, password);
            } catch (NamingException e) {
                throw new WebApplicationException("Credenciales inválidas o usuario no pertenece al dominio", Response.Status.UNAUTHORIZED);
            }
        }

        Usuario user = em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :usuario", Usuario.class)
                .setParameter("usuario", usuario)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new WebApplicationException("Usuario no encontrado", Response.Status.UNAUTHORIZED));

        if (!Estados.ACTIVO.equals(user.getEstado())) {
            throw new WebApplicationException("Cuenta inactiva", Response.Status.UNAUTHORIZED);
        }

        UsuarioDto dto = usuarioMapper.toDto(user, new CycleAvoidingMappingContext());
        dto.setContrasenia(null);
        return dto;
    }

    @Override
    public List<UsuarioDto> obtenerUsuariosPorEstado(Estados estado) {
        return usuarioMapper.toDto(
                em.createQuery("SELECT u FROM Usuario u WHERE u.estado = :estado", Usuario.class)
                        .setParameter("estado", estado)
                        .getResultList(),
                new CycleAvoidingMappingContext());
    }

    @Override
    public UsuarioDto findUserByEmail(String email) {
        Usuario user = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
        return user != null ? usuarioMapper.toDto(user, new CycleAvoidingMappingContext()) : null;
    }

    @Override
    public boolean existeCI(String cedula) {
        Long count = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.cedula = :cedula", Long.class)
                .setParameter("cedula", cedula)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existeMail(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count != null && count > 0;
    }

    private void autenticarLDAP(String usuario, String password) throws NamingException {
        Dotenv dotenv = Dotenv.load();
        String ldapUrl = dotenv.get("LDAP_URL");
        String domain = dotenv.get("LDAP_DOMAIN");

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



