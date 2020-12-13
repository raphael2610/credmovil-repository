package pe.com.cmacica.flujocredito.Model.AgendaComercial;

import java.util.Objects;

public class OfertaCliente {

    private int id;
    private int idUsuario;
    private String doc;
    private int idOferta;
    private String oferta;
    private double montoOfertaCC;
    private double montoOfertaSC;

    public OfertaCliente() {  }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public int getIdOferta() {
        return idOferta;
    }

    public void setIdOferta(int idOferta) {
        this.idOferta = idOferta;
    }

    public String getOferta() {
        return oferta;
    }

    public void setOferta(String oferta) {
        this.oferta = oferta;
    }

    public double getMontoOfertaCC() {
        return montoOfertaCC;
    }

    public void setMontoOfertaCC(double montoOfertaCC) {
        this.montoOfertaCC = montoOfertaCC;
    }

    public double getMontoOfertaSC() {
        return montoOfertaSC;
    }

    public void setMontoOfertaSC(double montoOfertaSC) {
        this.montoOfertaSC = montoOfertaSC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfertaCliente that = (OfertaCliente) o;
        return id == that.id &&
                idUsuario == that.idUsuario &&
                idOferta == that.idOferta &&
                Double.compare(that.montoOfertaCC, montoOfertaCC) == 0 &&
                Double.compare(that.montoOfertaSC, montoOfertaSC) == 0 &&
                Objects.equals(doc, that.doc) &&
                Objects.equals(oferta, that.oferta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUsuario, doc, idOferta, oferta, montoOfertaCC, montoOfertaSC);
    }

}
