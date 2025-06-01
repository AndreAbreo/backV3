package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.UsuarioDto;
import codigocreativo.uy.servidorapp.entidades.Usuario;
import codigocreativo.uy.servidorapp.enumerados.Estados;
import jakarta.ejb.Remote;

import java.util.List;

@Remote
public interface UsuarioRemote {
    void crearUsuario(UsuarioDto u);
    void modificarUsuario(UsuarioDto u);
    void eliminarUsuario(UsuarioDto u);
    UsuarioDto obtenerUsuario(Long id);
    UsuarioDto obtenerUsuarioDto(Long id);
    UsuarioDto obtenerUsuarioPorCI(String ci);
    List<UsuarioDto> obtenerUsuarios();
    List<UsuarioDto> obtenerUsuariosFiltrado(String filtro, Object valor);
    UsuarioDto login(String usuario, String password);
    List<UsuarioDto> obtenerUsuariosPorEstado(Estados estado);
    UsuarioDto findUserByEmail(String email);
    boolean existeCI(String cedula);
    boolean existeMail(String email);
}
