package pe.com.cmacica.flujocredito.Model.GeoReferenciacion;

import java.io.Serializable;

/**
 * by MFPE - 2019.
 */
public class GeoRefClienteModel implements Serializable {
    private int IdCliente;
    private String Nombres;
    private String Doi;
    private String Telefono;
    private int IdTipoDireccion;
    private String TipoDireccion;
    private Double Longitud;
    private Double Latitud;
    private String Referencia;

    public String getReferencia() {
        return Referencia;
    }

    public void setReferencia(String referencia) {
        Referencia = referencia;
    }

    public int getIdCliente() {
        return IdCliente;
    }

    public void setIdCliente(int idCliente) {
        IdCliente = idCliente;
    }

    public String getNombres() {
        return Nombres.toUpperCase();
    }

    public void setNombres(String nombres) {
        Nombres = nombres.toUpperCase();
    }

    public int getIdTipoDireccion() {
        return IdTipoDireccion;
    }

    public void setIdTipoDireccion(int idTipoDireccion) {
        IdTipoDireccion = idTipoDireccion;
    }

    public String getDoi() {
        return Doi;
    }

    public void setDoi(String doi) {
        Doi = doi;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public String getTipoDireccion() {
        return TipoDireccion;
    }

    public void setTipoDireccion(String tipoDireccion) {
        TipoDireccion = tipoDireccion;
    }
}
