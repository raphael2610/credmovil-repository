package pe.com.cmacica.flujocredito.Model.ExpedienteCredito;

import android.os.Parcel;
import android.os.Parcelable;

public class Credito implements Parcelable {

    private int id;
    private String personCode;
    private String numberCredit;
    private String typeCredit;
    private String refundDate;
    private String state;
    private String amount;

    public Credito() {}

    protected Credito(Parcel in) {
        id = in.readInt();
        personCode = in.readString();
        numberCredit = in.readString();
        typeCredit = in.readString();
        refundDate = in.readString();
        state = in.readString();
        amount = in.readString();
    }

    public static final Creator<Credito> CREATOR = new Creator<Credito>() {
        @Override
        public Credito createFromParcel(Parcel in) {
            return new Credito(in);
        }

        @Override
        public Credito[] newArray(int size) {
            return new Credito[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

    public String getNumberCredit() {
        return numberCredit;
    }

    public void setNumberCredit(String numberCredit) {
        this.numberCredit = numberCredit;
    }

    public String getTypeCredit() {
        return typeCredit;
    }

    public void setTypeCredit(String typeCredit) {
        this.typeCredit = typeCredit;
    }

    public String getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(String refundDate) {
        this.refundDate = refundDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(personCode);
        parcel.writeString(numberCredit);
        parcel.writeString(typeCredit);
        parcel.writeString(refundDate);
        parcel.writeString(state);
        parcel.writeString(amount);
    }
}
