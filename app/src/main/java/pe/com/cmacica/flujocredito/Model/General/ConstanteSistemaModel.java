package pe.com.cmacica.flujocredito.Model.General;

public class ConstanteSistemaModel {

    public ConstanteSistemaModel(int codigoConstSis, String codigoConstSisValor, String constSisDescripcion,String ultimaActualizacion) {
        nConsSisCod = codigoConstSis;
        nConsSisValor = codigoConstSisValor;
        nConsSisDesc = constSisDescripcion;
        UltimaActualizacion=ultimaActualizacion;

    }

    public int getCodigoConstSis() { return nConsSisCod; }
    public String getCodigoConstSisValor() {
        return nConsSisValor;
    }
    public String getConstSisDescripcion() {
        return nConsSisDesc;
    }
    public String getUltimaActualizacion() {
        return UltimaActualizacion;
    }

    private int nConsSisCod;
    private String nConsSisValor;
    private String nConsSisDesc;
    private String UltimaActualizacion;
}
