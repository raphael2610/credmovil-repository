package pe.com.cmacica.flujocredito.Model.AgendaComercial;

public class TipoContacto {

    private int codigo;
    private int valor;
    private String descripcion;


    public TipoContacto() {  }


    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    @Override
    public String toString() {
        return descripcion == null ? "" : descripcion.toUpperCase();
    }


}
