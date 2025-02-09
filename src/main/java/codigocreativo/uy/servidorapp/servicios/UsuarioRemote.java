package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.UsuarioDto;
import codigocreativo.uy.servidorapp.entidades.Usuario;
import codigocreativo.uy.servidorapp.enumerados.Estados;
import jakarta.ejb.Remote;

import java.util.List;

@Remote
public interface UsuarioRemote {
    public void crearUsuario(UsuarioDto u);
    public void modificarUsuario(UsuarioDto u);
    public void eliminarUsuario(UsuarioDto u);
    public UsuarioDto obtenerUsuario(Long id);
    public UsuarioDto obtenerUsuarioDto(Long id);
    public UsuarioDto obtenerUsuarioPorCI(String ci);
    public List<UsuarioDto> obtenerUsuarios();
    public List<UsuarioDto> obtenerUsuariosFiltrado(String filtro, Object valor);
    public UsuarioDto login(String usuario, String password);
    public List<UsuarioDto> obtenerUsuariosPorEstado(Estados estado);
    public UsuarioDto findUserByEmail(String email);
    public boolean eliminarTelefono(Long id);
}
