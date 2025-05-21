package codigocreativo.uy.servidorapp.ws;

import codigocreativo.uy.servidorapp.DTO.PaisDto;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import codigocreativo.uy.servidorapp.servicios.PaisRemote;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/paises")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaisesResource {
    @EJB
    private PaisRemote pr;

    @POST
    @Path("/crear")
    public Response crearPais(PaisDto pais) {
        try {
            this.pr.crearPais(pais);
            return Response.status(201).build();
        }catch(ServiciosException e) {
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/modificar")
    public Response modificarPais(PaisDto pais) {
        try {
            this.pr.modificarPais(pais);
            return Response.status(201).build();
        }catch(ServiciosException e) {
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/inactivar")
    public Response inactivarPais(PaisDto pais) {
        try {
            this.pr.bajaLogicaPais(pais);
            return Response.status(201).build();
        }catch(ServiciosException e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/listarPaises")
    public Response listarPaises() {
        try{
            List<PaisDto> paises = this.pr.obtenerpais();
            return Response.ok(paises).build();
        }catch (ServiciosException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al listar los paises: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/buscar")
    public Response buscarPais(@QueryParam("id") Long id) {
        try {
            PaisDto pais = this.pr.obtenerPaisPorId(id);
            return Response.ok(pais).build();
        } catch (ServiciosException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al buscar el país: " + e.getMessage()).build();
        }
    }

}
