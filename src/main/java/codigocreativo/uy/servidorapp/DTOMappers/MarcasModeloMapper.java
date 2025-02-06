package codigocreativo.uy.servidorapp.DTOMappers;

import codigocreativo.uy.servidorapp.DTO.MarcasModeloDto;
import codigocreativo.uy.servidorapp.DTO.ModelosEquipoDto;
import codigocreativo.uy.servidorapp.entidades.MarcasModelo;
import codigocreativo.uy.servidorapp.entidades.ModelosEquipo;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface MarcasModeloMapper {

    MarcasModelo toEntity(MarcasModeloDto marcasModeloDto);

    MarcasModeloDto toDto(MarcasModelo marcasModelo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MarcasModelo partialUpdate(MarcasModeloDto marcasModeloDto, @MappingTarget MarcasModelo marcasModelo);

    List<MarcasModelo> toEntity(List<MarcasModeloDto> marcasModeloDto);

    List<MarcasModeloDto> toDtoMarcas(List<MarcasModelo> marcasModelo);
    List<ModelosEquipoDto> toDtoModelos(List<ModelosEquipo> modelosEquipo);

    ModelosEquipoDto toDto(ModelosEquipo modelo);

    List<MarcasModeloDto> toDto(List<MarcasModelo> selectMarcasModeloFromMarcasModeloMarcasModelo);
}