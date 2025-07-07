package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.PerfilDto;
import codigocreativo.uy.servidorapp.DTOMappers.PerfilMapper;
import codigocreativo.uy.servidorapp.entidades.Perfil;
import codigocreativo.uy.servidorapp.enumerados.Estados;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;

@Stateless
public class PerfilBean implements PerfilRemote {
    @PersistenceContext(unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    private PerfilMapper perfilMapper;

    @Override
    public void crearPerfil(PerfilDto p) {
        em.persist(perfilMapper.toEntity(p));
        em.flush();
    }

    @Override
    public void modificarPerfil(PerfilDto p) {
        em.merge(perfilMapper.toEntity(p));
        em.flush();
    }

    @Override
    public void eliminarPerfil(PerfilDto p) {
        Perfil entity = perfilMapper.toEntity(p);
        entity.setEstado(Estados.INACTIVO);
        em.merge(entity);
    }

    @Override
    public PerfilDto obtenerPerfil(Long id) {
        return perfilMapper.toDto(em.find(Perfil.class, id));
    }

    @Override
    public List<PerfilDto> obtenerPerfiles() {
        return perfilMapper.toDto(em.createQuery("SELECT p FROM Perfil p", Perfil.class).getResultList());
    }

    @Override
    public List<PerfilDto> listarPerfilesPorNombre(String nombre) {
        //busca los perfiles por nombre
        return perfilMapper.toDto(em.createQuery("SELECT p FROM Perfil p WHERE p.nombrePerfil LIKE :nombre", Perfil.class).setParameter("nombre", "%" + nombre + "%").getResultList());
    }

    @Override
    public List<PerfilDto> listarPerfilesPorEstado(Estados estado) {
        //busca los perfiles por estado
        return perfilMapper.toDto(em.createQuery("SELECT p FROM Perfil p WHERE p.estado = :estado", Perfil.class).setParameter("estado", estado).getResultList());
    }

}
