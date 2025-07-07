package codigocreativo.uy.servidorapp.ws;

import codigocreativo.uy.servidorapp.DTO.PerfilDto;
import codigocreativo.uy.servidorapp.DTO.UsuarioDto;
import codigocreativo.uy.servidorapp.enumerados.Estados;
import codigocreativo.uy.servidorapp.JWT.JwtService;
import codigocreativo.uy.servidorapp.servicios.PerfilRemote;
import codigocreativo.uy.servidorapp.servicios.UsuarioRemote;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    @EJB
    private UsuarioRemote er;

    private static final Dotenv dotenv = Dotenv.load();
    private static final String LDAP_URL = dotenv.get("LDAP_URL");
    private static final String LDAP_DOMAIN = dotenv.get("LDAP_DOMAIN");

    @EJB
    private JwtService jwtService;

    @EJB
    private PerfilRemote perfilRemote;

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
    @Path("/Inactivar")
    public Response inactivarUsuario(UsuarioDto usuario){
        this.er.eliminarUsuario(usuario);
        return Response.status(200).build();
    }

    @GET
    @Path("/BuscarUsuarioPorCI")
    public UsuarioDto buscarEquipo(@QueryParam("ci") String ci){
        return this.er.obtenerUsuarioPorCI(ci);
    }

    @GET
    @Path("/existeMail")
    public boolean existeMail(@QueryParam("email") String email){
        return this.er.existeMail(email);
    }

    @GET
    @Path("/existeCI")
    public boolean existeCI(@QueryParam("cedula") String cedula){
        return this.er.existeCI(cedula);
    }

    @GET
    @Path("/BuscarUsuarioPorId")
    public UsuarioDto buscarEquipo(@QueryParam("id") Long id){
        return this.er.obtenerUsuario(id);
    }

    @GET
    @Path("/ObtenerUsuarioPorEstado")
    public List<UsuarioDto> obtenerUsuarioPorEstado(@QueryParam("estado") Estados estado){
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
        System.out.println("LDAP_URL env: " + LDAP_URL);

        String email = loginRequest.getUsuario();
        String password = loginRequest.getPassword();

        System.out.println("\uD83D\uDD10 Intentando login con: " + email);

        if (email != null && email.toLowerCase().endsWith("@hospital.local")) {
            System.out.println("➡️ Usuario con dominio hospital.local, intentando autenticación LDAP...");

            try {
                Hashtable<String, String> env = new Hashtable<>();
                env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
                env.put("java.naming.provider.url", LDAP_URL);
                env.put("java.naming.security.authentication", "simple");
                if (email.contains("@")) {
                    env.put("java.naming.security.principal", email);
                } else if (LDAP_DOMAIN != null && !LDAP_DOMAIN.isEmpty()) {
                    env.put("java.naming.security.principal", LDAP_DOMAIN + "\\" + email);
                } else {
                    env.put("java.naming.security.principal", email);
                }
                env.put("java.naming.security.credentials", password);

                DirContext ctx = new InitialDirContext(env);
                ctx.close();

                System.out.println("✅ Autenticación LDAP exitosa.");
            } catch (Exception e) {
                System.out.println("❌ Falló autenticación LDAP: " + e.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Credenciales inválidas (LDAP)\"}")
                        .build();
            }
        }

        UsuarioDto user = this.er.findUserByEmail(email);

        if (user == null) {
            System.out.println("❌ Usuario no encontrado en la base de datos: " + email);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Usuario no encontrado\"}")
                    .build();
        }

        if (!user.getEstado().equals("ACTIVO")) {
            System.out.println("❌ Usuario inactivo: " + user.getEmail());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Cuenta inactiva\"}")
                    .build();
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getIdPerfil().getNombrePerfil());
        user = user.setContrasenia(null);

        System.out.println("✅ Login exitoso: " + user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", token);
        return Response.ok(response).build();
    }

    @POST
    @Path("/google-login")
    public Response googleLogin(GoogleLoginRequest googleLoginRequest) {
        if (googleLoginRequest == null || googleLoginRequest.getEmail() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Pedido de login nulo\"}").build();
        }

        UsuarioDto user = this.er.findUserByEmail(googleLoginRequest.getEmail());
        if (user == null) {
            return Response.ok(Map.of("userNeedsAdditionalInfo", true)).build();
        }
        if (!user.getEstado().equals(Estados.ACTIVO)) {
            return Response.status(Response.Status.FORBIDDEN).entity(Map.of("error", "Cuenta inactiva, por favor contacte al administrador")).build();
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getIdPerfil().getNombrePerfil());
        user = user.setContrasenia(null);
        GoogleLoginResponse loginResponse = new GoogleLoginResponse(token, false, user);
        return Response.ok(loginResponse).build();
    }

    public static class LoginRequest {
        private String usuario;
        private String password;

        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private UsuarioDto user;

        public LoginResponse(String token, UsuarioDto user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public UsuarioDto getUser() { return user; }
        public void setUser(UsuarioDto user) { this.user = user; }
    }

    public static class GoogleLoginRequest {
        private String email;
        private String name;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class GoogleLoginResponse {
        private String token;
        private boolean userNeedsAdditionalInfo;
        private UsuarioDto user;

        public GoogleLoginResponse(String token, boolean userNeedsAdditionalInfo, UsuarioDto user) {
            this.token = token;
            this.userNeedsAdditionalInfo = userNeedsAdditionalInfo;
            this.user = user;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public boolean isUserNeedsAdditionalInfo() { return userNeedsAdditionalInfo; }
        public void setUserNeedsAdditionalInfo(boolean userNeedsAdditionalInfo) { this.userNeedsAdditionalInfo = userNeedsAdditionalInfo; }
        public UsuarioDto getUser() { return user; }
        public void setUser(UsuarioDto user) { this.user = user; }
    }
}
