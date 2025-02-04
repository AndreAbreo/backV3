package codigocreativo.uy.servidorapp.DTOMappers;

import codigocreativo.uy.servidorapp.DTO.UbicacionDto;
import codigocreativo.uy.servidorapp.entidades.Ubicacion;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface UbicacionMapper {
    Ubicacion toEntity(UbicacionDto ubicacionDto, @Context CycleAvoidingMappingContext context);

    UbicacionDto toDto(Ubicacion ubicacion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Ubicacion partialUpdate(UbicacionDto ubicacionDto, @MappingTarget Ubicacion ubicacion);

    List<Ubicacion> toEntity(List<UbicacionDto> ubicacionDto);

    List<UbicacionDto> toDto(List<Ubicacion> ubicacion);
}