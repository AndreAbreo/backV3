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
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    @EJB
    private UsuarioRemote er;
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
            // Buscar usuario existente en la base de datos
            UsuarioDto usuarioExistente = er.obtenerUsuario(usuario.getId());

            if (usuarioExistente == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }

            // Actualizar los datos permitidos
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setCedula(usuario.getCedula());
            usuarioExistente.setEmail(usuario.getEmail());
            usuarioExistente.setEstado(usuario.getEstado());
            usuarioExistente.setFechaNacimiento(usuario.getFechaNacimiento());
            usuarioExistente.setIdInstitucion(usuario.getIdInstitucion());
            usuarioExistente.setIdPerfil(usuario.getIdPerfil());
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setNombreUsuario(usuario.getNombreUsuario());

            // ✅ Si la contraseña no se envía en la solicitud, mantener la existente
            if (usuario.getContrasenia() != null && !usuario.getContrasenia().isEmpty()) {
                usuarioExistente.setContrasenia(usuario.getContrasenia());
            }

            // Guardar los cambios
            er.modificarUsuario(usuarioExistente);

            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/eliminarTelefono/{id_telefono}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarTelefono(@PathParam("id_telefono") Long id) {
        System.out.println("📡 Recibida solicitud DELETE para eliminar teléfono con ID: " + id);

        try {
            boolean eliminado = er.eliminarTelefono(id);
            if (eliminado) {
                System.out.println("✅ Teléfono eliminado en la BD: " + id);
                return Response.status(Response.Status.NO_CONTENT).build(); // 🔥 204 No Content
            } else {
                System.out.println("⚠️ No se encontró el teléfono en la BD: " + id);
                return Response.status(Response.Status.NOT_FOUND).entity("Teléfono no encontrado").build();
            }
        } catch (Exception e) {
            System.out.println("❌ Error eliminando teléfono: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al eliminar teléfono: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("Inactivar")
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
        if (loginRequest == null) {
            System.out.println("Request null");
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Pedido de login nulo\"}").build();
        }
        UsuarioDto user = this.er.login(loginRequest.getUsuario(), loginRequest.getPassword());

        if (user != null) {

            String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getIdPerfil().getNombrePerfil());

            user = user.setContrasenia(null);
            LoginResponse loginResponse = new LoginResponse(token, user);
            System.out.println("Ingreso correcto");
            System.out.println(Response.ok(loginResponse).build());
            return Response.ok(loginResponse).build();
        } else {
            System.out.println("login unautorized invalid credentials");
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Datos de acceso incorrectos\"}").build();
        }
    }

    @POST
@Path("/google-login")
public Response googleLogin(GoogleLoginRequest googleLoginRequest) {
    if (googleLoginRequest == null) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Pedido de login nulo\"}").build();
    }

    UsuarioDto user = this.er.findUserByEmail(googleLoginRequest.getEmail());
    boolean userNeedsAdditionalInfo = false;

    if (user == null) {
        user = new UsuarioDto();
        user.setEmail(googleLoginRequest.getEmail());
        user.setNombre(googleLoginRequest.getName());
        userNeedsAdditionalInfo = true;
    } else if (!user.getEstado().equals(Estados.ACTIVO)) {
        return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"Cuenta inactiva, por favor contacte al administrador\"}").build();
    }
    user = user.setContrasenia(null);
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getIdPerfil().getNombrePerfil());
    GoogleLoginResponse loginResponse = new GoogleLoginResponse(token, userNeedsAdditionalInfo, user);
    return Response.ok(loginResponse).build();

}


    public static class LoginRequest {
        private String usuario;
        private String password;

        public String getUsuario() {
            return usuario;
        }

        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        private String token;
        private UsuarioDto user;

        public LoginResponse(String token, UsuarioDto user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UsuarioDto getUser() {
            return user;
        }

        public void setUser(UsuarioDto user) {
            this.user = user;
        }
    }

    public static class GoogleLoginRequest {
        private String email;
        private String name;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class GoogleLoginResponse {
        private String token;
        private boolean userNeedsAdditionalInfo;
        private UsuarioDto user; // Añade este campo

        // Constructor
        public GoogleLoginResponse(String token, boolean userNeedsAdditionalInfo, UsuarioDto user) {
            this.token = token;
            this.userNeedsAdditionalInfo = userNeedsAdditionalInfo;
            this.user = user;
        }

        // Getters y setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isUserNeedsAdditionalInfo() {
            return userNeedsAdditionalInfo;
        }

        public void setUserNeedsAdditionalInfo(boolean userNeedsAdditionalInfo) {
            this.userNeedsAdditionalInfo = userNeedsAdditionalInfo;
        }

        public UsuarioDto getUser() {
            return user;
        }

        public void setUser(UsuarioDto user) {
            this.user = user;
        }
    }
}
