package codigocreativo.uy.servidorapp.ws;

import codigocreativo.uy.servidorapp.DTO.PerfilDto;
import codigocreativo.uy.servidorapp.DTO.UsuarioDto;
import codigocreativo.uy.servidorapp.DTO.LoginRequest;
import codigocreativo.uy.servidorapp.DTO.LoginResponse;
import codigocreativo.uy.servidorapp.enumerados.Estados;
import codigocreativo.uy.servidorapp.JWT.JwtService;
import codigocreativo.uy.servidorapp.servicios.PerfilRemote;
import codigocreativo.uy.servidorapp.servicios.UsuarioRemote;
import codigocreativo.uy.servidorapp.DTOMappers.UsuarioMapper;
import codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext;
import codigocreativo.uy.servidorapp.entidades.Usuario;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.servlet.http.HttpServletRequest;
import javax.naming.NamingException;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    @EJB
    private UsuarioRemote er;

    @Inject
    private JwtService jwtService;

    @EJB
    private PerfilRemote perfilRemote;

    @Inject
    private UsuarioMapper usuarioMapper;

    @PersistenceContext(unitName = "servidorappPU")
    private EntityManager em;

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/crear")
    public Response crearUsuario(UsuarioDto usuario) {
        this.er.crearUsuario(usuario);
        return Response.status(201).build();
    }

    @PUT
    @Path("/modificar")
    public Response modificarUsuario(UsuarioDto usuario) {
        try {
            UsuarioDto usuarioExistente = er.obtenerUsuario(usuario.getId());

            if (usuarioExistente == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }

            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setCedula(usuario.getCedula());
            usuarioExistente.setEmail(usuario.getEmail());
            usuarioExistente.setEstado(usuario.getEstado());
            usuarioExistente.setFechaNacimiento(usuario.getFechaNacimiento());
            usuarioExistente.setIdInstitucion(usuario.getIdInstitucion());
            usuarioExistente.setIdPerfil(usuario.getIdPerfil());
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setNombreUsuario(usuario.getNombreUsuario());

            if (usuario.getContrasenia() != null && !usuario.getContrasenia().isEmpty()) {
                usuarioExistente.setContrasenia(usuario.getContrasenia());
            }

            usuarioExistente.setUsuariosTelefonos(usuario.getUsuariosTelefonos());

            er.modificarUsuario(usuarioExistente);

            return Response.status(200).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("Inactivar")
    public Response inactivarUsuario(UsuarioDto usuario) {
        this.er.eliminarUsuario(usuario);
        return Response.status(200).build();
    }

    @GET
    @Path("/BuscarUsuarioPorCI")
    public UsuarioDto buscarEquipo(@QueryParam("ci") String ci) {
        return this.er.obtenerUsuarioPorCI(ci);
    }

    @GET
    @Path("/existeMail")
    public boolean existeMail(@QueryParam("email") String email) {
        return this.er.existeMail(email);
    }

    @GET
    @Path("/existeCI")
    public boolean existeCI(@QueryParam("cedula") String cedula) {
        return this.er.existeCI(cedula);
    }

    @GET
    @Path("/BuscarUsuarioPorId")
    public UsuarioDto buscarEquipo(@QueryParam("id") Long id) {
        return this.er.obtenerUsuario(id);
    }

    @GET
    @Path("/ObtenerUsuarioPorEstado")
    public List<UsuarioDto> obtenerUsuarioPorEstado(@QueryParam("estado") Estados estado) {
        return this.er.obtenerUsuariosPorEstado(estado);
    }

    @GET
    @Path("/ListarTodosLosUsuarios")
    public List<UsuarioDto> obtenerTodosLosUsuarios() {
        return this.er.obtenerUsuarios();
    }

    @GET
    @Path("/ListarPerfiles")
    public List<PerfilDto> obtenerPerfiles() {
        return this.perfilRemote.obtenerPerfiles();
    }

    @GET
    @Path("/obtenerUserEmail")
    public Response getUserByEmail(@QueryParam("email") String email) {
        UsuarioDto user = er.findUserByEmail(email);
        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsuario() == null || loginRequest.getUsuario().isEmpty()
                || loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Usuario y contraseña requeridos")
                    .build();
        }
        try {
            Usuario usuarioEntity = em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.nombreUsuario = :usuario", Usuario.class)
                    .setParameter("usuario", loginRequest.getUsuario())
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new WebApplicationException("Usuario no encontrado", Response.Status.UNAUTHORIZED));

            if (!Estados.ACTIVO.equals(usuarioEntity.getEstado())) {
                throw new WebApplicationException("Cuenta inactiva", Response.Status.UNAUTHORIZED);
            }

            autenticarLDAP(loginRequest.getUsuario(), loginRequest.getPassword());

            UsuarioDto userDto = usuarioMapper.toDto(usuarioEntity, new CycleAvoidingMappingContext());
            String token = jwtService.generateToken(
                    userDto.getEmail(), userDto.getId(), userDto.getIdPerfil().getNombrePerfil());
            userDto.setContrasenia(null);
            LoginResponse response = new LoginResponse(token, userDto);
            return Response.ok(response).build();

        } catch (NamingException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas o usuario no pertenece al dominio.")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error inesperado")
                    .build();
        }
    }

    private void autenticarLDAP(String usuario, String password) throws NamingException {
        Dotenv dotenv = Dotenv.load();
        String ldapURL = dotenv.get("LDAP_URL");
        String domain = dotenv.get("LDAP_DOMAIN");

        java.util.Hashtable<String, String> env = new java.util.Hashtable<>();
        env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(javax.naming.Context.PROVIDER_URL, ldapURL);
        env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "simple");
        env.put(javax.naming.Context.SECURITY_PRINCIPAL, domain + "\\" + usuario);
        env.put(javax.naming.Context.SECURITY_CREDENTIALS, password);

        new javax.naming.directory.InitialDirContext(env);
    }
}
