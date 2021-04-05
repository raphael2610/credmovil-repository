package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import org.json.JSONArray;
import org.json.JSONObject;

import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaPreferences;
import pe.com.cmacica.flujocredito.ViewModel.ActividadPrincipal;

public class FiltroBusquedaActivity extends AppCompatActivity {

    private static final String TAG = "FiltroBusquedaActivity";
    public static final int LOCATION_INTERVAL = 1000;
    public static final int LOCATION_FAST_INTERVAL = 500;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 100;

    private ImageView _imageClose;
    private ImageView _imageCheck;
    private Button _buttonCurrentAnalyst;
    private Button _buttonCmacica;
    private Button _buttonAll;
    private TextView _textRadioContent;
    private AppCompatSeekBar _seekbarRadio;
    private ProgressDialog _progressDialog;
    private Button _buttonNoGeoPosicionados;
    private Button _buttonGeoPosicionados;
    private Button _buttonApply;
    private Button _buttonClear;
    private Button _buttonAllGeoPosition;


    private int _radio;
    private int _tipoCartera;
    private int _geoposicion;
    private boolean _buttonCmacicaSeleccionado = false;
    private FusedLocationProviderClient _fusedLocationProviderClient;
    private LocationRequest _locationRequest;
    private LocationCallback _locationCallback;
    private Location _location;
    private CarteraAnalistaDbHelper _carteraAnalistaDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_busqueda);

        _imageClose = (ImageView) findViewById(R.id.imageClose);
        _imageCheck = (ImageView) findViewById(R.id.imageCheck);
        _buttonCurrentAnalyst = (Button) findViewById(R.id.buttonCurrentAnalyst);
        _buttonCmacica = (Button) findViewById(R.id.buttonCmacica);
        _buttonAll = (Button) findViewById(R.id.buttonAll);
        _textRadioContent = (TextView) findViewById(R.id.textRadioContent);
        _seekbarRadio = (AppCompatSeekBar) findViewById(R.id.seekbarRadio);
        _buttonGeoPosicionados = (Button) findViewById(R.id.buttonGeoPosicionados);
        _buttonNoGeoPosicionados = (Button) findViewById(R.id.buttonNoGeoPosicionados);
        _buttonApply = (Button) findViewById(R.id.buttonApply);
        _buttonClear = (Button) findViewById(R.id.buttonClear);
        _buttonAllGeoPosition = (Button) findViewById(R.id.buttonAllGeoPosition);

        _carteraAnalistaDbHelper = new CarteraAnalistaDbHelper(this);
        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
        enableConfigurationlocation();


        getPreferences();
        listenerView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void listenerView() {

        _imageCheck.setOnClickListener(v -> saveFilterSearch() );
        _imageClose.setOnClickListener(v -> finish());

        _seekbarRadio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _radio = progress;
                _textRadioContent.setText(progress + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        _buttonCurrentAnalyst.setOnClickListener(v -> buttonSelected(v));
        _buttonCmacica.setOnClickListener(v -> buttonSelected(v));
        _buttonAll.setOnClickListener(v -> buttonSelected(v));

        _buttonGeoPosicionados.setOnClickListener(v -> buttonSelectedGeoposition(v));
        _buttonNoGeoPosicionados.setOnClickListener(v -> buttonSelectedGeoposition(v));
        _buttonAllGeoPosition.setOnClickListener(v -> buttonSelectedGeoposition(v));

        _buttonApply.setOnClickListener( v -> saveFilterSearch() );

        _buttonClear.setOnClickListener(v -> {
            CarteraAnalistaPreferences.limpiarFiltros(getApplicationContext());
            finish();
        });

        //_buttonAllGeoPosition.setOnClickListener();
    }


    private void buttonSelected(View view) {

        if (view.getId() == R.id.buttonCurrentAnalyst) {
            _tipoCartera = Constantes.ACTUAL_ANALISTA;
            _buttonCmacicaSeleccionado = false;
            _buttonCurrentAnalyst.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            _buttonCurrentAnalyst.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            _buttonCmacica.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonCmacica.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

            _buttonAll.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonAll.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

        } else if (view.getId() == R.id.buttonCmacica){
            _tipoCartera = Constantes.OTROS;
            _buttonCmacicaSeleccionado = true;
            _buttonCurrentAnalyst.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonCurrentAnalyst.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

            _buttonCmacica.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            _buttonCmacica.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            _buttonAll.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonAll.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

        } else {

            _tipoCartera = Constantes.TODOS_CLIENTES;

            _buttonCurrentAnalyst.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonCurrentAnalyst.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

            _buttonCmacica.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonCmacica.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

            _buttonAll.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            _buttonAll.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

    }

    private void buttonSelectedGeoposition( View view ) {

        if (view.getId() == R.id.buttonGeoPosicionados) {
            _geoposicion = Constantes.GEOPOSICIONADO;

            _buttonNoGeoPosicionados.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonNoGeoPosicionados.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

            _buttonGeoPosicionados.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            _buttonGeoPosicionados.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            _buttonAllGeoPosition.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonAllGeoPosition.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

        } else if (view.getId() == R.id.buttonNoGeoPosicionados){

            _geoposicion = Constantes.NO_GEOPOSICIONADO;

            _buttonGeoPosicionados.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonGeoPosicionados.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

            _buttonNoGeoPosicionados.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            _buttonNoGeoPosicionados.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            _buttonAllGeoPosition.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonAllGeoPosition.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

        } else {

            _geoposicion = Constantes.SIN_FILTRO_GEOPOSICION;

            _buttonGeoPosicionados.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonGeoPosicionados.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

            _buttonAllGeoPosition.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            _buttonAllGeoPosition.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            _buttonNoGeoPosicionados.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            _buttonNoGeoPosicionados.setBackground(ContextCompat.getDrawable(this, R.drawable.outlined_button));

        }

        Log.d(TAG, "geoposicion " + _geoposicion);

    }


    private void getPreferences() {

        _radio = CarteraAnalistaPreferences.getRadio(getApplicationContext());
        _tipoCartera = CarteraAnalistaPreferences.getTipoCartera(getApplicationContext());
        _geoposicion = CarteraAnalistaPreferences.getFiltroGeoPosicion(getApplicationContext());
        Log.d(TAG, "geoposicion: " + _geoposicion);

        _textRadioContent.setText(_radio + " m");
        _seekbarRadio.setProgress(_radio);

        if (_tipoCartera == Constantes.OTROS) {
            buttonSelected(_buttonCmacica);
        } else if (_tipoCartera == Constantes.ACTUAL_ANALISTA){
            buttonSelected(_buttonCurrentAnalyst);
        } else {
            buttonSelected(_buttonAll);
        }

        if (_geoposicion == Constantes.GEOPOSICIONADO) {
            buttonSelectedGeoposition(_buttonGeoPosicionados);
        } else if (_geoposicion == Constantes.NO_GEOPOSICIONADO){
            buttonSelectedGeoposition(_buttonNoGeoPosicionados);
        } else {
            buttonSelectedGeoposition(_buttonAllGeoPosition);
        }

    }

    private void insertPreferences(int radio, int tipoCartera, int geoposicion) {
        Log.d(TAG, "geoposicion: " + geoposicion);
        CarteraAnalistaPreferences.setRadio(getApplicationContext(), radio);
        CarteraAnalistaPreferences.setTipoCartera(getApplicationContext(), tipoCartera);
        CarteraAnalistaPreferences.setFiltroGeoPosicion(getApplicationContext(), geoposicion);
        Log.d(TAG, "geoposicion: " + geoposicion);
    }


    private void saveFilterSearch() {

        insertPreferences(_radio, _tipoCartera, _geoposicion);
        finish();

        /*
        SharedPreferences sharedPreferences = getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, MODE_PRIVATE);
        boolean getCustomer = sharedPreferences.getBoolean(Constantes.PREF_OBTENER_CLIENTES, true);
        if (!getCustomer) {

            int cantidadInsertadoActualizado = _carteraAnalistaDbHelper.customerInsertUpdate().getCount();

            if (cantidadInsertadoActualizado == 0) {

                int radioAnterior = sharedPreferences.getInt(Constantes.PREF_RADIO, 0);
                int tipoCarteraAnterior = sharedPreferences.getInt(Constantes.PREF_TIPO_CARTERA, 0);

                if (radioAnterior != _radio || tipoCarteraAnterior != _tipoCartera) {

                    if (_location != null) {

                        getCustomersServer(
                                _location.getLatitude(),
                                _location.getLongitude()
                        );

                    } else {
                        Toast.makeText(this, getString(R.string.filtro_busqueda_error_lat_long), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    finish();
                }

            } else {
                Toast.makeText(this, getString(R.string.filtro_busqueda_msg_existe_clientes_editado_insertado), Toast.LENGTH_SHORT).show();
            }

        } else {
            insertPreferences(_radio, _tipoCartera);
            finish();
        }
        */

    }



    private void getCustomersServer(double latitud, double longitud) {
        _progressDialog = ProgressDialog.show(this, getString(R.string.filtro_busqueda_msg_esperar), getString(R.string.filtro_busqueda_msg_get_customer));

        String user = UPreferencias.ObtenerUserLogeo(this);
        user = "ERRC";
        boolean cartera = _tipoCartera == Constantes.ACTUAL_ANALISTA ? true : false;

        /*
        String url = String.format(SrvCmacIca.GET_CARTERA_ANALISTA, latitud, longitud, _radio, "ERRC", cartera);

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServer(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        )
                );
        */

    }

    private void responseServer(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")) {
                JSONArray jsonClientes = response.getJSONArray("Data");

                if(jsonClientes.length() == 0){
                    Toast.makeText(this, getString(R.string.cartera_analista_error_not_customers), Toast.LENGTH_LONG).show();
                } else {
                    deleteCustomer(jsonClientes);
                }
            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void deleteCustomer(JSONArray jsonCustomers) {

        AsyncTask task = new AsyncTask<Object, Void, Integer>() {

            @Override
            protected Integer doInBackground(Object... objects) {
                return _carteraAnalistaDbHelper.deleteAllCustomer();
            }

            @Override
            protected void onPostExecute(Integer integer) {

                if (integer >= 0) {
                    insertarCustomers(jsonCustomers);
                    insertPreferences(_radio, _tipoCartera, _geoposicion);
                } else {
                    Toast.makeText(FiltroBusquedaActivity.this, getString(R.string.filtro_busqueda_error_eliminacion_clientes), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();
    }


    private void insertarCustomers(JSONArray jsonCustomers) {

        AsyncTask task = new AsyncTask<Object, Void, Integer>() {

            @Override
            protected Integer doInBackground(Object... objects) {

                Integer result = -1;

                try {
                    for (int i = 0; i < jsonCustomers.length(); i++) {
                        JSONObject row = jsonCustomers.getJSONObject(i);

                        Cliente cliente = new Cliente();
                        cliente.setNombre(row.getString("Nombres"));
                        cliente.setDoi(row.getString("Doi"));
                        cliente.setTelefono(row.getString("Telefono"));
                        cliente.setLongitud(row.getDouble("Longitud"));
                        cliente.setLatitud(row.getDouble("Latitud"));
                        cliente.setIdTipoDireccion(row.getInt("IdTipoDireccion"));
                        //cliente.setDireccion(row.getString("Direccion"));
                        cliente.setReferencia(row.getString("Referencia"));

                        _carteraAnalistaDbHelper.insertCustomer(cliente, CarteraAnalistaDbHelper.FLAG_ACTUAL_ANALISTA);
                    }
                    result = 0;
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                return result;
            }

            @Override
            protected void onPostExecute(Integer integer) {

                if (integer == 0) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.cartera_analista_error_save_customers), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();
    }




    // location
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                enableMyLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) ) {
            enableMyLocation();
        }

    }


    private void startLocationUpdates() {
        _fusedLocationProviderClient.requestLocationUpdates(_locationRequest, _locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        _fusedLocationProviderClient.removeLocationUpdates(_locationCallback);
    }

    private void createLocationCallback() {

        _locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null){
                    _location = locationResult.getLastLocation();
                    Log.d(TAG, "onLocationResult: " + _location.getLatitude());
                    Log.d(TAG, "onLocationResult: " + _location.getLongitude());
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

    }
    private void createLocationRequest() {
        _locationRequest = LocationRequest.create();
        _locationRequest.setInterval(LOCATION_INTERVAL);
        _locationRequest.setFastestInterval(LOCATION_FAST_INTERVAL);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void enableConfigurationlocation() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(_locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(locationSettingsResponse -> {
                    enableMyLocation();
                })
                .addOnFailureListener(exception -> {

                    if (exception instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult( this ,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) { }
                    }

                });

    }

    private void enableMyLocation() {

        if ( isPermissionGranted() ){
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }

    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }



}
