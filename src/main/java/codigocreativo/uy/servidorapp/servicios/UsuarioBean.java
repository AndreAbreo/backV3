package codigocreativo.uy.servidorapp.servicios;

import codigocreativo.uy.servidorapp.DTO.UsuarioDto;
import codigocreativo.uy.servidorapp.DTO.UsuariosTelefonoDto;
import codigocreativo.uy.servidorapp.DTOMappers.CycleAvoidingMappingContext;
import codigocreativo.uy.servidorapp.DTOMappers.UsuarioMapper;
import codigocreativo.uy.servidorapp.entidades.Usuario;
import codigocreativo.uy.servidorapp.entidades.UsuariosTelefono;
import codigocreativo.uy.servidorapp.enumerados.Estados;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import javax.swing.*;
import java.sql.ResultSet;
import java.util.List;
@Stateless
public class UsuarioBean implements UsuarioRemote {
    @PersistenceContext (unitName = "default")
    private EntityManager em;

    @Inject
    UsuarioMapper usuarioMapper;

    @Override
    public void crearUsuario(UsuarioDto u) {
        u.setEstado(Estados.SIN_VALIDAR);
        em.merge(usuarioMapper.toEntity(u, new CycleAvoidingMappingContext()));
    }

    @Override
    public void modificarUsuario(UsuarioDto u) {
        Usuario usuarioEntity = usuarioMapper.toEntity(u, new CycleAvoidingMappingContext());

        // Persistir teléfonos nuevos
        if (usuarioEntity.getUsuariosTelefonos() != null) {
            for (UsuariosTelefono tel : usuarioEntity.getUsuariosTelefonos()) {
                if (tel.getId() == null || tel.getId() == 0) {
                    tel.setIdUsuario(usuarioEntity); // Asegura relación bidireccional
                    em.persist(tel);
                }
            }
        }

        em.merge(usuarioEntity); // Actualiza usuario y teléfonos existentes
        em.flush();
    }

    @Override
    public boolean existeMail(String email) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean existeCI(String cedula) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM Usuario u WHERE u.cedula = :cedula", Long.class)
                    .setParameter("cedula", cedula)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void eliminarUsuario(UsuarioDto u) {
        em.createQuery("UPDATE Usuario u SET u.estado = 'INACTIVO' WHERE u.id = :id")
                .setParameter("id", u.getId())
                .executeUpdate();
        em.flush();
    }

    @Override
    public UsuarioDto obtenerUsuario(Long id) {
        return usuarioMapper.toDto(em.find(Usuario.class, id), new CycleAvoidingMappingContext());
    }

    @Override
    public UsuarioDto obtenerUsuarioDto(Long id) {
        return usuarioMapper.toDto(em.createQuery("SELECT u FROM Usuario u WHERE u.id = :id", Usuario.class)
                .setParameter("id", id)
                .getSingleResult(), new CycleAvoidingMappingContext());
    }

    @Override
    public UsuarioDto obtenerUsuarioPorCI(String ci) {
        return usuarioMapper.toDto(em.createQuery("SELECT u FROM Usuario u WHERE u.cedula = :ci", Usuario.class)
                .setParameter("ci", ci)
                .getSingleResult(), new CycleAvoidingMappingContext());
    }

    @Override
    public List<UsuarioDto> obtenerUsuarios() {
        return usuarioMapper.toDto(em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList(), new CycleAvoidingMappingContext());
    }

    @Override
    public List<UsuarioDto> obtenerUsuariosFiltrado(String filtro, Object valor) {
        System.out.println("SELECT u FROM Usuario u WHERE u." + filtro + " = :valor");
        System.out.println("valor: " + valor);
        return usuarioMapper.toDto(em.createQuery("SELECT u FROM Usuario u WHERE u." + filtro + " = :valor", Usuario.class)
                .setParameter("valor", valor)
                .getResultList(), new CycleAvoidingMappingContext());
    }

    @Override
    public List<UsuarioDto> obtenerUsuariosPorEstado(Estados estado) {
        return usuarioMapper.toDto(em.createQuery("SELECT u FROM Usuario u WHERE u.estado = :estado", Usuario.class)
                .setParameter("estado", estado)
                .getResultList(), new CycleAvoidingMappingContext());
        }

    @Override
    public UsuarioDto login(String usuario, String password) {
        try {
            return usuarioMapper.toDto(em.createQuery("SELECT u FROM Usuario u WHERE u.email = :usuario AND u.contrasenia = :password", Usuario.class)
                    .setParameter("usuario", usuario)
                    .setParameter("password", password)
                    .getSingleResult(), new CycleAvoidingMappingContext());
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public UsuarioDto findUserByEmail(String email) {
    try {
        Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                            .setParameter("email", email)
                            .getSingleResult();
        return usuarioMapper.toDto(usuario, new CycleAvoidingMappingContext());
    } catch (NoResultException e) {
        return null;
    }
}
}
