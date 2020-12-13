package pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial;

import android.provider.BaseColumns;

public class AgendaComercialContract {


    public AgendaComercialContract() {}


    /* opcion: Agenda Comercial */
    public static class VisitsEntry implements BaseColumns {

        public static final String TABLE_NAME = "visitas";
        public static final String COLUMN_ID_AGENCY = "id_agencia";
        public static final String COLUMN_DESC_AGENCY = "desc_agencia";
        public static final String COLUMN_ID_CUSTOMER = "id_cliente";
        public static final String COLUMN_NAME = "nombres";
        public static final String COLUMN_AGE = "edad";
        public static final String COLUMN_DNI = "dni";
        public static final String COLUMN_PHONE = "telefonos";
        public static final String COLUMN_ADDRESS = "direccion";
        public static final String COLUMN_DATE_VISIT = "fecha_visita";
        public static final String COLUMN_ID_USER = "id_user";

        /*
            Flag:
                1 - nuevo base de datos
                2 - actualizado en local
         */
        public static final String COLUMN_FLAG = "flag";
        public static final String COLUMN_RESULT = "resultado";
        public static final String COLUMN_SYNCHRONIZATION = "syncronization";


    }


    public static class ResultEntry implements BaseColumns {

        public static final String TABLE_NAME = "resultados";

        public static final String COLUMN_ID_CUSTOMER = "id_cliente";
        public static final String COLUMN_ID_OFFER = "id_oferta";
        public static final String COLUMN_ID_USER = "id_usuario";
        public static final String COLUMN_AMOUNT = "monto";
        public static final String COLUMN_ID_RESULT = "id_resultado";
        public static final String COLUMN_ID_PRODUCT = "id_producto";
        public static final String COLUMN_COMMENTARY = "comentario";
        public static final String COLUMN_TYPE_CREDIT = "tipo_credito";
        public static final String COLUMN_CREDIT_DESTINATION = "destino_credito";
        public static final String COLUMN_TYPE_CONTACT = "tipo_contacto";
        public static final String COLUMN_LATITUD = "latitud";
        public static final String COLUMN_LONGITUD = "longitud";

        public static final String COLUMN_VISIT = "visita";

        /*
            TYPE:
                1 - resultado
                2 - programar una nueva visita
         */
        public static final String COLUMN_TYPE = "tipo_resultado";
        public static final String COLUM_SYNCHRONIZATION = "syncronization";

    }



    public static class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "productos";
        public static final String COLUMN_CODE = "codigo";
        public static final String COLUMN_VALUE = "valor";
        public static final String COLUMN_DESCRIPTION = "descripcion";

    }


    public static class ContactTypeEntry implements BaseColumns {

        public static final String TABLE_NAME = "tipos_contacto";
        public static final String COLUMN_CODE = "codigo";
        public static final String COLUMN_VALUE = "valor";
        public static final String COLUMN_DESCRIPTION = "descripcion";

    }


    public static class VisitResultsEntry implements BaseColumns {

        public static final String TABLE_NAME = "visitas_resultados";
        public static final String COLUMN_CODE = "codigo";
        public static final String COLUMN_VALUE = "valor";
        public static final String COLUMN_DESCRIPTION = "decripcion";
        public static final String COLUMN_TYPE_CONTACT = "tipo_contacto";

    }


    public static class OfferClientEntry implements BaseColumns {

        public static final String TABLE_NAME = "ofertas_cliente";
        public static final String COLUMN_ID_USER = "id_usuario";
        public static final String COLUMN_ID_CLIENT = "id_cliente";
        public static final String COLUMN_DOC = "doc";
        public static final String COLUMN_ID_OFFER = "id_oferta";
        public static final String COLUMN_OFFER = "oferta";
        public static final String COLUMN_AMOUNT_OFFER_CC = "monto_ofert_cc";
        public static final String COLUMN_AMOUNT_OFFER_SC = "monto_ofert_sc";

    }


    public static class OffersEntry implements BaseColumns {

        public static final String TABLE_NAME = "ofertas";
        public static final String COLUMN_ID_CLIENT = "id_cliente";
        public static final String COLUMN_DNI = "dni";
        public static final String COLUMN_NAME = "nombres";
        public static final String COLUMN_DESC_OFFER = "desc_oferta";
        public static final String COLUMN_OFFER_AMOUNT_CC = "monto_oferta_cc";
        public static final String COLUMN_OFFER_AMOUNT_SC = "monto_oferta_sc";

    }





    /* opcion: Referido */
    public static class ReferredsEntry implements BaseColumns {

        public static final String TABLE_NAME = "referidos";
        public static final String COLUMN_DNI = "dni";
        public static final String COLUMN_NAME = "nombres";
        public static final String COLUMN_ADDRESS = "direccion";
        public static final String COLUMN_PHONE = "telefono";
        public static final String COLUMN_DEPARTMENT = "departamento";
        public static final String COLUMN_PROVINCE = "provincia";
        public static final String COLUMN_DISTRICT = "distrito";
        public static final String COLUMN_ID_AGENCY = "id_agencia";
        public static final String COLUMN_ID_USER = "id_usuario";
        public static final String COLUMN_ID_PRODUCT = "id_producto";
        public static final String COLUMN_RESULT_STATE = "estado_resultado";

        public static final String COLUM_SYNCHRONIZATION = "syncronization";

    }


    public static class DepartmentEntry implements BaseColumns {

        public static final String TABLE_NAME = "departamento";
        public static final String COLUMN_UBIGEO_COD = "ubigeo_cod";
        public static final String COLUMN_UBIGEO_DESC = "ubigeo_desc";
        public static final String COLUMN_UBIGEO_CIUDAD = "ubigeo_ciudad";
        public static final String COLUMN_UBIGEO_COD_RENIEC = "ubigeo_cod_reniec";

    }


    public static class ProvinceEntry implements BaseColumns {

        public static final String TABLE_NAME = "provincia";
        public static final String COLUMN_UBIGEO_COD = "ubigeo_cod";
        public static final String COLUMN_UBIGEO_DESC = "ubigeo_desc";
        public static final String COLUMN_UBIGEO_CIUDAD = "ubigeo_ciudad";
        public static final String COLUMN_UBIGEO_COD_RENIEC = "ubigeo_cod_reniec";

    }


    public static class DistrictEntry implements BaseColumns {

        public static final String TABLE_NAME = "distrito";
        public static final String COLUMN_UBIGEO_COD = "ubigeo_cod";
        public static final String COLUMN_UBIGEO_DESC = "ubigeo_desc";
        public static final String COLUMN_UBIGEO_CIUDAD = "ubigeo_ciudad";
        public static final String COLUMN_UBIGEO_COD_RENIEC = "ubigeo_cod_reniec";

    }

    public static class AgencyEntry implements BaseColumns {

        public static final String TABLE_NAME = "agencias";
        public static final String COLUMN_AGENCIA_ID = "agencia_id";
        public static final String COLUMN_AGENCIA_COD = "agencia_cod";
        public static final String COLUMN_AGENCIA_DESCRIPTION = "agencia_descripcion";

    }

}
