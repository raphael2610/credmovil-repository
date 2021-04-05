package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONArray;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista.ClienteAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaContract;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaPreferences;
import pe.com.cmacica.flujocredito.ViewModel.ActividadPrincipal;


/**
 * Created by CJCF
 */

public class CarteraAnalistaFragment extends Fragment
        implements ClienteAdapter.ClienteAdapterListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        TextWatcher {

    private static final String TAG = "CarteraAnalistaFragment";
    private static String EXTRA_FILTER_NAME_DOI = "nameDoi";
    private static int LOADER_LIST_CUSTOMERS = 0;
    private static int LOADER_FILTER_CUSTOMERS = 1;
    public static final int LOCATION_INTERVAL = 1000;
    public static final int LOCATION_FAST_INTERVAL = 500;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 100;

    private ConstraintLayout _constraintlayoutGetCustomers;
    private ConstraintLayout _constraintlayoutListCustomers;
    private ImageView _imageLocation;
    private ImageView _imageAdd;
    private ImageView _imageFilter;
    private ImageView _imageLogo;
    private Button _buttonGetCustomers;
    private TextView _textMsgGetCustomers;
    private TextView _textDate;
    private TextInputEditText _textinputedittextSearch;
    private RecyclerView _recyclerviewCustomers;
    private Button _buttonSynchronize;

    private ClienteAdapter _clienteAdapter;
    private ArrayList<Cliente> _clienteList;
    private CarteraAnalistaDbHelper _carteraAnalistaDbHelper;
    private ProgressDialog _progressDialog;
    private FusedLocationProviderClient _fusedLocationProviderClient;
    private LocationCallback _locationCallback;
    private LocationRequest _locationRequest;
    private Location _location;
    private boolean _searchCustomer = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_cartera_analista, container, false);
        _constraintlayoutGetCustomers = (ConstraintLayout) root.findViewById(R.id.constraintlayoutGetCustomers);
        _constraintlayoutListCustomers = (ConstraintLayout) root.findViewById(R.id.constraintlayoutListCustomers);
        _imageLogo = (ImageView) root.findViewById(R.id.imageLogo);
        _buttonGetCustomers = (Button) root.findViewById(R.id.buttonGetCustomers);
        _textMsgGetCustomers = (TextView) root.findViewById(R.id.textMsgGetCustomers);
        _textDate = (TextView) root.findViewById(R.id.textDate);
        _recyclerviewCustomers = (RecyclerView) root.findViewById(R.id.recyclerviewCustomers);
        _textinputedittextSearch = (TextInputEditText) root.findViewById(R.id.textinputedittextSearch);
        _buttonSynchronize = (Button) root.findViewById(R.id.buttonSynchronize);

        showDate();

        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        createLocationRequest();
        createLocationCallback();
        enableConfigurationlocation();

        _buttonGetCustomers.setOnClickListener(v -> searchCustomer());
        _buttonSynchronize.setOnClickListener(v -> {

            if (CarteraAnalistaPreferences.getFlagSyncronization(getContext()) ==  CarteraAnalistaDbHelper.FLAG_SYNCRONIZED) {
                searchCustomer();
            } else {
                synchronizeCustomers();
            }

        });
        _textinputedittextSearch.addTextChangedListener(this);

        valitationGetCustomer();

        setupRecyclerView();
        _carteraAnalistaDbHelper = new CarteraAnalistaDbHelper(getContext());

        cargarTipoDirecciones();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        valitationGetCustomer();

        Log.d(TAG, "onStart: 1");
        if ( !CarteraAnalistaPreferences.getValidationGetCustomer(getContext()) ) {
            Log.d(TAG, "onStart: 2");
            Fragment fragment = getFragmentManager().findFragmentByTag(CarteraAnalistaFragment.class.getSimpleName());
            getLoaderManager().initLoader(LOADER_LIST_CUSTOMERS, null, (CarteraAnalistaFragment)fragment);
        }

        // TODO sincronizacion
        if ( CarteraAnalistaPreferences.getFlagSyncronization(getContext()) == CarteraAnalistaDbHelper.FLAG_NOT_SYNCRONIZED ) {
            _buttonSynchronize.setText(getString(R.string.cartera_analista_action_sync_up));
        } else {
            _buttonSynchronize.setText(getString(R.string.cartera_analista_action_get_customers));
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocationUpdates();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cartera_analista, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_location:
                navigateToMapaGeoreferenciacion();
                break;
            case R.id.menu_add:
                navigateToRegistrarUsuario();
                break;
            case R.id.menu_filter:
                navigateToFiltroBusqueda();
                break;
        }

        _textinputedittextSearch.setText("");

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if ( String.valueOf(s).trim().length() == 0 ) {
            getLoaderManager().initLoader(LOADER_LIST_CUSTOMERS, null, this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_FILTER_NAME_DOI, String.valueOf(s).trim());
            getLoaderManager().initLoader(LOADER_FILTER_CUSTOMERS, bundle, this);
        }


    }

    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public void onClick(Cliente cliente) {
        Log.d(TAG, "onClick: " + cliente.getNombre());
        Log.d(TAG, "onClick: " + cliente.getFlag());
        Log.d(TAG, "onClick: " + cliente.getCreditos());
        navigateToDetalleUsuario(cliente);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;

        if (id==LOADER_LIST_CUSTOMERS) {
            cursorLoader =new CursorLoader(getContext()) {
                @Override
                public Cursor loadInBackground() {
                    return _carteraAnalistaDbHelper.getCustomers();
                }
            };
        } else {
            String filter = args.getString(EXTRA_FILTER_NAME_DOI);
            cursorLoader = new CursorLoader(getContext()) {
                @Override
                public Cursor loadInBackground() {
                    return  filter.isEmpty() ? _carteraAnalistaDbHelper.getCustomers() : _carteraAnalistaDbHelper.filterCustomers(filter);
                }
            };
        }

        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        _clienteList = _carteraAnalistaDbHelper.listCustomers(data);
        int tipoCartera = CarteraAnalistaPreferences.getTipoCartera(getContext());

        if (loader.getId() == LOADER_LIST_CUSTOMERS) {
            getLoaderManager().destroyLoader(LOADER_LIST_CUSTOMERS);
        } else if ( loader.getId() == LOADER_FILTER_CUSTOMERS ) {
            getLoaderManager().destroyLoader(LOADER_FILTER_CUSTOMERS);
        }

        if (tipoCartera != Constantes.TODOS_CLIENTES) {
            _clienteList = applyFilterList(_clienteList, tipoCartera);
        }

        // TODO filtro por radio
        if ( loader.getId() == LOADER_LIST_CUSTOMERS ) {
            _clienteList = applyFilterRadio(_clienteList);
        }


        // TODO filtro geoposicionado
        _clienteList =  applyFilterGeoposicionado(_clienteList);

        _clienteAdapter.updateList(_clienteList);
    }

    private ArrayList<Cliente> applyFilterGeoposicionado(ArrayList<Cliente> clienteList) {

        ArrayList<Cliente> clienteArrayList = new ArrayList<>();
        int  geoposicionado = CarteraAnalistaPreferences.getFiltroGeoPosicion(getContext());

        for (Cliente cliente : clienteList) {

            if (geoposicionado == Constantes.GEOPOSICIONADO) {

                if (cliente.getGeoposicion().toUpperCase().equals("SI")) {
                    clienteArrayList.add(cliente);
                }

            } else if (geoposicionado == Constantes.NO_GEOPOSICIONADO){

                if (cliente.getGeoposicion().toUpperCase().equals("NO")) {
                    clienteArrayList.add(cliente);
                }

            } else {
                clienteArrayList.add(cliente);
            }

        }

        return clienteArrayList;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    private ArrayList<Cliente> applyFilterList(ArrayList<Cliente> clienteList, int flag) {

        ArrayList<Cliente> clientes = new ArrayList<>();

        for (Cliente cliente : clienteList) {

            if (flag == Constantes.ACTUAL_ANALISTA) {

                if (cliente.getFlag() == CarteraAnalistaDbHelper.FLAG_INSERTADO ||
                        cliente.getFlag() == CarteraAnalistaDbHelper.FLAG_ACTUAL_ANALISTA ||
                        cliente.getFlag() == CarteraAnalistaDbHelper.FLAG_ACTUALIZADO) {
                    clientes.add(cliente);
                }

            } else {

                if (cliente.getFlag() == CarteraAnalistaDbHelper.FLAG_OTRAS_CARTERAS_ANALISTA) {
                    clientes.add(cliente);
                }

            }
        }

        return clientes;
    }


    private ArrayList<Cliente> applyFilterRadio(ArrayList<Cliente> clienteList) {

        int radio = CarteraAnalistaPreferences.getRadio(getContext());
        ArrayList<Cliente> clienteArrayList = new ArrayList<>();


        for ( Cliente cliente : clienteList ) {

            if ( (cliente.getLatitud()==null && cliente.getLongitud()==null) ||
                    (cliente.getLatitud() == 0.0 && cliente.getLongitud() == 0.0) ) {
                clienteArrayList.add(cliente);
            } else {

                if ( _location != null ) {

                    Location  locationCliente = new Location("");
                    locationCliente.setLatitude(cliente.getLatitud());
                    locationCliente.setLongitude(cliente.getLongitud());
                    float distance;

                    distance = _location.distanceTo(locationCliente);

                    if (distance <= radio) {
                        clienteArrayList.add(cliente);
                    }

                } else {
                    clienteArrayList.add(cliente);
                }

            }

        }


        return clienteArrayList;
    }

    private void cargarTipoDirecciones() {
        if (_carteraAnalistaDbHelper.countListTypeAddress() == 0) {
            _carteraAnalistaDbHelper.insertTypeAddress();
        }
    }


    private void setupRecyclerView() {
        _clienteAdapter = new ClienteAdapter(this, new ArrayList<>());
        _recyclerviewCustomers.setAdapter(_clienteAdapter);
    }

    private void navigateToMapaGeoreferenciacion() {


        if ( !CarteraAnalistaPreferences.getValidationGetCustomer(getContext()) ) {
            Intent intent = new Intent(getActivity(), MapaGeoreferenciacionActivity.class);
            intent.putExtra(MapaGeoreferenciacionActivity.EXTRA_TYPE_MAP, MapaGeoreferenciacionActivity.TYPE_CUSTOMER_LIST);
            intent.putExtra(MapaGeoreferenciacionActivity.EXTRA_CUSTOMER_LIST, _clienteList);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), getString(R.string.cartera_analista_msg_debe_obtener_clientes), Toast.LENGTH_SHORT).show();
        }

    }

    private void navigateToFiltroBusqueda() {
        startActivity(
                new Intent(getActivity(), FiltroBusquedaActivity.class)
        );
    }

    private void navigateToDetalleUsuario(Cliente cliente) {
        Intent intent = new Intent(getActivity(), DetalleUsuarioActivity.class);
        intent.putExtra(DetalleUsuarioActivity.EXTRA_CUSTOMER, cliente);
        startActivity(intent);
    }

    private void navigateToRegistrarUsuario() {

        startActivity(new Intent(getActivity(), RegistrarUsuarioActivity.class));
        /*
        if ( !CarteraAnalistaPreferences.getValidationGetCustomer(getContext()) ) {
            startActivity(new Intent(getActivity(), RegistrarUsuarioActivity.class));
        } else {
            Toast.makeText(getContext(), getString(R.string.cartera_analista_msg_debe_obtener_clientes), Toast.LENGTH_SHORT).show();
        }
        */
    }

    private void showHideGetCustomer(boolean flag) {
        if (flag) {
            _constraintlayoutGetCustomers.setVisibility(View.VISIBLE);
            _constraintlayoutListCustomers.setVisibility(View.GONE);
        } else {
            _constraintlayoutGetCustomers.setVisibility(View.GONE);
            _constraintlayoutListCustomers.setVisibility(View.VISIBLE);
        }
    }

    private void showDate() {
        long time = CarteraAnalistaPreferences.getDateDownload(getContext());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date;

        if (time == 0) {
            date = simpleDateFormat.format(new Date());
        } else {
            date = simpleDateFormat.format(new Date(time));
        }

        _textDate.setText( String.format(getString(R.string.cartera_analista_date), date) );

    }


    private void insertarCustomers(JSONArray jsonCustomers) {

        AsyncTask task = new AsyncTask<Object, Void, Integer>() {

            @Override
            protected Integer doInBackground(Object... objects) {

                Integer result = -1;

                try {

                    _carteraAnalistaDbHelper.deleteAllCustomer();

                    for (int i = 0; i < jsonCustomers.length(); i++) {
                        JSONObject row = jsonCustomers.getJSONObject(i);

                        Cliente cliente = new Cliente();

                        cliente.setPersCod(row.getString("PersCod"));
                        cliente.setNombre(row.getString("PersNombre"));
                        cliente.setDoi(row.getString("NumeroDocumento"));
                        cliente.setDireccionDomicilio(row.getString("PersDireccDomicilio"));
                        cliente.setTelefonoUno(row.getString("PersTelefono"));
                        cliente.setTelefonoDos(row.getString("PersTelefono2"));
                        cliente.setGeoposicion(row.getString("GeoPosicion"));
                        cliente.setCreditos(row.getString("Creditos"));


                        cliente.setTelefono(row.getString("Telefono"));
                        cliente.setLongitud(row.getDouble("Longitud"));
                        cliente.setLatitud(row.getDouble("Latitud"));
                        cliente.setIdTipoDireccion(row.getInt("TipoDireccion"));
                        cliente.setReferencia(row.getString("Direccion"));

                        // TODO sincronizar
                        cliente.setSincronizar(CarteraAnalistaDbHelper.FLAG_SYNCRONIZED);

                        _carteraAnalistaDbHelper.insertCustomerPortafolio(cliente, CarteraAnalistaDbHelper.FLAG_ACTUAL_ANALISTA);
                    }
                    result = 0;
                    Log.d(TAG, "doInBackground: trajo datos ");
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: " + e.getMessage());
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                return result;
            }

            @Override
            protected void onPostExecute(Integer integer) {

                if (integer == 0) {
                    Fragment fragment = getFragmentManager().findFragmentByTag(CarteraAnalistaFragment.class.getSimpleName());
                    getLoaderManager().initLoader(LOADER_LIST_CUSTOMERS, null, (CarteraAnalistaFragment)fragment);
                    CarteraAnalistaPreferences.setValidationGetCustomer(getContext(), false);
                    CarteraAnalistaPreferences.setDateDownload(getContext());
                    showDate();
                    showHideGetCustomer(false);

                    // TODO sincronizar
                    CarteraAnalistaPreferences.setFlagSyncronization(getContext(), CarteraAnalistaDbHelper.FLAG_SYNCRONIZED);

                    Toast.makeText(getActivity(), getString(R.string.cartera_analista_msg_success_customers), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.cartera_analista_error_save_customers), Toast.LENGTH_SHORT).show();
                }

            }
        };
        task.execute();

    }



    private void searchCustomer() {

        if ( _location == null ) {
            _searchCustomer = true;
            enableConfigurationlocation();
        } else {
            getCustomersServer( _location.getLatitude(), _location.getLongitude() );
        }

    }

    private void getCustomersServer(double latitud, double longitud) {

        Log.d(TAG, "getCustomersServer: " + latitud);
        Log.d(TAG, "getCustomersServer: " + longitud);
        _searchCustomer = false;
        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.cartera_analista_msg_esperar), getString(R.string.cartera_analista_msg_get_customer));

        int radio = CarteraAnalistaPreferences.getRadio(getContext());
        int tipoCartera = CarteraAnalistaPreferences.getTipoCartera(getContext());

        boolean cartera = tipoCartera == Constantes.ACTUAL_ANALISTA ? true : false;

        String user = UPreferencias.ObtenerUserLogeo(getContext());


        //String url = String.format(SrvCmacIca.GET_CARTERA_ANALISTA, latitud, longitud, radio, user, true);
        String url = String.format(SrvCmacIca.GET_CLIENTES_CARTERA, user);

        VolleySingleton
                .getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> {
                                    Log.d(TAG, "getCustomersServer: " + response);
                                    responseServer(response);
                                },
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        )
                );
    }




    private void responseServer(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")) {
                JSONArray jsonClientes = response.getJSONArray("Data");

                if(jsonClientes.length() == 0){
                    Toast.makeText(getActivity(), getString(R.string.cartera_analista_error_not_customers), Toast.LENGTH_LONG).show();
                    _carteraAnalistaDbHelper.deleteAllCustomer();
                    getLoaderManager().initLoader(LOADER_LIST_CUSTOMERS,  null, this);
                } else {
                    insertarCustomers(jsonClientes);
                }
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    private void valitationGetCustomer() {
        boolean validacion = CarteraAnalistaPreferences.getValidationGetCustomer(getContext());
        showHideGetCustomer(validacion);

        if (!validacion) {
            getLoaderManager().initLoader(LOADER_LIST_CUSTOMERS, null, this);
        }
    }


    private void synchronizeCustomers() {

        Log.d(TAG, "synchronizeCustomers: ");
        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.cartera_analista_msg_esperar), getString(R.string.cartera_analista_msg_sincronizando_clientes));


        try {

            ArrayList<Cliente> allClient = _carteraAnalistaDbHelper.getAllCustomers(getContext());
            if (allClient!=null) {

                Log.d(TAG, "synchronizeCustomers: " + allClient.size());
                Gson gsonpojo = new GsonBuilder().create();
                String json = gsonpojo.toJson(allClient);
                HashMap<String, String> cabeceras = new HashMap<>();

                Log.d(TAG, "synchronizeCustomers: " + json);

                new RESTService(getActivity()).post(
                        SrvCmacIca.POST_GEOCLIENTES,
                        json,
                        response -> responseServerSynchronize(response),
                        error -> {
                            _progressDialog.cancel();
                            Toast.makeText(getActivity(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        },
                        cabeceras
                );

            } else {
                Log.d(TAG, "synchronizeCustomers: error");
            }

        } catch (Exception e) {
            _progressDialog.cancel();
            Toast.makeText(getContext(), getString(R.string.cartera_analista_error_sincronizar_clientes), Toast.LENGTH_SHORT).show();
        }

    }

    private void responseServerSynchronize(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(getActivity(), "¡Clientes guardados correctamente!", Toast.LENGTH_SHORT).show();

                _carteraAnalistaDbHelper.updateClientSynchronizationStatus(getContext());

                // TODO sincronizacion
                //_carteraAnalistaDbHelper.deleteAllCustomer();
                //_clienteAdapter.updateList(new ArrayList<>());

                //saveValidation(true);
                //showHideGetCustomer(true);

                CarteraAnalistaPreferences.setFlagSyncronization(getContext(), CarteraAnalistaDbHelper.FLAG_SYNCRONIZED);
                _buttonSynchronize.setText(getString(R.string.cartera_analista_action_get_customers));
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

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

                if (locationResult!=null) {
                    _location = locationResult.getLastLocation();

                    if (_location!=null){
                        if (_searchCustomer) {
                            getCustomersServer(_location.getLatitude(), _location.getLongitude());
                        }
                    }
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(locationSettingsResponse -> {
                    enableMyLocation();
                })
                .addOnFailureListener(exception -> {

                    if (exception instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult( (ActividadPrincipal)getContext() ,
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
                    getActivity(),
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }

    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
