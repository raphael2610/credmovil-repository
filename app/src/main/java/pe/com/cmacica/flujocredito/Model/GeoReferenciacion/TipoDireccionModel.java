package pe.com.cmacica.flujocredito.Model.GeoReferenciacion;

import java.util.Objects;

/**
 * by MFPE - 2019.
 */
public class TipoDireccionModel {
    private int Id;
    private String Descripcion;

    public TipoDireccionModel(int id, String descripcion) {
        Id = id;
        Descripcion = descripcion;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    @Override
    public String toString() {
        return Descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoDireccionModel that = (TipoDireccionModel) o;
        return Id == that.Id &&
                Objects.equals(Descripcion, that.Descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, Descripcion);
    }
}
