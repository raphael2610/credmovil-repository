package pe.com.cmacica.flujocredito.Model.AgendaComercial;

public class Oferta {


    private int id;
    private int idCliente;
    private String dni;
    private String nombres;
    private String descOferta;
    private double montoOfertCc;
    private double montoOfertSc;

    public Oferta() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getDescOferta() {
        return descOferta;
    }

    public void setDescOferta(String descOferta) {
        this.descOferta = descOferta;
    }

    public double getMontoOfertCc() {
        return montoOfertCc;
    }

    public void setMontoOfertCc(double montoOfertCc) {
        this.montoOfertCc = montoOfertCc;
    }

    public double getMontoOfertSc() {
        return montoOfertSc;
    }

    public void setMontoOfertSc(double montoOfertSc) {
        this.montoOfertSc = montoOfertSc;
    }

    @Override
    public String toString() {
        return descOferta;
    }

}
