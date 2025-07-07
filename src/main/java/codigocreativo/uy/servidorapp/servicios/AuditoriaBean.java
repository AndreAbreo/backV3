package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.AuditoriaDto;
import codigocreativo.uy.servidorapp.DTOMappers.AuditoriaMapper;
import codigocreativo.uy.servidorapp.entidades.Auditoria;
import codigocreativo.uy.servidorapp.entidades.Intervencion;
import codigocreativo.uy.servidorapp.entidades.Usuario;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class AuditoriaBean implements AuditoriaRemote{

    @PersistenceContext (unitName = "servidorappPU")
    private EntityManager em;

    @Inject
    AuditoriaMapper auditoriaMapper;

    @Override
    public void crearRegistro(AuditoriaDto a){
        em.persist(auditoriaMapper.toEntity(a));
    }
    @Override
    public List<AuditoriaDto> obtenerTodas() throws ServiciosException {
        try {
            return auditoriaMapper.toDto(em.createQuery("SELECT auditoria FROM Auditoria auditoria", Auditoria.class).getResultList());
        } catch (Exception e) {
            throw new ServiciosException("No se pudo listar las auditorias");
        }
    }
}
