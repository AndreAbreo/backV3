package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.BajaEquipoDto;
import codigocreativo.uy.servidorapp.DTO.EquipoDto;
import codigocreativo.uy.servidorapp.entidades.BajaEquipo;
import codigocreativo.uy.servidorapp.entidades.Equipo;
import jakarta.ejb.Remote;

import java.util.List;

@Remote
public interface EquipoRemote {
     void crearEquipo(EquipoDto equipo);
     void modificarEquipo(EquipoDto equipo);
     void eliminarEquipo(BajaEquipoDto bajaEquipo);

    List<EquipoDto> obtenerEquiposFiltrado(String filtro, String valor);

     EquipoDto obtenerEquipo(Long id);
     List<EquipoDto> listarEquipos();

     boolean existeIdInterno(String id);
     boolean existeNroSerie(String nroSerie);

}