package pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial;

import android.content.Context;

import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;

public class AgendaComercialPreferences {


    public static void setFlagSyncronization(Context context, int flag) {
        context.getSharedPreferences(Constantes.SHARED_PREF_AGENDA_COMERCIAL, Context.MODE_PRIVATE)
                .edit().putInt(Constantes.PREF_SINCRONIZACION, flag).commit();
    }

    public static int getFlagSyncronization(Context context) {
        return context.getSharedPreferences(Constantes.SHARED_PREF_AGENDA_COMERCIAL, Context.MODE_PRIVATE)
                .getInt(Constantes.PREF_SINCRONIZACION, CarteraAnalistaDbHelper.FLAG_SYNCRONIZED);
    }

    public static void setIdUser(Context context, int idUser) {
        context.getSharedPreferences(Constantes.SHARED_PREF_AGENDA_COMERCIAL, Context.MODE_PRIVATE)
                .edit().putInt(Constantes.PREF_ID_USUARIO, idUser).commit();
    }

    public static int getIdUser(Context context) {
        return context.getSharedPreferences(Constantes.SHARED_PREF_AGENDA_COMERCIAL, Context.MODE_PRIVATE)
                .getInt(Constantes.PREF_ID_USUARIO, 0);
    }


}
