package pe.com.cmacica.flujocredito.Model.AgendaComercial;

public class Cliente {


    private int IdClienteDni;
    private String Telefono;
    private String Correo;
    private String DireccionHogar;
    private String DireccionNegocio;
    private String ReferenciaHogar;
    private String ReferenciaNegocio;


    public Cliente() {  }

    public int getIdClienteDni() {
        return IdClienteDni;
    }

    public void setIdClienteDni(int idClienteDni) {
        IdClienteDni = idClienteDni;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getDireccionHogar() {
        return DireccionHogar;
    }

    public void setDireccionHogar(String direccionHogar) {
        DireccionHogar = direccionHogar;
    }

    public String getDireccionNegocio() {
        return DireccionNegocio;
    }

    public void setDireccionNegocio(String direccionNegocio) {
        DireccionNegocio = direccionNegocio;
    }

    public String getReferenciaHogar() {
        return ReferenciaHogar;
    }

    public void setReferenciaHogar(String referenciaHogar) {
        ReferenciaHogar = referenciaHogar;
    }

    public String getReferenciaNegocio() {
        return ReferenciaNegocio;
    }

    public void setReferenciaNegocio(String referenciaNegocio) {
        ReferenciaNegocio = referenciaNegocio;
    }

}
