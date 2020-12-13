package pe.com.cmacica.flujocredito.Utilitarios;

/**
 * Created by JHON on 29/06/2016.
 */
public class Constantes {

    /**
     * Códigos del operación servicio {@link ESTADO}
     */
    public static final String SUCCESS = "1";
    public static final String FAILED = "2";

    /**
     * constante para estados de operacion al servicio
     */
    public static final int Correcto = 3;
    public static final int Validacion = 2;
    public static final int Error = 1;
    /**
     * Constantes para verificar si es nuevo o actualizado
     */
    public static final String Nuevo = "0";
    public static final String Actualizado = "1";

    /**
     * Constante para tiempo de conección al servicio
     */
    public static final int TimeOutService = 60000;

    /**
     * key para enviar como parametro
     */
    public static  final String KeyTipoFuenteIngreso = "KeyTipoFuenteIngreso";
    public static  final String KeyIdPersona = "KeyIdPersona";
    public static  final int CodigoFteDependiente = 1;
    public static  final int CodigoFteIndependiente = 2;


    /**
     * Constantes para PersonaProgressIntentService
     */
    public static final String ACTION_RUN_ISERVICE = "pe.com.cmacica.flujocredito.action.RUN_INTENT_SERVICE";
    public static final String ACTION_PROGRESS_EXIT = "pe.com.cmacica.flujocredito.action.PROGRESS_EXIT";

    public static final String EXTRA_PROGRESS = "pe.com.cmacica.flujocredito.extra.PROGRESS";


    /**
     * Cartera de Analista
     * Lista de Analista
     */
    public static final String SHARED_PREF_ANALISTA = "analista";
    public static final String PREF_RADIO = "radio";
    public static final int ACTUAL_ANALISTA = 0;
    public static final int OTROS = 1;
    public static final int TODOS_CLIENTES = 2;

    public static final int GEOPOSICIONADO = 1;
    public static final int NO_GEOPOSICIONADO = 2;
    public static final int SIN_FILTRO_GEOPOSICION = 3;

    public static final int RADIO = 100;
    public static final String PREF_TIPO_CARTERA = "tipo_cartera";
    public static final String PREF_OBTENER_CLIENTES = "obtener_clientes";
    public static final String PREF_FECHA_DESCARGA = "fecha_descarga";
    public static final String PREF_SINCRONIZACION = "sincronizacion";
    public static final String PREF_GEOPOSICION = "geoposicion";


    /**
     * Agenda Comercial
     */
    public static final String SHARED_PREF_AGENDA_COMERCIAL = "agenda_comercial";
    public static final String PREF_ID_USUARIO = "id_user";

    public static final int TYPE_CONTACT_FACE_FACE = 1;
    public static final int TYPE_CONTACT_CALL = 2;

}
