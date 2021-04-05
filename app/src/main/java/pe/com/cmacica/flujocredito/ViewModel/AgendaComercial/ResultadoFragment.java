package pe.com.cmacica.flujocredito.ViewModel.AgendaComercial;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Oferta;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Producto;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Resultado;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ResultadoVisita;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.TipoContacto;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialPreferences;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;


public class ResultadoFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        RegistroResultadoActivity.RegistrarResultadolistener {


    private static final String TAG = "ResultadoFragment";
    public static final String EXTRA_VISIT = "visit";
    public static final int LOADER_RESULTS_VISITS = 1;
    public static final int LOADER_PRODUCTS_OFFERED = 2;
    public static final int LOADER_TYPE_CONTACT = 3;
    public static final int LOADER_OFFERS = 4;
    public static final int LOCATION_INTERVAL = 1000;
    public static final int LOCATION_FAST_INTERVAL = 500;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 100;

    private Spinner _spinnerOffer;
    private Spinner _spinnerContactType;
    private Spinner _spinnerResult;
    private Spinner _spinnerOfferResult;
    private TextInputEditText _textinputedittextAmount;
    private TextView _textDateVisitContent;
    private TextView _textofferResult;
    private TextView _textAmount;
    private TextInputLayout _textinputlayoutAmount;
    private TextView _textNewDateVisit;

    private ProgressDialog _progressDialog;
    private AgendaComercialDbHelper _agendaComercialDbHelper;
    private TipoContacto _typeContact;
    private int _year;
    private int _month;
    private int _dayOfMonth;
    private ClienteVisita _clienteVisita;
    private LocationRequest _locationRequest;
    private LocationCallback _locationCallback;
    private FusedLocationProviderClient _fusedLocationProviderClient;
    private Location _location;


    // region lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resultado, container, false);

        _spinnerOffer = (Spinner) view.findViewById(R.id.spinnerOffer);
        _spinnerContactType = (Spinner) view.findViewById(R.id.spinnerContactType);
        _spinnerResult = (Spinner) view.findViewById(R.id.spinnerResult);
        _spinnerOfferResult = (Spinner) view.findViewById(R.id.spinnerOfferResult);
        _textinputedittextAmount = (TextInputEditText) view.findViewById(R.id.textinputedittextAmount);
        _textDateVisitContent = (TextView) view.findViewById(R.id.textDateVisitContent);
        _textofferResult = (TextView) view.findViewById(R.id.textofferResult);
        _textAmount = (TextView) view.findViewById(R.id.textAmount);
        _textinputlayoutAmount = (TextInputLayout) view.findViewById(R.id.textinputlayoutAmount);
        _textNewDateVisit = (TextView) view.findViewById(R.id.textNewDateVisit);

        _agendaComercialDbHelper = new AgendaComercialDbHelper(getContext());
        _fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
        createLocationRequest();
        createLocationCallback();
        enableConfigurationlocation();
        _textDateVisitContent.setOnClickListener(v -> showCalendar());

        getLoaderManager().initLoader(LOADER_PRODUCTS_OFFERED, null, this);
        getLoaderManager().initLoader(LOADER_TYPE_CONTACT, null, this);

        initializeAndGetInformation();
        setupView();
        //setupSpinnerOffer();
        //setupSpinnerContactType();

        //getResult();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public static ResultadoFragment newInstance(ClienteVisita clienteVisita) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_VISIT, clienteVisita);
        ResultadoFragment fragment = new ResultadoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // endregion



    // region callback

    @Override
    public void onClick() {
        Log.d(TAG, "onClick: ");


        if (_location == null) {
            Toast.makeText(getContext(), getString(R.string.resultado_error_location),Toast.LENGTH_SHORT).show();
            createLocationRequest();
            createLocationCallback();
            enableConfigurationlocation();
            return;
        }


        // TODO 1 interesado
        if ( ((Resultado)_spinnerResult.getSelectedItem()).getValor() == AgendaComercialDbHelper.CODIGO_INTERESADO ) {

            Calendar calendar = Calendar.getInstance();
            ResultadoVisita resultadoVisita = new ResultadoVisita();
            int idClient = _clienteVisita == null ? 0 : _clienteVisita.getIdCliente();
            int idUser = AgendaComercialPreferences.getIdUser(getContext());


            // TODO DINAMICO idCliente, idUsuario cambiar al dinamico

            if (idClient == 0) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_obtener_id_cliente), Toast.LENGTH_SHORT).show();
                return;
            }

            if (idUser == 0) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_obtener_id_usuario), Toast.LENGTH_SHORT).show();
                return;
            }

            if (_spinnerOffer.getSelectedItem() == null) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_ofreciste), Toast.LENGTH_SHORT).show();
                return;
            }

            if ( _spinnerContactType.getSelectedItem() == null ) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_tipo_contacto), Toast.LENGTH_SHORT).show();
                return;
            }


            if (_year == 0 && _month == 0 && _dayOfMonth == 0) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_fecha), Toast.LENGTH_SHORT).show();
                return;
            }

            if (_spinnerResult.getSelectedItem() == null) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_resultado), Toast.LENGTH_SHORT).show();
                return;
            }


            calendar.set(_year, _month - 1, _dayOfMonth);

            resultadoVisita.setIdCliente(idClient);
            resultadoVisita.setVisita(calendar.getTime());
            resultadoVisita.setIdUsuario(idUser);
            resultadoVisita.setTipo(AgendaComercialDbHelper.TYPE_PROGRAMAR);
            resultadoVisita.setIdResultado( ((Resultado)_spinnerResult.getSelectedItem()).getValor() );

            resultadoVisita.setTipoContacto( ((TipoContacto)_spinnerContactType.getSelectedItem()).getValor() );


            if ( ((TipoContacto)_spinnerContactType.getSelectedItem()).getValor() == 2 ) {
                resultadoVisita.setLatitud(0.0);
                resultadoVisita.setLongitud(0.0);
            } else {
                resultadoVisita.setLatitud(_location.getLatitude());
                resultadoVisita.setLongitud(_location.getLongitude());
            }



            insertResultVisit(resultadoVisita);

            return;
        }


        // TODO 2 venta cerrada
        if ( ((Resultado)_spinnerResult.getSelectedItem()).getValor() == AgendaComercialDbHelper.CODIGO_VENTA_CERRADA ) {


            int idClient = _clienteVisita == null ? 0 : _clienteVisita.getIdCliente();
            int idUser = AgendaComercialPreferences.getIdUser(getContext());

            if (idClient == 0) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_obtener_id_cliente), Toast.LENGTH_SHORT).show();
                return;
            }

            if (idUser == 0) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_obtener_id_usuario), Toast.LENGTH_SHORT).show();
                return;
            }

            if (_textinputedittextAmount.getText().toString().trim().length() == 0) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_monto), Toast.LENGTH_SHORT).show();
                return;
            }

            if ( Double.valueOf(_textinputedittextAmount.getText().toString().trim()) <= 0 ) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_monto), Toast.LENGTH_SHORT).show();
                return;
            }


            if (_spinnerResult.getSelectedItem() == null) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_resultado), Toast.LENGTH_SHORT).show();
                return;
            }

            if (_spinnerOffer.getSelectedItem() == null) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_ofreciste), Toast.LENGTH_SHORT).show();
                return;
            }

            if (_spinnerContactType.getSelectedItem() == null) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_tipo_contacto), Toast.LENGTH_SHORT).show();
                return;
            }


            ResultadoVisita resultadoVisita = new ResultadoVisita();
            // TODO DINAMICO idCliente, idUsuario
            resultadoVisita.setIdCliente(idClient);
            resultadoVisita.setIdOferta( ((Oferta)_spinnerOfferResult.getSelectedItem()).getIdCliente());
            resultadoVisita.setIdUsuario(idUser);
            resultadoVisita.setMonto(Double.valueOf(_textinputedittextAmount.getText().toString()));
            resultadoVisita.setIdResultado( ((Resultado)_spinnerResult.getSelectedItem()).getValor() );
            resultadoVisita.setIdProducto( ((Producto)_spinnerOffer.getSelectedItem()).getValor() );
            resultadoVisita.setComentario("");
            // TODO PREGUNTAR tipocredito, creditoDestinatario
            resultadoVisita.setTipoCredito(0);
            resultadoVisita.setDestinoCredito(0);

            resultadoVisita.setTipoContacto( ((TipoContacto)_spinnerContactType.getSelectedItem()).getValor() );

            resultadoVisita.setVisita(new Date());
            resultadoVisita.setTipo(AgendaComercialDbHelper.TYPE_RESULTADO);
            resultadoVisita.setSincronizar(AgendaComercialDbHelper.FLAG_NOT_SYNCRONIZED);


            if ( ( (TipoContacto)_spinnerContactType.getSelectedItem()).getValor() == 2 ) {
                resultadoVisita.setLongitud(0.0);
                resultadoVisita.setLatitud(0.0);
            } else {
                resultadoVisita.setLongitud(_location.getLongitude());
                resultadoVisita.setLatitud(_location.getLatitude());
            }


            insertResultVisit(resultadoVisita);

            return;
        }


        // TODO las demos resultados
        if ( ((Resultado) _spinnerResult.getSelectedItem()).getValor() != AgendaComercialDbHelper.CODIGO_VENTA_CERRADA &&
                ((Resultado) _spinnerResult.getSelectedItem()).getValor() != AgendaComercialDbHelper.CODIGO_INTERESADO   ) {


            int idClient = _clienteVisita == null ? 0 : _clienteVisita.getIdCliente();
            int idUser = AgendaComercialPreferences.getIdUser(getContext());

            if (idClient == 0) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_obtener_id_cliente), Toast.LENGTH_SHORT).show();
                return;
            }

            if (idUser == 0) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_obtener_id_usuario), Toast.LENGTH_SHORT).show();
                return;
            }

            if (_spinnerResult.getSelectedItem() == null) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_resultado), Toast.LENGTH_SHORT).show();
                return;
            }

            if (_spinnerOffer.getSelectedItem() == null) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_ofreciste), Toast.LENGTH_SHORT).show();
                return;
            }

            if (_spinnerContactType.getSelectedItem() == null) {
                Toast.makeText(getContext(), getString(R.string.resultado_error_tipo_contacto), Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO PREGUNTAR valores por defecto

            ResultadoVisita resultadoVisita = new ResultadoVisita();
            // TODO DINAMICO idCliente, idUsuario
            resultadoVisita.setIdCliente(idClient);
            resultadoVisita.setIdOferta( 0 );
            resultadoVisita.setIdUsuario(idUser);
            resultadoVisita.setMonto(0);
            resultadoVisita.setIdResultado( ((Resultado)_spinnerResult.getSelectedItem()).getValor() );
            resultadoVisita.setIdProducto( ((Producto)_spinnerOffer.getSelectedItem()).getValor() );
            resultadoVisita.setComentario("");

            // TODO PREGUNTAR tipocredito, creditoDestinatario
            resultadoVisita.setTipoCredito(0);
            resultadoVisita.setDestinoCredito(0);

            resultadoVisita.setTipoContacto( ((TipoContacto)_spinnerContactType.getSelectedItem()).getValor() );

            resultadoVisita.setVisita(new Date());
            resultadoVisita.setTipo( AgendaComercialDbHelper.TYPE_RESULTADO );
            resultadoVisita.setSincronizar(AgendaComercialDbHelper.FLAG_NOT_SYNCRONIZED);


            if (((TipoContacto) _spinnerContactType.getSelectedItem()).getValor() == 2) {
                resultadoVisita.setLongitud(0.0);
                resultadoVisita.setLatitud(0.0);
            } else {
                resultadoVisita.setLongitud(_location.getLongitude());
                resultadoVisita.setLatitud(_location.getLatitude());
            }

            insertResultVisit(resultadoVisita);

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext()) {
            @Override
            public Cursor loadInBackground() {

                Cursor cursor;

                switch (id) {
                    case LOADER_PRODUCTS_OFFERED:
                        cursor = _agendaComercialDbHelper.getAllProduct();
                        break;
                    case LOADER_TYPE_CONTACT:
                        cursor = _agendaComercialDbHelper.getAllContactType();
                        break;
                    case LOADER_RESULTS_VISITS:
                        if (_typeContact.getValor() == Constantes.TYPE_CONTACT_FACE_FACE) {
                            cursor = _agendaComercialDbHelper.getAllResultVisits(Constantes.TYPE_CONTACT_FACE_FACE);
                        } else {
                            cursor = _agendaComercialDbHelper.getAllResultVisits(Constantes.TYPE_CONTACT_CALL);
                        }
                        break;
                    default:
                        cursor = _agendaComercialDbHelper.getAllOffers();
                        break;
                }

                return cursor;

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {

            case LOADER_PRODUCTS_OFFERED:
                List<Producto> productoList = _agendaComercialDbHelper.listProduct(data);
                setupSpinnerOfferProduct(productoList);
                break;
            case LOADER_TYPE_CONTACT:
                List<TipoContacto> tipoContactoList = _agendaComercialDbHelper.listContactType(data);
                setupSpinnerContactType(tipoContactoList);
                break;
            case LOADER_RESULTS_VISITS:
                List<Resultado> resultadoList = _agendaComercialDbHelper.listResultVisits(data);
                setupSpinnerResult(resultadoList);
                getLoaderManager().destroyLoader(LOADER_RESULTS_VISITS);
                break;
            default:
                List<Oferta> ofertaList = _agendaComercialDbHelper.listOffers(data);
                setupSpinnerOffer(ofertaList);
                getLoaderManager().destroyLoader(LOADER_OFFERS);
                break;

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    // endregion


    // region ui controller

    private void initializeAndGetInformation() {

        try {
            _clienteVisita = getArguments().getParcelable(EXTRA_VISIT);
        } catch (Exception e) { }

    }

    private void setupView() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        _year = calendar.get(Calendar.YEAR);
        _month = calendar.get(Calendar.MONTH);
        _dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        _textDateVisitContent.setText(simpleDateFormat.format(calendar.getTime()));

    }

    private void setupSpinnerOfferProduct(List<Producto> offerList) {

        ArrayAdapter<Producto> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, offerList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerOffer.setAdapter(adapter);

    }

    private void setupSpinnerContactType(List<TipoContacto> contactTypeList) {

        ArrayAdapter<TipoContacto> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, contactTypeList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerContactType.setAdapter(adapter);


        _spinnerContactType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _typeContact = contactTypeList.get(position);
                searchLocalResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    private void setupSpinnerResult(List<Resultado> resultList) {

        ArrayAdapter<Resultado> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, resultList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerResult.setAdapter(adapter);


        _spinnerResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validationResult(resultList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


    }

    private void setupSpinnerOffer(List<Oferta> offerList) {
        ArrayAdapter<Oferta> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, offerList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerOfferResult.setAdapter(adapter);
    }


    private void validationResult(Resultado resultado) {

        _textofferResult.setVisibility(View.GONE);
        _spinnerOfferResult.setVisibility(View.GONE);
        _textAmount.setVisibility(View.GONE);
        _textinputlayoutAmount.setVisibility(View.GONE);

        _textNewDateVisit.setVisibility(View.GONE);
        _textDateVisitContent.setVisibility(View.GONE);


        if (resultado.getValor() == 1) {

            _textofferResult.setVisibility(View.VISIBLE);
            _spinnerOfferResult.setVisibility(View.VISIBLE);
            _textAmount.setVisibility(View.VISIBLE);
            _textinputlayoutAmount.setVisibility(View.VISIBLE);

            searchLocalOffer();

        } else if (resultado.getValor() == 4) {

            _textNewDateVisit.setVisibility(View.VISIBLE);
            _textDateVisitContent.setVisibility(View.VISIBLE);

        }

    }


    private void showCalendar() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
//                    _year = calendar.get(Calendar.YEAR);
//////                    _month = calendar.get(Calendar.MONTH);
//////                    _dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    _year = year;
                    _month = (month + 1) % 12;
                    if(_month == 0){
                        _month = 12;
                    }
                    _dayOfMonth = dayOfMonth;
                    _textDateVisitContent.setText( _dayOfMonth + "/" + _month + "/" + _year);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) );
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        Log.d(TAG, "showCalendar: " + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DAY_OF_MONTH));

    }

    // endregion


    // region source local

    private void searchLocalResults() {
        getLoaderManager().initLoader(LOADER_RESULTS_VISITS, null, this);
    }

    private void searchLocalOffer() {
        getLoaderManager().initLoader(LOADER_OFFERS, null, this);
    }

    // endregion

    // region network

    private void insertResultVisit(ResultadoVisita resultadoVisita) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                return _agendaComercialDbHelper.insertResultVisitCustomer(resultadoVisita, AgendaComercialDbHelper.FLAG_CON_RESULTADO);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    Toast.makeText(getContext(), getString(R.string.resultado_msg_insertar_resultado_visita_exitoso), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), getString(R.string.resultado_error_insertar_resultado_visita), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }

    // endregion


    // region location


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
                    Log.d(TAG, "onLocationResult - latitud: " + _location.getLatitude() +
                            " - longitud: " + _location.getLongitude());

                } else {
                    Log.d(TAG, "onLocationResult: no entro" );
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
                            resolvable.startResolutionForResult( (RegistroResultadoActivity)getContext() ,
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


    // endregion


}
