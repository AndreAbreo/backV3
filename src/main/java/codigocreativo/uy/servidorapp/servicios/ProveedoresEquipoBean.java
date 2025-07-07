package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.ProveedoresEquipoDto;
import codigocreativo.uy.servidorapp.DTOMappers.ProveedoresEquipoMapper;
import codigocreativo.uy.servidorapp.entidades.Pais;
import codigocreativo.uy.servidorapp.entidades.ProveedoresEquipo;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class ProveedoresEquipoBean implements ProveedoresEquipoRemote{
    @PersistenceContext (unitName = "servidorappPU")
    private EntityManager em;
    @Inject
    private ProveedoresEquipoMapper proveedoresEquipoMapper;

    @Override
    public void CrearProveedoresEquipo(ProveedoresEquipoDto proveedoresEquipo) {
        //agregamos dto
        ProveedoresEquipo proveedoresEquipoEntity = proveedoresEquipoMapper.toEntity(proveedoresEquipo);
        //persistimos
        em.persist(proveedoresEquipoEntity);
        em.flush();
    }

    /*@Override
    public void modificarProveedoresEquipo(ProveedoresEquipo proveedoresEquipo) {
        em.merge(proveedoresEquipo);
        em.flush();
    }

    @Override
    public void obtenerProveedoresEquipo(Long id) {
        em.find(ProveedoresEquipo.class, id);
    }*/


    @Override
    public List<ProveedoresEquipoDto> obtenerProveedoresEquipo() {
        List<ProveedoresEquipo> proveedoresEquipo = em.createQuery("SELECT p FROM ProveedoresEquipo p", ProveedoresEquipo.class).getResultList();
        return proveedoresEquipoMapper.toDto(proveedoresEquipo);
    }
}

