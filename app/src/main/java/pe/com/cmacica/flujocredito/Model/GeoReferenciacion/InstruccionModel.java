package pe.com.cmacica.flujocredito.Model.GeoReferenciacion;

/**
 * by MFPE - 2019.
 */
public class InstruccionModel {

    private Integer imagen;
    private String indicacion;

    public InstruccionModel(Integer imagen, String indicacion) {
        this.imagen = imagen;
        this.indicacion = indicacion;
    }

    public Integer getImagen() {
        return imagen;
    }

    public void setImagen(Integer imagen) {
        this.imagen = imagen;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(String indicacion) {
        this.indicacion = indicacion;
    }
}
