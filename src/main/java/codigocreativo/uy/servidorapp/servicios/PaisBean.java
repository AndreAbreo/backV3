package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.PaisDto;
import codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext;
import codigocreativo.uy.servidorapp.DTOMappers.PaisMapper;
import codigocreativo.uy.servidorapp.entidades.Pais;
import codigocreativo.uy.servidorapp.entidades.Ubicacion;
import codigocreativo.uy.servidorapp.enumerados.Estados;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
@Stateless
public class PaisBean implements PaisRemote{
    @PersistenceContext (unitName = "default")
    private EntityManager em;
    @Inject
    private PaisMapper paisMapper;

    @Override
    public void crearPais(PaisDto pais) throws ServiciosException{
        try{
            em.persist(paisMapper.toEntity(pais, new CycleAvoidingMappingContext()));
            em.flush();
        }catch (Exception e){
            throw new ServiciosException("Error al crear el país: "+e.getMessage());
        }
    }

    @Override
    public void modificarPais(PaisDto pais) throws ServiciosException {
        try{
            em.merge(paisMapper.toEntity(pais, new CycleAvoidingMappingContext()));
            em.flush();
        }catch (Exception e){
            throw new ServiciosException("Error al modificar el país: "+e.getMessage());
        }
    }

    @Override
    public List<PaisDto> obtenerpais() throws ServiciosException {
        try {
            List<Pais> paises = em.createQuery("SELECT p FROM Pais p", Pais.class).getResultList();
            return paisMapper.toDto(paises);
        }catch (Exception e){
            throw new ServiciosException("Error al listar los paises: "+e.getMessage());
        }
    }

    @Override
    public PaisDto obtenerPaisPorId(Long id) throws ServiciosException{
        try {
            return paisMapper.toDto(em.createQuery("SELECT p FROM Pais p WHERE p.id = :id", Pais.class).setParameter("id", id).getSingleResult());
        }catch(Exception e){
            throw new ServiciosException("Error al obtener el [aís seleccionado: "+e.getMessage());
        }
    }

    @Override
    public void bajaLogicaPais(PaisDto paisdto) throws ServiciosException{
        Pais pais = paisMapper.toEntity(paisdto, new CycleAvoidingMappingContext());
        try {
            pais.setEstado(Estados.INACTIVO);
            em.merge(pais);
            em.flush();
        } catch (Exception e) {
            throw new ServiciosException("Error al inactivar el país: "+e.getMessage());
        }
    }

    @Override
    public boolean existePais(String nombre) {
        try {
            Long count = em.createQuery(
                            " SELECT COUNT(p) FROM Pais p WHERE p.nombre = :nombre", Long.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
