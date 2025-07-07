package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.InstitucionDto;
import codigocreativo.uy.servidorapp.DTO.MarcasModeloDto;
import codigocreativo.uy.servidorapp.DTO.ModelosEquipoDto;
import codigocreativo.uy.servidorapp.DTOMappers.MarcasModeloMapper;
import codigocreativo.uy.servidorapp.entidades.Equipo;
import codigocreativo.uy.servidorapp.entidades.Institucion;
import codigocreativo.uy.servidorapp.entidades.MarcasModelo;
import codigocreativo.uy.servidorapp.entidades.ModelosEquipo;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class MarcasModeloBean implements MarcasModeloRemote {
    @PersistenceContext(unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    private MarcasModeloMapper marcasModeloMapper;

    @Override
    public void crearMarcasModelo(MarcasModeloDto marcasModelo) {
        em.persist(marcasModeloMapper.toEntity(marcasModelo));
        em.flush();
    }

    @Override
    public void modificarMarcasModelo(MarcasModeloDto marcasModelo) {
        em.merge(marcasModeloMapper.toEntity(marcasModelo));
        em.flush();
    }

    @Override
    public MarcasModeloDto obtenerMarca(Long id) {
        return marcasModeloMapper.toDto(em.find(MarcasModelo.class, id));
    }


    @Override
    public List<MarcasModeloDto> obtenerMarcasLista() {
        return marcasModeloMapper.toDto(em.createQuery("SELECT marcasModelo FROM MarcasModelo marcasModelo", MarcasModelo.class).getResultList());
    }

    public List<ModelosEquipoDto> obtenerModeloXMarca(Long idMarca) {
        List<ModelosEquipo> modelos = em.createQuery(
                        "SELECT m FROM ModelosEquipo m WHERE m.idMarca.id = :idMarca", ModelosEquipo.class)
                .setParameter("idMarca", idMarca)
                .getResultList();


        return modelos.stream()
                .map(marcasModeloMapper::toDto)
                .collect(Collectors.toList());
    }
}