package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.ModelosEquipoDto;
import codigocreativo.uy.servidorapp.DTOMappers.ModelosEquipoMapper;
import codigocreativo.uy.servidorapp.entidades.ModelosEquipo;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class ModelosEquipoBean implements ModelosEquipoRemote {
    @PersistenceContext(unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    private ModelosEquipoMapper modelosEquipoMapper;

    @Override
    public void crearModelosEquipo(ModelosEquipoDto modelosEquipo) {
        em.persist(modelosEquipoMapper.toEntity(modelosEquipo));
        em.flush();
    }

    @Override
    public void modificarModelosEquipo(ModelosEquipoDto modelosEquipo) {
        em.merge(modelosEquipoMapper.toEntity(modelosEquipo));
        em.flush();
    }

    @Override
    public ModelosEquipoDto obtenerModelosEquipo(Long id) {
        return modelosEquipoMapper.toDto(em.find(ModelosEquipo.class, id));
    }


    @Override
    public List<ModelosEquipoDto> listarModelosEquipo() {
        return modelosEquipoMapper.toDto(em.createQuery("SELECT modelosEquipo FROM ModelosEquipo modelosEquipo", ModelosEquipo.class).getResultList());
    }
}

