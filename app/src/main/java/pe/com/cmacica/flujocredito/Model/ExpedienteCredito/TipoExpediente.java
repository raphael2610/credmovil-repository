package pe.com.cmacica.flujocredito.Model.ExpedienteCredito;

public class TipoExpediente {

    private int idCar;
    private String typeName;
    private int codeType;



    public int getIdCar() {
        return idCar;
    }

    public void setIdCar(int idCar) {
        this.idCar = idCar;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getCodeType() {
        return codeType;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    @Override
    public String toString() {
        return typeName;
    }

}
