package pe.com.cmacica.flujocredito.Model.carteraanalista;


import java.util.Objects;

public class Direccion {

    private int id;
    private String referencia;
    private double latitud;
    private double longitud;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direccion direccion = (Direccion) o;
        return id == direccion.id &&
                Double.compare(direccion.latitud, latitud) == 0 &&
                Double.compare(direccion.longitud, longitud) == 0 &&
                Objects.equals(referencia, direccion.referencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, referencia, latitud, longitud);
    }
}

