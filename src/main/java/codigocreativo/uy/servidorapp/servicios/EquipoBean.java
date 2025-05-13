package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.BajaEquipoDto;
import codigocreativo.uy.servidorapp.DTO.EquipoDto;
import codigocreativo.uy.servidorapp.DTO.UbicacionDto;
import codigocreativo.uy.servidorapp.DTOMappers.BajaEquipoMapper;
import codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext;
import codigocreativo.uy.servidorapp.DTOMappers.EquipoMapper;
import codigocreativo.uy.servidorapp.entidades.Equipo;
import codigocreativo.uy.servidorapp.entidades.Ubicacion;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
@Stateless
public class EquipoBean implements EquipoRemote {
    @PersistenceContext (unitName = "default")
    private EntityManager em;

    @Inject
    EquipoMapper equipoMapper;

    @Inject
    BajaEquipoMapper bajaEquipoMapper;

    //se cambia em.persist() por em.merge()
    @Override
    public void crearEquipo(EquipoDto equipo) {
        em.persist(equipoMapper.toEntity(equipo, new CycleAvoidingMappingContext()));
        em.flush();
    }

    @Override
    public void modificarEquipo(EquipoDto equipo) {
        em.merge(equipoMapper.toEntity(equipo, new CycleAvoidingMappingContext()));
        em.flush();
    }

    @Override
    public boolean existeIdInterno(String idInterno) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Equipo e WHERE e.idInterno = :idInterno", Long.class)
                    .setParameter("idInterno", idInterno)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean existeNroSerie(String nroSerie) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Equipo e WHERE e.nroSerie = :nroSerie", Long.class)
                    .setParameter("nroSerie", nroSerie)
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    //se cambia em.persist() por em.merge()
    @Override
    public void eliminarEquipo(BajaEquipoDto bajaEquipo) {
        em.merge(bajaEquipoMapper.toEntity(bajaEquipo, new CycleAvoidingMappingContext()));
        em.createQuery("UPDATE Equipo equipo SET equipo.estado = 'INACTIVO' WHERE equipo.id = :id")
                .setParameter("id", bajaEquipo.getIdEquipo().getId())
                .executeUpdate();
        em.flush();
    }

    @Override
    public List<EquipoDto> obtenerEquiposFiltrado(String filtro, String valor) {
        return equipoMapper.toDto(em.createQuery("SELECT e FROM Equipo e WHERE e." + filtro + " = :valor", Equipo.class)
                .setParameter("valor", valor)
                .getResultList(), new CycleAvoidingMappingContext());
    }

    @Override
    public EquipoDto obtenerEquipo(Long id) {
        return equipoMapper.toDto(em.find(Equipo.class, id), new CycleAvoidingMappingContext());
    }



    @Override
    public List<EquipoDto> listarEquipos() {
        return equipoMapper.toDto(em.createQuery("SELECT equipo FROM Equipo equipo", Equipo.class).getResultList(), new CycleAvoidingMappingContext());
    }
}
