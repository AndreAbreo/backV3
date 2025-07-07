package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.PerfilesPermisoDto;
import codigocreativo.uy.servidorapp.DTOMappers.PerfilesPermisoMapper;
import codigocreativo.uy.servidorapp.entidades.PerfilesPermiso;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
@Stateless
public class PerfilesPermisoBean implements PerfilesPermisoRemote {
    @PersistenceContext (unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    private PerfilesPermisoMapper perfilesPermisoMapper;

    @Override
    public void CrearPerfilesPermiso(PerfilesPermisoDto perfilesPermiso) {
        em.persist(perfilesPermisoMapper.toEntity(perfilesPermiso));
        em.flush();
    }

    @Override
    public void modificarPerfilesPermiso(PerfilesPermisoDto perfilesPermiso) {
        em.merge(perfilesPermisoMapper.toEntity(perfilesPermiso));
        em.flush();
    }

    @Override
    public List<PerfilesPermisoDto> obtenerPerfilesPermiso() {
        return perfilesPermisoMapper.toDto(em.createQuery("SELECT perfilesPermiso FROM PerfilesPermiso perfilesPermiso", PerfilesPermiso.class).getResultList());
    }
}

