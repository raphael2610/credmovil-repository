package pe.com.cmacica.flujocredito.Model.ExpedienteCredito;

import android.os.Parcel;
import android.os.Parcelable;

public class Cliente implements Parcelable {

    private int id;
    private String name;
    private String typePerson;
    private String personCode;

    public Cliente() {}


    protected Cliente(Parcel in) {
        id = in.readInt();
        name = in.readString();
        typePerson = in.readString();
        personCode = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(typePerson);
        parcel.writeString(personCode);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypePerson() {
        return typePerson;
    }

    public void setTypePerson(String typePerson) {
        this.typePerson = typePerson;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

}
