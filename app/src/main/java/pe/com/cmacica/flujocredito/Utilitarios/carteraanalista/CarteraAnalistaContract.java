package pe.com.cmacica.flujocredito.Utilitarios.carteraanalista;

import android.provider.BaseColumns;

public final class CarteraAnalistaContract {

    private CarteraAnalistaContract() {}

    public static class ClientEntry implements BaseColumns {


        public static final String TABLE_NAME = "cliente";
        public static final String COLUM_PERS_COD = "persCod";
        public static final String COLUMN_NAME = "nombre";
        public static final String COLUMN_DOI = "doi";
        public static final String COLUMN_PHONE_ONE = "telefono1";
        public static final String COLUMN_PHONE_TWO = "telefono2";
        public static final String COLUMN_ADDRESS_HOME = "direccionDomicilio";
        public static final String COLUMN_GEOPOSITION = "geoposition";
        public static final String COLUMN_CREDITS = "creditos";

        public static final String COLUMN_PHONE = "telefono";
        public static final String COLUMN_ID_TYPE_ADDRESS = "tipoDireccion";
        public static final String COLUMN_ADDRESS = "direccion";
        public static final String COLUMN_LONGITUDE = "longitud";
        public static final String COLUMN_LATITUDE = "latitud";
        public static final String COLUMN_FLAG = "flag";
        public static final String COLUM_SYNCHRONIZATION = "syncronization";

    }

    public static class CreditEntry implements BaseColumns {

        public static final String TABLE_NAME = "creditos";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CTA_COD = "ctaCod";
        public static final String COLUMN_STATUS = "estado";
        public static final String COLUMN_AMOUNT = "monto";

        public static final String COLUMN_DOI = "doi";

    }


    public static class TypeAddressEntry implements BaseColumns {

        public static final String TABLE_NAME = "tipoDireccion";
        public static final String COLUMN_NAME = "nombre";

    }

}
