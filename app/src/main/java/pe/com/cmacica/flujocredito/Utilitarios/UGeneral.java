package pe.com.cmacica.flujocredito.Utilitarios;

import android.util.Log;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pe.com.cmacica.flujocredito.R;


/**
 * Created by jhcc on 31/08/2016.
 */
public class UGeneral {

    public static long obtenerTiempoEnMS() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String obtenerTiempo() {
        Date date = GregorianCalendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String obtenerTiempoCorto() {
        Date date = GregorianCalendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static Integer ConvertirIconos(String icono){
        int icon;
        switch (icono){
            case "" :
                icon = R.drawable.clear;
                break;
            case "turn-sharp-left" :
                icon = R.drawable.hard_left;
                break;
            case "turn-sharp-right" :
                icon = R.drawable.hard_right;
                break;
            case "uturn-left" :
                icon = R.drawable.uturn_left;
                break;
            case "uturn-right" :
                icon = R.drawable.uturn_right;
                break;
            case "turn-slight-left" :
                icon = R.drawable.slightly_left;
                break;
            case "turn-slight-right" :
                icon = R.drawable.slightly_right;
                break;
            case "ramp-left" :
                icon = R.drawable.turn_left;
                break;
            case "ramp-right" :
                icon = R.drawable.turn_right;
                break;
            case "turn-left" :
                icon = R.drawable.left;
                break;
            case "turn-right" :
                icon = R.drawable.right;
                break;
            case "fork-left" :
                icon = R.drawable.exit_left;
                break;
            case "fork-right" :
                icon = R.drawable.exit_right;
                break;
            case "merge" :
                icon = R.drawable.merge;
                break;
            case "roundabout-left" :
                icon = R.drawable.circle_clockwise;
                break;
            case "roundabout-right" :
                icon = R.drawable.circle_counterclockwise;
                break;
            case "straight" :
                icon = R.drawable.continua;
                break;
            default :
                icon = R.drawable.clear;
                break;
        }
        return icon;
    }


    public static boolean validaFechaMascara(String sFecha, String sMascara)
    {
        boolean retorno = false;
        try {
            //Convertir la fecha al Calendar
            java.util.Locale locInstancia = new java.util.Locale("es","PE");
            java.text.DateFormat dfInstancia;
            java.util.Date dInstancia;
            dfInstancia = new java.text.SimpleDateFormat(sMascara,locInstancia);
            dInstancia = (java.util.Date)dfInstancia.parse(sFecha);
            java.util.Calendar cal = java.util.Calendar.getInstance(locInstancia);
            cal.setTime(dInstancia); //setear la fecha en cuestion al calendario
            retorno = true;
        } catch (java.text.ParseException excep) {
            retorno = false; //unparseable date
        } finally {
            return retorno;
        }
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static double RoundDouble(double monto){
        return Math.round(monto*100.0)/100.0;
    }

    public static Date ConvertStringToDate(String fechaCadena){

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date FechaDate = null;
        try{
            FechaDate = formato.parse(fechaCadena);


        }catch(ParseException ex){
            Log.d("Utilitario",ex.getMessage());
        }
        return FechaDate;
    }

    public static int DaysEntreFechas (String DateStart, String DateEnd){
        Date FechaInicio = ConvertStringToDate(DateStart);
        Date FechaFin = ConvertStringToDate(DateEnd);

        long diff = FechaInicio.getTime()-FechaFin.getTime();
        int result  = (int) diff/(24 * 60 * 60 * 1000);
        return result;
    }

}
