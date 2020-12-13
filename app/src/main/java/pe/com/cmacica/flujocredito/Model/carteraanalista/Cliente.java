package pe.com.cmacica.flujocredito.Model.carteraanalista;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;


public class Cliente implements Parcelable {

    private int id;
    private String persCod;
    private String nombre;
    private String doi;
    private String telefonoUno;
    private String telefonoDos;
    private String direccionDomicilio;
    private String geoposicion;
    private String creditos;


    private String telefono;
    private int idTipoDireccion;
    private String referencia;
    private Double longitud;
    private Double latitud;
    private int flag;
    private int sincronizar;

    private String user;

    public Cliente() { }

    protected Cliente(Parcel in) {
        id = in.readInt();
        persCod = in.readString();
        nombre = in.readString();
        doi = in.readString();
        telefonoUno = in.readString();
        telefonoDos = in.readString();
        direccionDomicilio = in.readString();
        geoposicion = in.readString();
        creditos = in.readString();
        telefono = in.readString();
        idTipoDireccion = in.readInt();
        referencia = in.readString();
        if (in.readByte() == 0) {
            longitud = null;
        } else {
            longitud = in.readDouble();
        }
        if (in.readByte() == 0) {
            latitud = null;
        } else {
            latitud = in.readDouble();
        }
        flag = in.readInt();
        sincronizar = in.readInt();
        user = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(persCod);
        dest.writeString(nombre);
        dest.writeString(doi);
        dest.writeString(telefonoUno);
        dest.writeString(telefonoDos);
        dest.writeString(direccionDomicilio);
        dest.writeString(geoposicion);
        dest.writeString(creditos);
        dest.writeString(telefono);
        dest.writeInt(idTipoDireccion);
        dest.writeString(referencia);
        if (longitud == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitud);
        }
        if (latitud == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitud);
        }
        dest.writeInt(flag);
        dest.writeInt(sincronizar);
        dest.writeString(user);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cliente> CREATOR = new Creator<Cliente>() {
        @Override
        public Cliente createFromParcel(Parcel in) {
            return new Cliente(in);
        }

        @Override
        public Cliente[] newArray(int size) {
            return new Cliente[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersCod() {
        return persCod;
    }

    public void setPersCod(String persCod) {
        this.persCod = persCod;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getTelefonoUno() {
        return telefonoUno;
    }

    public void setTelefonoUno(String telefonoUno) {
        this.telefonoUno = telefonoUno;
    }

    public String getTelefonoDos() {
        return telefonoDos;
    }

    public void setTelefonoDos(String telefonoDos) {
        this.telefonoDos = telefonoDos;
    }

    public String getDireccionDomicilio() {
        return direccionDomicilio;
    }

    public void setDireccionDomicilio(String direccionDomicilio) {
        this.direccionDomicilio = direccionDomicilio;
    }

    public String getGeoposicion() {
        return geoposicion;
    }

    public void setGeoposicion(String geoposicion) {
        this.geoposicion = geoposicion;
    }

    public String getCreditos() {
        return creditos;
    }

    public void setCreditos(String creditos) {
        this.creditos = creditos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getIdTipoDireccion() {
        return idTipoDireccion;
    }

    public void setIdTipoDireccion(int idTipoDireccion) {
        this.idTipoDireccion = idTipoDireccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id == cliente.id &&
                idTipoDireccion == cliente.idTipoDireccion &&
                flag == cliente.flag &&
                sincronizar == cliente.sincronizar &&
                Objects.equals(persCod, cliente.persCod) &&
                Objects.equals(nombre, cliente.nombre) &&
                Objects.equals(doi, cliente.doi) &&
                Objects.equals(telefonoUno, cliente.telefonoUno) &&
                Objects.equals(telefonoDos, cliente.telefonoDos) &&
                Objects.equals(direccionDomicilio, cliente.direccionDomicilio) &&
                Objects.equals(geoposicion, cliente.geoposicion) &&
                Objects.equals(creditos, cliente.creditos) &&
                Objects.equals(telefono, cliente.telefono) &&
                Objects.equals(referencia, cliente.referencia) &&
                Objects.equals(longitud, cliente.longitud) &&
                Objects.equals(latitud, cliente.latitud) &&
                Objects.equals(user, cliente.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, persCod, nombre, doi, telefonoUno, telefonoDos, direccionDomicilio, geoposicion, creditos, telefono, idTipoDireccion, referencia, longitud, latitud, flag, sincronizar, user);
    }
}
