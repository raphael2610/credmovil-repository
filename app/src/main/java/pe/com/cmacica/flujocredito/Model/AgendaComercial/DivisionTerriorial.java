package pe.com.cmacica.flujocredito.Model.AgendaComercial;

public class DivisionTerriorial {

    private String codigoUbigeo;
    private String codigoUbigeoRENIEC;
    private String descripcionUbigeo;


    public String getCodigoUbigeo() {
        return codigoUbigeo;
    }

    public void setCodigoUbigeo(String codigoUbigeo) {
        this.codigoUbigeo = codigoUbigeo;
    }

    public String getCodigoUbigeoRENIEC() {
        return codigoUbigeoRENIEC;
    }

    public void setCodigoUbigeoRENIEC(String codigoUbigeoRENIEC) {
        this.codigoUbigeoRENIEC = codigoUbigeoRENIEC;
    }

    public String getDescripcionUbigeo() {
        return descripcionUbigeo;
    }

    public void setDescripcionUbigeo(String descripcionUbigeo) {
        this.descripcionUbigeo = descripcionUbigeo;
    }

    @Override
    public String toString() {
        return getDescripcionUbigeo();
    }

}
