package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;

public class MapaGeoreferenciacionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapaGeoreferenciacionAc";

    public static final String EXTRA_TYPE_MAP = "type_map";
    public static final String EXTRA_CUSTOMER = "customer";
    public static final String EXTRA_CUSTOMER_LIST = "customer_list";
    public static final int TYPE_CUSTOMER = 1;
    public static final int TYPE_CUSTOMER_LIST = 2;
    public static final float ZOOM_LEVEL = 17f;

    private Toolbar _toolbar;
    private GoogleMap _googleMap;

    private ArrayList<Cliente> _clienteArrayList;
    private Cliente _cliente;
    private int _type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_georeferenciacion);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar();
        initializeAndGetInformation();
        setupGoogleMaps();

    }

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        getSupportActionBar().setTitle(R.string.mapa_georeferenciacion_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _toolbar.setNavigationOnClickListener(v -> finish());
    }


    private void initializeAndGetInformation() {

        _type = getIntent().getIntExtra(EXTRA_TYPE_MAP, 0) ;

        if (_type == TYPE_CUSTOMER_LIST) {
            Log.d(TAG, "initializeAndGetInformation: todos");
            _clienteArrayList = getIntent().getParcelableArrayListExtra(EXTRA_CUSTOMER_LIST);
        } else {
            Log.d(TAG, "initializeAndGetInformation: uno");
            _cliente = getIntent().getParcelableExtra(EXTRA_CUSTOMER);
        }

    }

    private void setupGoogleMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        _googleMap = googleMap;
        showCustomer(_type);
    }

    private void showCustomer(int type) {

        Log.d(TAG, "showCustomer: " + type);
        if ( type == TYPE_CUSTOMER ) {

            if (_cliente!=null) {
                showMarker(_cliente.getLatitud(), _cliente.getLongitud());
            }

        } else {

            if (_clienteArrayList!=null) {

                for (Cliente cliente : _clienteArrayList) {
                    showMarker(cliente.getLatitud(), cliente.getLongitud());
                }

            }

        }

    }

    private void showMarker(double latitud, double longitud) {

        Log.d(TAG, "showMarker: " + latitud);
        LatLng latLng = new LatLng(latitud, longitud);
        _googleMap.addMarker(
                new MarkerOptions().position(latLng) );

        _googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));

    }






}
