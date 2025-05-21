package codigocreativo.uy.servidorapp.DTO;

import codigocreativo.uy.servidorapp.enumerados.Estados;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link codigocreativo.uy.servidorapp.entidades.Pais}
 */
public class PaisDto implements Serializable {
    private Long id;
    private String nombre;
    private Estados estado;

    public PaisDto() {
    }

    public PaisDto(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public PaisDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public PaisDto setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public Estados getEstado() {
        return estado;
    }

    public Estados setEstado(Estados estado) {
        this.estado = estado;
        return this.estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaisDto entity = (PaisDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.nombre, entity.nombre)&&
                Objects.equals(this.estado, entity.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, estado);
    }

    @Override
    public String toString() {
        return nombre;
    }
}