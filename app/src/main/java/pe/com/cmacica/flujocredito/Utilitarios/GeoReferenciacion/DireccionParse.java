package pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion;

import android.text.Html;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * by MFPE - 2019.
 */
public class DireccionParse {

    public String Duracion = "";
    public String Distancia = "";
    public ArrayList<String> ListaInstrucciones = new ArrayList<>();
    public ArrayList<String> ListaInstruccionesiconos = new ArrayList<>();

    public List<List<HashMap<String,String>>> parse(JSONObject jsonObject){
        ArrayList<List<HashMap<String, String>>> rutas = new ArrayList<>();
        JSONArray jRutas = null;
        JSONArray jLegs = null;
        JSONArray jPasos = null;

        try{
            jRutas = jsonObject.getJSONArray("routes");
            for (int i = 0; i < jRutas.length(); i++) {
                jLegs = ((JSONObject) jRutas.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                for (int j = 0; j < jLegs.length(); j++) {
                    jPasos= ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    this.Duracion = ((JSONObject) jLegs.get(j)).getJSONObject("duration").getString("text");
                    this.Distancia = ((JSONObject) jLegs.get(j)).getJSONObject("distance").getString("text");

                    for (int k = 0; k < jPasos.length(); k++) {
                        String linea = (String) ((JSONObject)((JSONObject) jPasos.get(k)).get("polyline")).get("points");
                        String iconos = "";
                        String instrucciones = "";

                        if(((JSONObject) jPasos.get(k)).has("html_instructions")){
                            instrucciones = ((JSONObject) jPasos.get(k)).getString("html_instructions");
                            instrucciones = Html.fromHtml(instrucciones).toString();
                            instrucciones = instrucciones.replace("\\<.*?>", "");
                            ListaInstrucciones.add(instrucciones);

                            if(((JSONObject) jPasos.get(k)).has("maneuver")){
                                iconos = ((JSONObject) jPasos.get(k)).getString("maneuver");
                            }
                            else {
                                iconos = "";
                            }
                            ListaInstruccionesiconos.add(iconos);
                        }

                        List<LatLng> list = decodePoly(linea);
                        for (int l = 0; l < list.size(); l++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(list.get(l).latitude));
                            hm.put("lng", Double.toString(list.get(l).longitude));
                            path.add(hm);
                        }
                    }
                    rutas.add(path);
                }
            }

        } catch (JSONException e) {

        } catch (Exception e){

        }
        return rutas;
    }

    public ArrayList<LatLng> decodePoly(String encoded){
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while(index < len){
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double llat = (double) lat;
            llat = llat/100000;
            double llong= (double) lng;
            llong = llong/100000;
            LatLng p = new LatLng(llat,llong);
            poly.add(p);
        }
        return poly;
    }
}