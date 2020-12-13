package pe.com.cmacica.flujocredito.Model.AgendaComercial;

import android.os.Parcel;
import android.os.Parcelable;

public class ClienteVisita implements Parcelable {

    private int id;
    private int idAgencia;
    private String descAgencia;
    private int idCliente;
    private String nombres;
    private int edad;
    private String dni;
    private String telefono;
    private String direccion;
    private String fechaVisita;
    private int idUsuario;

    private int flag;
    private int sincronizar;
    private int resultado;


    public ClienteVisita() {  }

    protected ClienteVisita(Parcel in) {
        id = in.readInt();
        idAgencia = in.readInt();
        descAgencia = in.readString();
        idCliente = in.readInt();
        nombres = in.readString();
        edad = in.readInt();
        dni = in.readString();
        telefono = in.readString();
        direccion = in.readString();
        fechaVisita = in.readString();
        idUsuario = in.readInt();
        flag = in.readInt();
        sincronizar = in.readInt();
        resultado = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idAgencia);
        dest.writeString(descAgencia);
        dest.writeInt(idCliente);
        dest.writeString(nombres);
        dest.writeInt(edad);
        dest.writeString(dni);
        dest.writeString(telefono);
        dest.writeString(direccion);
        dest.writeString(fechaVisita);
        dest.writeInt(idUsuario);
        dest.writeInt(flag);
        dest.writeInt(sincronizar);
        dest.writeInt(resultado);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClienteVisita> CREATOR = new Creator<ClienteVisita>() {
        @Override
        public ClienteVisita createFromParcel(Parcel in) {
            return new ClienteVisita(in);
        }

        @Override
        public ClienteVisita[] newArray(int size) {
            return new ClienteVisita[size];
        }
    };



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAgencia() {
        return idAgencia;
    }

    public void setIdAgencia(int idAgencia) {
        this.idAgencia = idAgencia;
    }

    public String getDescAgencia() {
        return descAgencia;
    }

    public void setDescAgencia(String descAgencia) {
        this.descAgencia = descAgencia;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(String fechaVisita) {
        this.fechaVisita = fechaVisita;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getSincronizar() {
        return sincronizar;
    }

    public void setSincronizar(int sincronizar) {
        this.sincronizar = sincronizar;
    }

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }


}
