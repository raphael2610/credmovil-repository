package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.LocalizacionModel;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.Model.carteraanalista.TipoDireccion;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaPreferences;


public class AnadeDireccionActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "AnadeDireccionActivity";
    public static final String EXTRA_CUSTOMER = "customer";
    public static final String EXTRA_UPDATE_INSERT = "update_insert";
    private static final int LOADER_ADDRESS = 0;
    public static final int LOCATION_INTERVAL = 1000;
    public static final int LOCATION_FAST_INTERVAL = 500;
    public static final int REQUEST_CHECK_SETTINGS = 100;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float ZOOM_LEVEL = 17f;


    private Toolbar _toolbar;
    private Button _buttonSaveAddress;
    private EditText _edittextReference;
    private Spinner _spinnerTypeAddress;
    private TextView _textLatitude;
    private TextView _textLongitude;

    private GoogleMap _googleMap;
    private Cliente _cliente;
    private LocalizacionModel _localizacionModel;
    private Double _latitud;
    private Double _longitud;
    private CarteraAnalistaDbHelper _carteraAnalistaDbHelper;
    private int _flagUpdateInsert;
    private FusedLocationProviderClient _fusedLocationProviderClient;
    private LocationRequest _locationRequest;
    private LocationCallback _locationCallback;
    private Location _location;
    private LatLng OFICINA_PRINCIPAL = new LatLng(-14.064944, -75.730348);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anade_direccion);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _buttonSaveAddress = (Button) findViewById(R.id.buttonSaveAddress);
        _edittextReference = (EditText) findViewById(R.id.edittextReference);
        _spinnerTypeAddress = (Spinner) findViewById(R.id.spinnerTypeAddress);
        _textLatitude = (TextView) findViewById(R.id.textLatitude);
        _textLongitude = (TextView) findViewById(R.id.textLongitude);

        _buttonSaveAddress.setOnClickListener(v -> saveAddress());

        _carteraAnalistaDbHelper = new CarteraAnalistaDbHelper(getApplicationContext());
        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
        enableConfigurationlocation();

        //getSupportLoaderManager().initLoader(LOADER_ADDRESS, null, this);
        initToolbar();
        setupGoogleMaps();
        initializeAndGetInformation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                return _carteraAnalistaDbHelper.getTypeAddress();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<TipoDireccion> tipoDireccions = _carteraAnalistaDbHelper.listTypeAddress(data);
        //setupSpinner(tipoDireccions);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.anade_direccion_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupGoogleMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeAndGetInformation() {
        boolean validador = getIntent().getParcelableExtra(EXTRA_CUSTOMER) instanceof Cliente;

        if ( validador ) {
            _cliente = getIntent().getParcelableExtra(EXTRA_CUSTOMER);
            _flagUpdateInsert = getIntent().getIntExtra(EXTRA_UPDATE_INSERT, 0);
        }
        showAddress();
        setupSpinner(_cliente);
    }

    private void saveAddress() {


        if (_edittextReference.getText().length() == 0) {
            Toast.makeText(this, getString(R.string.anade_direccion_msg_detail_address), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_location==null) {
            Toast.makeText(this, getString(R.string.anade_direccion_error_lat_long), Toast.LENGTH_SHORT).show();
            return;
        }


        _cliente.setLatitud(_location.getLatitude());
        _cliente.setLongitud(_location.getLongitude());
        _cliente.setIdTipoDireccion(((TipoDireccion)_spinnerTypeAddress.getSelectedItem()).getId());
        _cliente.setReferencia(_edittextReference.getText().toString());

        Log.d(TAG, "saveAddress: " + _cliente.getIdTipoDireccion());

        // TODO sincronizar
        _cliente.setSincronizar(CarteraAnalistaDbHelper.FLAG_NOT_SYNCRONIZED);


        AsyncTask task = new AsyncTask<Object, Void, Integer>() {
            @Override
            protected Integer doInBackground(Object... objects) {

                int result = 0;
                if (_flagUpdateInsert == GeoreferenciarActivity.FLAG_INSERT_ADDRESS) {
                    //_cliente.setFlag(CarteraAnalistaDbHelper.FLAG_INSERTADO);
                    result = _carteraAnalistaDbHelper.insertAddress(_cliente);
                } else if (_flagUpdateInsert == GeoreferenciarActivity.FLAG_UPDATE_ADDRESS) {
                    result = _carteraAnalistaDbHelper.updateAddress(_cliente);
                }

                return result;
            }

            @Override
            protected void onPostExecute(Integer aInteger) {

                switch (aInteger) {
                    case CarteraAnalistaDbHelper.FLAG_INSERTAR_DIRECCION_EXITOSO:
                        Toast.makeText(AnadeDireccionActivity.this, getString(R.string.anade_direccion_msg_insertado_exitoso), Toast.LENGTH_SHORT).show();
                        //setFlagSyncronization(CarteraAnalistaDbHelper.FLAG_NOT_SYNCRONIZED);
                        CarteraAnalistaPreferences.setFlagSyncronization(getApplicationContext(), CarteraAnalistaDbHelper.FLAG_NOT_SYNCRONIZED);
                        finish();
                        break;
                    case CarteraAnalistaDbHelper.FLAG_INSERTAR_DIRECCION_ERROR_EXISTENTE:
                        Toast.makeText(AnadeDireccionActivity.this, getString(R.string.anade_direccion_error_insertado_existente), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(AnadeDireccionActivity.this, getString(R.string.anade_direccion_error_save_address), Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };
        task.execute();

    }



    private void setupSpinner(Cliente cliente) {

        ArrayList<TipoDireccion> tipoDireccions = new ArrayList<>();
        TipoDireccion tipoDireccionDomicilio = new TipoDireccion();
        tipoDireccionDomicilio.setId(1);
        tipoDireccionDomicilio.setName("Dirección Domicilio");

        TipoDireccion tipoDireccionNegocio = new TipoDireccion();
        tipoDireccionNegocio.setId(2);
        tipoDireccionNegocio.setName("Dirección Negocio");

        tipoDireccions.add(tipoDireccionDomicilio);
        tipoDireccions.add(tipoDireccionNegocio);

        // TODO 1 obtenemos las direcciones del cliente
        Cursor cursor = _carteraAnalistaDbHelper.getAllAddressByCustomer(cliente.getDoi());

        // TODO 2 validamos que no sea repetida
        ArrayList<Cliente> clienteArrayList = _carteraAnalistaDbHelper.listCustomers(cursor);


        if (_flagUpdateInsert == GeoreferenciarActivity.FLAG_INSERT_ADDRESS) {

            for (Cliente itemCliente : clienteArrayList) {
                if (itemCliente.getIdTipoDireccion() == 1) {
                    tipoDireccions.remove(tipoDireccionDomicilio);
                } else if (itemCliente.getIdTipoDireccion() == 2) {
                    tipoDireccions.remove(tipoDireccionNegocio);
                }
            }

        }

        ArrayAdapter<TipoDireccion> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tipoDireccions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerTypeAddress.setAdapter(adapter);

        if (_flagUpdateInsert == GeoreferenciarActivity.FLAG_UPDATE_ADDRESS) {

            if (_cliente.getIdTipoDireccion() == 1) {
                _spinnerTypeAddress.setSelection(0);
            } else {
                _spinnerTypeAddress.setSelection(1);
            }

        }


    }

    private void showAddress() {

        if (_flagUpdateInsert==GeoreferenciarActivity.FLAG_UPDATE_ADDRESS) {
            _textLatitude.setText(String.format(getString(R.string.anade_direccion_latitud), _cliente.getLatitud().toString()));
            _textLongitude.setText(String.format(getString(R.string.anade_direccion_longitud), _cliente.getLongitud().toString()));
            _edittextReference.setText(_cliente.getReferencia());
            _spinnerTypeAddress.setEnabled(false);
        } else {
            _textLatitude.setText(String.format(getString(R.string.anade_direccion_latitud), "0.0"));
            _textLongitude.setText(String.format(getString(R.string.anade_direccion_longitud), "0.0"));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _googleMap = googleMap;
        updateLocationUI();
    }



    private void startLocationUpdates() {
        _fusedLocationProviderClient.requestLocationUpdates(_locationRequest, _locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        _fusedLocationProviderClient.removeLocationUpdates(_locationCallback);
    }


    private void createLocationRequest() {
        _locationRequest = LocationRequest.create();
        _locationRequest.setInterval(LOCATION_INTERVAL);
        _locationRequest.setFastestInterval(LOCATION_FAST_INTERVAL);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {

        _locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult!=null) {
                    _location = locationResult.getLastLocation();

                    if (_googleMap==null){
                        return;
                    }

                    if (_flagUpdateInsert==GeoreferenciarActivity.FLAG_UPDATE_ADDRESS) {
                        _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(_cliente.getLatitud(), _cliente.getLongitud()), ZOOM_LEVEL
                        ));
                        _textLatitude.setText(String.format(getString(R.string.anade_direccion_latitud), String.valueOf(_location.getLatitude()) ));
                        _textLongitude.setText(String.format(getString(R.string.anade_direccion_longitud), String.valueOf(_location.getLongitude())));
                    } else {
                        Log.d(TAG, "onLocationResult: " + _location.getLatitude());
                        _textLatitude.setText(String.format(getString(R.string.anade_direccion_latitud), String.valueOf(_location.getLatitude()) ));
                        _textLongitude.setText(String.format(getString(R.string.anade_direccion_longitud), String.valueOf(_location.getLongitude())));
                        _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(_location.getLatitude(), _location.getLongitude()), ZOOM_LEVEL
                        ));
                    }

                }

            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

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

    private void updateLocationUI() {

        if (_googleMap==null) {
            return;
        } else {

            if (_flagUpdateInsert == GeoreferenciarActivity.FLAG_UPDATE_ADDRESS){
                _googleMap.addMarker(
                        new MarkerOptions().position(new LatLng(_cliente.getLatitud(), _cliente.getLongitud()))
                );
                return;
            }

            if ( isPermissionGranted() ) {
                _googleMap.setMyLocationEnabled(true);
                _googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                _googleMap.setMyLocationEnabled(false);
                _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }

            _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(OFICINA_PRINCIPAL, ZOOM_LEVEL));

        }

    }



}
