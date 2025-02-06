package codigocreativo.uy.servidorapp.ws;

import codigocreativo.uy.servidorapp.DTO.ModelosEquipoDto;
import codigocreativo.uy.servidorapp.DTO.UbicacionDto;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import codigocreativo.uy.servidorapp.servicios.InstitucionRemote;
import codigocreativo.uy.servidorapp.servicios.UbicacionRemote;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/ubicaciones")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UbicacionesResource {
    @EJB
    private UbicacionRemote ur;

    @EJB
    private InstitucionRemote ir;

    @POST
    @Path("/crear")
    public Response crearUbicacion(UbicacionDto ubicacion) {
        try {
            this.ur.crearUbicacion(ubicacion);
            return Response.status(201).build();
        }catch(ServiciosException e) {
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/modificar")
    public Response modificarUbicacion(UbicacionDto ubicacion) {
        try {
            this.ur.modificarUbicacion(ubicacion);
            return Response.status(201).build();
        }catch(ServiciosException e) {
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/inactivar")
    public Response inactivarUbicacion(UbicacionDto ubicacion) {
        try {
            this.ur.bajaLogicaUbicacion(ubicacion);
            return Response.status(201).build();
        }catch(ServiciosException e) {
            return Response.status(500).build();
        }
    }


    @GET
    @Path("/listarXInstitucion")
    public Response listarUbicacionXInstitucion(@QueryParam("idInstitucion") Long idInstitucion) {
        try {
            List<UbicacionDto> ubicacion = this.ur.obtenerUbicacionesXInstitucion(idInstitucion);
            return Response.ok(ubicacion).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al buscar la ubicación para dicha institución: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/listarUbicaciones")
    public Response listarUbicaciones() {
        try{
            List<UbicacionDto> ubicaciones = this.ur.listarUbicaciones();
            return Response.ok(ubicaciones).build();
        }catch (ServiciosException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al listar las ubicaciones: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/buscar")
    public Response buscarUbicacion(@QueryParam("id") Long id) {
        try {
            UbicacionDto ubicacion = this.ur.obtenerUbicacionPorId(id);
            return Response.ok(ubicacion).build(); // Devuelve un 200 OK con la ubicación
        } catch (ServiciosException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al buscar la ubicación: " + e.getMessage()).build();
        }
    }
}
