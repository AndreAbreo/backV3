package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.PermisoDto;
import codigocreativo.uy.servidorapp.DTOMappers.PermisoMapper;
import codigocreativo.uy.servidorapp.entidades.Permiso;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
@Stateless
public class PermisoBean implements PermisoRemote{
    @PersistenceContext (unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    private PermisoMapper permisoMapper;

    @Override
    public void crearPermiso(PermisoDto p) {
        em.persist(permisoMapper.toEntity(p));
        em.flush();
    }

    @Override
    public void modificarPermiso(PermisoDto p) {
        em.merge(permisoMapper.toEntity(p));
        em.flush();
    }

    @Override
    public void eliminarPermiso(PermisoDto p) {
        em.remove(permisoMapper.toEntity(p));
        em.flush();
    }

    @Override
    public PermisoDto obtenerPermiso(Long id) {
        return permisoMapper.toDto(em.find(Permiso.class, id));
    }

    @Override
    public List<PermisoDto> obtenerPermisos() {
        return permisoMapper.toDto(em.createQuery("SELECT p FROM Permiso p", Permiso.class).getResultList());
    }
}
