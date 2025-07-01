package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.UsuariosTelefonoDto;
import codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext;
import codigocreativo.uy.servidorapp.DTOMappers.UsuariosTelefonoMapper;
import codigocreativo.uy.servidorapp.entidades.UsuariosTelefono;
import codigocreativo.uy.servidorapp.excepciones.ServiciosException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class UsuariosTelefonoBean implements UsuariosTelefonoRemote{
    @PersistenceContext (unitName = "default")
    private EntityManager em;

    @Inject
    private UsuariosTelefonoMapper telefonoMapper;


    @Override
    public void crearTelefono(UsuariosTelefonoDto telefono) throws ServiciosException {
        try {
            em.persist(telefonoMapper.toEntity(telefono, new CycleAvoidingMappingContext()));
            em.flush();
        } catch (Exception e) {
            throw new ServiciosException("No se pudo crear el telefono");
        }
    }

    @Override
    public void modificarTelefono(UsuariosTelefonoDto telefono) throws ServiciosException {
        try {
            em.merge(telefonoMapper.toEntity(telefono, new CycleAvoidingMappingContext()));
            em.flush();
        } catch (Exception e) {
            throw new ServiciosException("No se pudo modificar el telefono");
        }
    }

    @Override
    public UsuariosTelefonoDto obtenerTelefono(Long id) throws ServiciosException {
        return telefonoMapper.toDto(em.createQuery("SELECT t FROM UsuariosTelefono t WHERE t.id = :id", UsuariosTelefono.class).setParameter("id", id).getSingleResult());
    }

    @Override
    public void eliminarTelefono(Long id) throws ServiciosException {
        try {
            UsuariosTelefono telefono = em.find(UsuariosTelefono.class, id);
            if (telefono != null) {
                telefono.setIdUsuario(null);
                em.remove(telefono);
                em.flush();
            } else {
                throw new ServiciosException("Teléfono no encontrado con ID: " + id);
            }
        } catch (Exception e) {
            throw new ServiciosException("No se pudo eliminar el teléfono");
        }
    }

    @Override
    public List<UsuariosTelefonoDto> obtenerusuariosTelefono() throws ServiciosException {
        List<UsuariosTelefono> telefonos = em.createQuery("SELECT t FROM UsuariosTelefono t", UsuariosTelefono.class).getResultList();
        return telefonoMapper.toDto(telefonos);
    }
}

