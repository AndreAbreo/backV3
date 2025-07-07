package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.EquiposUbicacioneDto;
import codigocreativo.uy.servidorapp.DTOMappers.EquiposUbicacioneMapper;
import codigocreativo.uy.servidorapp.entidades.EquiposUbicacione;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class EquiposUbicacioneBean implements EquiposUbicacioneRemote {
    @PersistenceContext(unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    EquiposUbicacioneMapper equiposUbicacioneMapper;

    @Override
    public void crearEquiposUbicacione(EquiposUbicacioneDto equiposUbicacione) {
        em.persist(equiposUbicacioneMapper.toEntity(equiposUbicacione, new codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext()));
        em.flush();
    }

    @Override
    public List<EquiposUbicacioneDto> obtenerEquiposUbicacione() {
        return equiposUbicacioneMapper.toDto(em.createQuery("SELECT equiposUbicacione FROM EquiposUbicacione equiposUbicacione", EquiposUbicacione.class).getResultList(), new codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext());
    }

    @Override
    public List<EquiposUbicacioneDto> obtenerEquiposUbicacionePorEquipo(Long id) {
        return equiposUbicacioneMapper.toDto(em.createQuery("SELECT equiposUbicacione FROM EquiposUbicacione equiposUbicacione WHERE equiposUbicacione.idEquipo.id = :id", EquiposUbicacione.class)
                .setParameter("id", id)
                .getResultList(), new codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext());
    }
}
