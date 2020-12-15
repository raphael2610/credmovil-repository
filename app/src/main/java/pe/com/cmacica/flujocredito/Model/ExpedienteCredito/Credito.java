package pe.com.cmacica.flujocredito.Model.ExpedienteCredito;

public class Credito {

    private int id;
    private String personCode;
    private String numberCredit;
    private String typeCredit;
    private String refundDate;
    private String state;
    private String amount;


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

}
