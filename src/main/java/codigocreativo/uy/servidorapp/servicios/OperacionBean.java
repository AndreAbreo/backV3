package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.OperacionDto;
import codigocreativo.uy.servidorapp.DTOMappers.OperacionMapper;
import codigocreativo.uy.servidorapp.entidades.Operacion;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless

public class OperacionBean implements OperacionRemote{
    @PersistenceContext (unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    private OperacionMapper operacionMapper;

    @Override
    public void crearOperacion(OperacionDto o) {
        em.persist(operacionMapper.toEntity(o));
        em.flush();
    }

    @Override
    public void modificarOperacion(OperacionDto o) {
        em.merge(operacionMapper.toEntity(o));
        em.flush();

    }

    @Override
    public void eliminarOperacion(OperacionDto o) {
        em.remove(operacionMapper.toEntity(o));
        em.flush();

    }
}
