package codigocreativo.uy.servidorapp.ws;


import codigocreativo.uy.servidorapp.DTO.*;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import codigocreativo.uy.servidorapp.servicios.*;
import com.sun.jdi.PrimitiveValue;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/equipovinculaciones")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EquipoVinculacionesResources {

    @EJB
    private ProveedoresEquipoRemote proveedoresRemote;

    @EJB
    private TiposEquipoRemote tiposRemote;

    @EJB
    private MarcasModeloRemote marcaRemote;

    @EJB
    private PaisRemote paisRemote;

    @EJB
    private  InstitucionRemote institucionRemote;


    @GET
    @Path("/listaProveedores")
    public List<ProveedoresEquipoDto> listaProveedores() {
        return this.proveedoresRemote.obtenerProveedoresEquipo();
    }

    @GET
    @Path("listaTipoEquipo")
    public List<TiposEquipoDto> listaTiposEquipo() {
        return this.tiposRemote.listarTiposEquipo();
    }

    @GET
    @Path("listaMarca")
    public List<MarcasModeloDto> listaMarca() {
        return this.marcaRemote.obtenerMarcasLista();
    }

    @GET
    @Path("listaModeloXMarca")
    public List<ModelosEquipoDto> listaModeloXMarca(@QueryParam("idMarca") Long idMarca) {
        return this.marcaRemote.obtenerModeloXMarca(idMarca);
    }

    @GET
    @Path("listaInstituciones")
    public List<InstitucionDto> listaInstituciones() {
        return this.institucionRemote.obtenerInstituciones();
    }
}
