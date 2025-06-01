package codigocreativo.uy.servidorapp.DTOMappers;

import codigocreativo.uy.servidorapp.DTO.UbicacionDto;
import codigocreativo.uy.servidorapp.DTO.UsuariosTelefonoDto;
import codigocreativo.uy.servidorapp.entidades.Ubicacion;
import codigocreativo.uy.servidorapp.entidades.UsuariosTelefono;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {UsuarioMapper.class})
public interface UsuariosTelefonoMapper {
    UsuariosTelefono toEntity(UsuariosTelefonoDto usuariosTelefonoDto);
    UsuariosTelefono toEntity(UsuariosTelefonoDto usuariosTelefonoDto, @Context CycleAvoidingMappingContext context);

    UsuariosTelefonoDto toDto(UsuariosTelefono usuariosTelefono);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UsuariosTelefono partialUpdate(UsuariosTelefonoDto usuariosTelefonoDto, @MappingTarget UsuariosTelefono usuariosTelefono);

    List<UsuariosTelefono> toEntity(List<UsuariosTelefonoDto> usuariosTelefonoDto);

    List<UsuariosTelefonoDto> toDto(List<UsuariosTelefono> usuariosTelefono);
}