package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.PaisDto;
import codigocreativo.uy.servidorapp.DTO.UbicacionDto;
import codigocreativo.uy.servidorapp.entidades.MarcasModelo;
import codigocreativo.uy.servidorapp.entidades.Pais;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import jakarta.ejb.Remote;

import java.util.List;
@Remote
public interface PaisRemote {
    void crearPais(PaisDto pais)  throws ServiciosException;
    void modificarPais(PaisDto pais)  throws ServiciosException;
    List<PaisDto> obtenerpais()  throws ServiciosException;
    PaisDto obtenerPaisPorId(Long id)  throws ServiciosException;
    void bajaLogicaPais(PaisDto pais)  throws ServiciosException;
}
