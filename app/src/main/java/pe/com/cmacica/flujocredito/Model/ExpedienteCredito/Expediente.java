package pe.com.cmacica.flujocredito.Model.ExpedienteCredito;

import android.os.Parcel;
import android.os.Parcelable;

public class Expediente implements Parcelable {

    private int id;
    private String name;
    private String date;
    private String user;
    private String size;
    private String image;

    public Expediente() {}

    protected Expediente(Parcel in) {
        id = in.readInt();
        name = in.readString();
        date = in.readString();
        user = in.readString();
        size = in.readString();
        image = in.readString();
    }

    public static final Creator<Expediente> CREATOR = new Creator<Expediente>() {
        @Override
        public Expediente createFromParcel(Parcel in) {
            return new Expediente(in);
        }

        @Override
        public Expediente[] newArray(int size) {
            return new Expediente[size];
        }
    };

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(date);
        parcel.writeString(user);
        parcel.writeString(size);
        parcel.writeString(image);
    }
}
