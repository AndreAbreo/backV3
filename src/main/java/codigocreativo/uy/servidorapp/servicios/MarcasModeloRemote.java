package codigocreativo.uy.servidorapp.servicios;
import codigocreativo.uy.servidorapp.DTO.MarcasModeloDto;
import codigocreativo.uy.servidorapp.DTO.ModelosEquipoDto;
import codigocreativo.uy.servidorapp.entidades.MarcasModelo;
import jakarta.ejb.Remote;

import java.util.List;
@Remote
public interface MarcasModeloRemote {
    public void crearMarcasModelo(MarcasModeloDto marcasModelo);
    public void modificarMarcasModelo(MarcasModeloDto marcasModelo);
    public MarcasModeloDto obtenerMarca(Long id);
    public List<MarcasModeloDto> obtenerMarcasLista();
    public List<ModelosEquipoDto> obtenerModeloXMarca(Long idMarca);
}
