package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.EquipoDto;
import codigocreativo.uy.servidorapp.DTO.ModelosEquipoDto;
import codigocreativo.uy.servidorapp.DTO.UbicacionDto;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import jakarta.ejb.Remote;

import java.util.List;

@Remote
public interface UbicacionRemote {
    //Se crea la primer implementacion Registro de ubicaciones
    void crearUbicacion(UbicacionDto ubi) throws ServiciosException;

    void modificarUbicacion(UbicacionDto ubi) throws ServiciosException;

    void borrarUbicacion(Long id) throws ServiciosException;

    void moverEquipoDeUbicacion(EquipoDto equipo, UbicacionDto ubicacion) throws ServiciosException;

    List<UbicacionDto> listarUbicaciones() throws ServiciosException;

    UbicacionDto obtenerUbicacionPorId(Long id) throws ServiciosException;

    void bajaLogicaUbicacion(UbicacionDto ub) throws ServiciosException;

    public List<UbicacionDto> obtenerUbicacionesXInstitucion(Long idInstitucion);
}
