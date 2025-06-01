package codigocreativo.uy.servidorapp.ws;

import codigocreativo.uy.servidorapp.DTO.UbicacionDto;
import codigocreativo.uy.servidorapp.DTO.UsuariosTelefonoDto;
import codigocreativo.uy.servidorapp.entidades.UsuariosTelefono;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import codigocreativo.uy.servidorapp.servicios.UsuariosTelefonoBean;
import codigocreativo.uy.servidorapp.servicios.UsuariosTelefonoRemote;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/telefonos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TelefonosResource {

    @EJB
    private UsuariosTelefonoRemote tr;

    @POST
    @Path("/crear")
    public Response crearTelefono(UsuariosTelefonoDto telefono) {
        try {
            this.tr.crearTelefono(telefono);
            return Response.status(201).build();
        }catch(ServiciosException e) {
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/modificar")
    public Response modificarTelefono(UsuariosTelefonoDto telefono) {
        try {
            this.tr.modificarTelefono(telefono);
            return Response.status(201).build();
        }catch(ServiciosException e) {
            return Response.status(500).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarTelefono(@PathParam("id") Long id) {
        try {
            this.tr.eliminarTelefono(id);
            return Response.ok().build();
        } catch (ServiciosException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al eliminar teléfono").build();
        }
    }

    @GET
    @Path("/buscar")
    public Response buscarTelefono(@QueryParam("id") Long id) {
        try {
            UsuariosTelefonoDto telefono = this.tr.obtenerTelefono(id);
            return Response.ok(telefono).build();
        } catch (ServiciosException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al buscar el telefono: " + e.getMessage()).build();
        }
    }
}
