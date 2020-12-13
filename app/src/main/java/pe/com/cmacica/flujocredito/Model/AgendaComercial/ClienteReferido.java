package pe.com.cmacica.flujocredito.Model.AgendaComercial;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ClienteReferido implements Parcelable {

    private int id;
    private String documento;
    private String nombres;
    private String direccion;
    private String telefono;
    private String departamento;
    private String provincia;
    private String distrito;
    private int idAgencia;
    private int idUsuario;
    private int idProducto;
    private int estadoResultado;

    private int sincronizar;


    public ClienteReferido() {  }


    protected ClienteReferido(Parcel in) {
        id = in.readInt();
        documento = in.readString();
        nombres = in.readString();
        direccion = in.readString();
        telefono = in.readString();
        departamento = in.readString();
        provincia = in.readString();
        distrito = in.readString();
        idAgencia = in.readInt();
        idUsuario = in.readInt();
        idProducto = in.readInt();
        estadoResultado = in.readInt();
        sincronizar = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(documento);
        dest.writeString(nombres);
        dest.writeString(direccion);
        dest.writeString(telefono);
        dest.writeString(departamento);
        dest.writeString(provincia);
        dest.writeString(distrito);
        dest.writeInt(idAgencia);
        dest.writeInt(idUsuario);
        dest.writeInt(idProducto);
        dest.writeInt(estadoResultado);
        dest.writeInt(sincronizar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClienteReferido> CREATOR = new Creator<ClienteReferido>() {
        @Override
        public ClienteReferido createFromParcel(Parcel in) {
            return new ClienteReferido(in);
        }

        @Override
        public ClienteReferido[] newArray(int size) {
            return new ClienteReferido[size];
        }
    };


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public int getIdAgencia() {
        return idAgencia;
    }

    public void setIdAgencia(int idAgencia) {
        this.idAgencia = idAgencia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getEstadoResultado() {
        return estadoResultado;
    }

    public void setEstadoResultado(int estadoResultado) {
        this.estadoResultado = estadoResultado;
    }

    public int getSincronizar() {
        return sincronizar;
    }

    public void setSincronizar(int sincronizar) {
        this.sincronizar = sincronizar;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteReferido that = (ClienteReferido) o;
        return id == that.id &&
                idAgencia == that.idAgencia &&
                idUsuario == that.idUsuario &&
                idProducto == that.idProducto &&
                estadoResultado == that.estadoResultado &&
                sincronizar == that.sincronizar &&
                Objects.equals(documento, that.documento) &&
                Objects.equals(nombres, that.nombres) &&
                Objects.equals(direccion, that.direccion) &&
                Objects.equals(telefono, that.telefono) &&
                Objects.equals(departamento, that.departamento) &&
                Objects.equals(provincia, that.provincia) &&
                Objects.equals(distrito, that.distrito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, documento, nombres, direccion, telefono, departamento, provincia, distrito, idAgencia, idUsuario, idProducto, estadoResultado, sincronizar);
    }

}
