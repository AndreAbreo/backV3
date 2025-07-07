package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.IntervencionDto;
import codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext;
import codigocreativo.uy.servidorapp.DTOMappers.IntervencionMapper;
import codigocreativo.uy.servidorapp.entidades.*;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Context;

import java.util.List;

@Stateless
public class IntervencionBean implements IntervencionRemote {
    @PersistenceContext(unitName = "servidorappPU")
    private EntityManager em;

    @Inject //Se inyecta el mapper
    private IntervencionMapper intervencionMapper;

    @Override
    public void crear(IntervencionDto intervencion) throws ServiciosException {
        //Se "transforma" el DTO a una entidad
        Intervencion intervencionEntity = intervencionMapper.toEntity(intervencion, new CycleAvoidingMappingContext());

        //Se persiste la entidad (no el DTO)
        em.persist(intervencionEntity);
    }

    @Override
    public void actualizar(IntervencionDto intervencion) throws ServiciosException {
        //Se "transforma" el DTO a una entidad
        Intervencion intervencionEntity = intervencionMapper.toEntity(intervencion, new CycleAvoidingMappingContext());

        //Se actualiza la entidad (no el DTO)
        em.merge(intervencionEntity);
    }


    @Override
    public List<IntervencionDto> obtenerTodas() throws ServiciosException {
        //Se obtienen todas las entidades
        List<Intervencion> intervenciones = em.createQuery("SELECT i FROM Intervencion i", Intervencion.class).getResultList();

        //Se transforman la lista de entidades en una lista de DTOs (hay un metodo que recibe una lista y devuelve otra lista ya transformada)
        return intervencionMapper.toDto(intervenciones, new CycleAvoidingMappingContext());
    }
}
