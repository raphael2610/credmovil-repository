package pe.com.cmacica.flujocredito.Utilitarios.carteraanalista;

import android.content.Context;

import java.util.Date;

import pe.com.cmacica.flujocredito.Utilitarios.Constantes;

public class CarteraAnalistaPreferences {

    public static void setValidationGetCustomer(Context context, boolean flag) {
        context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .edit().putBoolean(Constantes.PREF_OBTENER_CLIENTES, flag).commit();
    }

    public static boolean getValidationGetCustomer(Context context) {
        return context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .getBoolean(Constantes.PREF_OBTENER_CLIENTES, true);
    }


    public static void setFlagSyncronization(Context context, int flag) {
        context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .edit().putInt(Constantes.PREF_SINCRONIZACION, flag).commit();
    }

    public static int getFlagSyncronization(Context context) {
        return context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .getInt(Constantes.PREF_SINCRONIZACION, CarteraAnalistaDbHelper.FLAG_SYNCRONIZED);
    }


    public static void setDateDownload(Context context) {
        Date date = new Date();
        context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .edit().putLong(Constantes.PREF_FECHA_DESCARGA, date.getTime()).apply();
    }

    public static long getDateDownload(Context context) {
        return context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .getLong(Constantes.PREF_FECHA_DESCARGA, 0);
    }


    public static void setRadio(Context context, int radio) {
        context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .edit().putInt(Constantes.PREF_RADIO, radio).commit();
    }

    public static int getRadio(Context context) {
        return context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .getInt(Constantes.PREF_RADIO, Constantes.RADIO);
    }

    public static void setTipoCartera(Context context, int tipoCartera) {
        context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .edit().putInt(Constantes.PREF_TIPO_CARTERA, tipoCartera).commit();
    }

    public static int getTipoCartera(Context context) {
        return context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .getInt(Constantes.PREF_TIPO_CARTERA, Constantes.TODOS_CLIENTES);
    }


    public static void setFiltroGeoPosicion(Context context, int geoposicion) {
        context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .edit().putInt(Constantes.PREF_GEOPOSICION, geoposicion).apply();
    }

    public static int getFiltroGeoPosicion(Context context) {
        return context.getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .getInt(Constantes.PREF_GEOPOSICION, Constantes.SIN_FILTRO_GEOPOSICION);
    }


    public static void limpiarFiltros(Context context) {

        setRadio(context, Constantes.RADIO);
        setFiltroGeoPosicion(context, Constantes.SIN_FILTRO_GEOPOSICION);
        setTipoCartera(context, Constantes.TODOS_CLIENTES);

    }


}
