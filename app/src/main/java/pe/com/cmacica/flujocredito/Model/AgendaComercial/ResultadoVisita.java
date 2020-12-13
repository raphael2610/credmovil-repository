package pe.com.cmacica.flujocredito.Model.AgendaComercial;

import java.util.Date;

public class ResultadoVisita {

    private int id;
    private int idCliente;
    private int idOferta;
    private int idUsuario;
    private double monto;
    private int idResultado;
    private int idProducto;
    private String comentario;
    private int tipoCredito;
    private int destinoCredito;
    private int tipoContacto;
    private Date visita;
    private int tipo;
    private int sincronizar;

    private double latitud;
    private double longitud;


    public ResultadoVisita() {  }


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

    public int getIdOferta() {
        return idOferta;
    }

    public void setIdOferta(int idOferta) {
        this.idOferta = idOferta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getIdResultado() {
        return idResultado;
    }

    public void setIdResultado(int idResultado) {
        this.idResultado = idResultado;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(int tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public int getDestinoCredito() {
        return destinoCredito;
    }

    public void setDestinoCredito(int destinoCredito) {
        this.destinoCredito = destinoCredito;
    }

    public int getTipoContacto() {
        return tipoContacto;
    }

    public void setTipoContacto(int tipoContacto) {
        this.tipoContacto = tipoContacto;
    }

    public Date getVisita() {
        return visita;
    }

    public void setVisita(Date visita) {
        this.visita = visita;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getSincronizar() {
        return sincronizar;
    }

    public void setSincronizar(int sincronizar) {
        this.sincronizar = sincronizar;
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

}
