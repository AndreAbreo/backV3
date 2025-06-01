package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.UsuariosTelefonoDto;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import jakarta.ejb.Remote;

import java.util.List;

@Remote
public interface UsuariosTelefonoRemote {
    void crearTelefono(UsuariosTelefonoDto telefono) throws ServiciosException;
    void modificarTelefono(UsuariosTelefonoDto telefono) throws ServiciosException;
    UsuariosTelefonoDto obtenerTelefono(Long id) throws ServiciosException;
    void eliminarTelefono(Long id) throws ServiciosException;
    List<UsuariosTelefonoDto> obtenerusuariosTelefono() throws ServiciosException;
}
