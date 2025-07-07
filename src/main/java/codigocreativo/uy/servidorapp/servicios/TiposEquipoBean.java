package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.TiposEquipoDto;
import codigocreativo.uy.servidorapp.DTOMappers.TiposEquipoMapper;
import codigocreativo.uy.servidorapp.entidades.Pais;
import codigocreativo.uy.servidorapp.entidades.TiposEquipo;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class TiposEquipoBean implements TiposEquipoRemote{
    @PersistenceContext (unitName = "servidorappPU")
    private EntityManager em;
    @Inject
    private TiposEquipoMapper tiposEquipoMapper;


    @Override
    public void crearTiposEquipo(TiposEquipoDto tiposEquipo) {
        TiposEquipo tiposEquipoEntity = tiposEquipoMapper.toEntity(tiposEquipo);
        em.persist(tiposEquipoEntity);
        em.flush();
    }

    @Override
    public void modificarTiposEquipo(TiposEquipoDto tiposEquipo) {
        TiposEquipo tiposEquipoEntity = tiposEquipoMapper.toEntity(tiposEquipo);
        em.merge(tiposEquipoEntity);
        em.flush();
    }

    @Override
    public List<TiposEquipoDto> listarTiposEquipo() {
        List<TiposEquipo> tiposEquipos = em.createQuery("SELECT t FROM TiposEquipo t", TiposEquipo.class).getResultList();
        return tiposEquipoMapper.toDto(tiposEquipos);
    }
}

