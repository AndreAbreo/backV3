package codigocreativo.uy.servidorapp.ws;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/status")
public class StatusServer {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkStatus() {
        return Response.ok("{\"status\":\"up\"}").build();
    }
}