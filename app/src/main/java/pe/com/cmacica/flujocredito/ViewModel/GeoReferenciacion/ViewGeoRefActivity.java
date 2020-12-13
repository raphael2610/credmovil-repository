package pe.com.cmacica.flujocredito.ViewModel.GeoReferenciacion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.GeoRefClienteModel;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.InstruccionModel;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.LocalizacionModel;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.GeoReferenciacion.AdaptadorInstruccion;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.DireccionParse;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.GeoLocalizacion;
import pe.com.cmacica.flujocredito.Utilitarios.UGeneral;

/**
 * by MFPE - 2019.
 */
public class ViewGeoRefActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView tvLocationInfo;
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private ImageView imgArrow;
    private RecyclerView rvInstrucciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_geo_ref);

        tvLocationInfo = (TextView) findViewById(R.id.tv_location_info);
        layoutBottomSheet = (LinearLayout) findViewById(R.id.layout_modal_direcciones);
        imgArrow = (ImageView) findViewById(R.id.img_arrow_modal);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        rvInstrucciones = (RecyclerView) findViewById(R.id.rv_instrucciones);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        imgArrow.setImageResource(R.drawable.ic_expand_more);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        imgArrow.setImageResource(R.drawable.ic_expand_less);
                    }
                    break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GeoLocalizacion location = new GeoLocalizacion();
        LocalizacionModel loc = location.GetActualPosition(this);
        LatLng ubicacionActual = new LatLng(loc.getLatitud(), loc.getLongitud());

        mMap.addMarker(new MarkerOptions().position(ubicacionActual).title("Ubicación actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        Intent i = getIntent();
        GeoRefClienteModel clienteIntent = (GeoRefClienteModel) i.getSerializableExtra("clienteSeleccionado");
        if (clienteIntent == null) return;

        LatLng ubicacionCliente = new LatLng(clienteIntent.getLatitud(), clienteIntent.getLongitud());

        Float color = BitmapDescriptorFactory.HUE_RED;
        String titulo = "Ubicación cliente";

        mMap.addMarker(new MarkerOptions().position(ubicacionCliente).title(titulo).icon(BitmapDescriptorFactory.defaultMarker(color)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.5f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLngBounds.builder().include(ubicacionActual).include(ubicacionCliente).build().getCenter()));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setPadding(0,0,0,165);

        TaskRequestDirections task = new TaskRequestDirections();
        String url = task.getSolicitudUrl(ubicacionActual, ubicacionCliente);
        task.execute(url);

    }


    public class TaskRequestDirections extends AsyncTask<String, Void ,String >{

        String getSolicitudUrl(LatLng origen, LatLng destino){
            return String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s,%s&sensor=false&language=es&mode=driving&key=AIzaSyBmrv89CVgjcFJGuhZN6cblM9ms77GeaHg", origen.latitude, origen.longitude, destino.latitude, destino.longitude);
        }

        String solicitaDireccion(String mUrl) throws IOException {
            String respuesta = "";
            InputStream inputStream = null;
            HttpURLConnection conexionHttp = null;

            try{
                URL u = new URL(mUrl);
                conexionHttp = (HttpURLConnection) u.openConnection();
                conexionHttp.setReadTimeout(10000);
                conexionHttp.setConnectTimeout(15000);
                conexionHttp.setRequestMethod("GET");
                conexionHttp.setDoInput(true);
                conexionHttp.connect();

                inputStream = conexionHttp.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader buffer = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String linea =buffer.readLine();

                while (linea != null){
                    sb.append(linea);
                    linea = buffer.readLine();
                }

                respuesta = sb.toString();
                buffer.close();
                reader.close();

            } catch (Exception ex){
                String error = ex.getMessage();
            } finally {
                if(inputStream != null) inputStream.close();
                if(conexionHttp !=null) conexionHttp.disconnect();
            }
            return respuesta;
        }

        @Override
        protected String doInBackground(String... strings) {
            String respuesta = "";
            try{
                respuesta = solicitaDireccion(strings[0]);
            } catch (Exception ex) {

            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String result){
            TaskParser convertidor = new TaskParser();
            convertidor.execute(result);
        }

    }

    public class TaskParser extends AsyncTask<String, Void , List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObjeto = null;
            List<List<HashMap<String, String>>> rutas = null;

            try {
                jsonObjeto = new JSONObject(strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            DireccionParse convertidor = new DireccionParse();
            try{
                rutas = convertidor.parse(jsonObjeto);

                ActualizarDatos(convertidor.Duracion, convertidor.Distancia, convertidor.ListaInstrucciones, convertidor.ListaInstruccionesiconos);

            } catch (Exception ex) {

            }
            return rutas;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists){
            ArrayList<LatLng> puntos = null;
            PolylineOptions opciones = null;

            for (List<HashMap<String, String>> path : lists){
                puntos = new ArrayList<>();
                opciones = new PolylineOptions();

                for (HashMap<String, String> point : path ){
                    String lat = point.get("lat");
                    String lon = point.get("lng");
                    puntos.add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lon)));
                }
                opciones.addAll(puntos);
                opciones.color(Color.rgb(255, 129, 0));
                opciones.width(15.2F);
                opciones.geodesic(true);
            }

            if (opciones != null){
                mMap.addPolyline(opciones);
            }
        }
    }

    public void ActualizarDatos(String duration, String distance, ArrayList<String> instrucciones, ArrayList<String> iconos){
        ArrayList<Integer> imagenes = new ArrayList<>();
        ArrayList<InstruccionModel> listaInstrucciones = new ArrayList<>();

        for (String icono: iconos){
            imagenes.add(UGeneral.ConvertirIconos(icono));
        }

        for(int i = 0; i < instrucciones.size(); i++) {
            listaInstrucciones.add(new InstruccionModel(imagenes.get(i), instrucciones.get(i)));
        }

        runOnUiThread(() -> {
            tvLocationInfo.setText(String.format("Duración: %s     |     Distancia: %s", duration, distance));

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvInstrucciones.setLayoutManager(layoutManager);
            AdaptadorInstruccion adaptador = new AdaptadorInstruccion(listaInstrucciones);
            rvInstrucciones.setAdapter(adaptador);
        });
    }

}