package pe.com.cmacica.flujocredito.ViewModel.AgendaComercial;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Agencia;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteReferido;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.DivisionTerriorial;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Producto;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialPreferences;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;

public class RegistroReferidoActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "RegistroReferidoActivit";

    public static final int LOADER_DEPARTAMENTS = 1;
    public static final int LOADER_PROVINCES = 2;
    public static final int LOADER_DISTRICTS = 3;
    public static final int LOADER_AGENCIES = 4;
    public static final int LOADER_PRODUCTS = 5;

    public static final int FLAG_DEPARTMENT = 1;
    public static final int FLAG_PROVINCE = 2;
    public static final int FLAG_DISTRICT = 3;



    private Toolbar _toolbar;
    private TextInputEditText _textinputedittextName;
    private TextInputEditText _textinputedittextAddress;
    private TextInputEditText _textinputedittextPhone;
    private TextInputEditText _textinputedittextDNI;
    private Spinner _spinnerDepartament;
    private Spinner _spinnerProvince;
    private Spinner _spinnerDistrict;
    private Spinner _spinnerAgency;
    private Spinner _spinnerProduct;
    private CheckBox _checkboxResult;
    private Button _buttonRegisterRefferedClient;
    private Button _buttonSearchPerson;

    private AgendaComercialDbHelper _agendaComercialDbHelper;
    private DivisionTerriorial _ubigeoModelDepartament;
    private DivisionTerriorial _ubigeoModelProvince;
    private DivisionTerriorial _ubigeoModelDistrict;
    private ProgressDialog _progressDialog;

    private String _departmentPersonReniec = "";
    private String _provincePersonReniec = "";
    private String _districtPersonReniec = "";

    private int _validarReferido = -1;



    // region lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_referido);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _textinputedittextName = (TextInputEditText) findViewById(R.id.textinputedittextName);
        _textinputedittextAddress = (TextInputEditText) findViewById(R.id.textinputedittextAddress);
        _textinputedittextPhone = (TextInputEditText) findViewById(R.id.textinputedittextPhone);
        _textinputedittextDNI = (TextInputEditText) findViewById(R.id.textinputedittextDNI);
        _spinnerDepartament = (Spinner) findViewById(R.id.spinnerDepartament);
        _spinnerProvince = (Spinner) findViewById(R.id.spinnerProvince);
        _spinnerDistrict = (Spinner) findViewById(R.id.spinnerDistrict);
        _spinnerAgency = (Spinner) findViewById(R.id.spinnerAgency);
        _spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        _checkboxResult = (CheckBox) findViewById(R.id.checkboxResult);
        _buttonRegisterRefferedClient = (Button) findViewById(R.id.buttonRegisterRefferedClient);
        _buttonSearchPerson = (Button) findViewById(R.id.buttonSearchPerson);

        _agendaComercialDbHelper = new AgendaComercialDbHelper(this);

        listenerView();
        initToolbar();
        enableRegistry(false);

        validateIdUser();
        searchLocalDepartments();
        searchServerAgencies();
        searchServerProducts();


    }
    // endregion


    // region ui controller

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.registro_referido_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void listenerView() {

        _buttonSearchPerson.setOnClickListener( v -> {

            if ( _textinputedittextDNI.getText().toString().trim().length() < 8 ) {
                Toast.makeText(this, getString(R.string.registro_referido_msg_dni), Toast.LENGTH_SHORT).show();
                return;
            }

          searchServerReniec( _textinputedittextDNI.getText().toString() );

        });

        _spinnerDepartament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _ubigeoModelDepartament = (DivisionTerriorial) parent.getSelectedItem();
                searchLocalProvinces();
                setupSpinnerProvinces(new ArrayList<>());
                setupSpinnerDistricts(new ArrayList<>());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        _spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _ubigeoModelProvince = (DivisionTerriorial) parent.getSelectedItem();
                searchLocalDistricts();
                setupSpinnerDistricts(new ArrayList<>());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        _spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _ubigeoModelDistrict = (DivisionTerriorial) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        _checkboxResult.setOnCheckedChangeListener( (buttonView, isChecked) -> {

            if (isChecked) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.registro_referido_dialog_title))
                        .setMessage(getString(R.string.registro_referido_dialog_content))
                        .setPositiveButton(R.string.registro_referido_dialog_action_si, (dialog, which) -> { _checkboxResult.setChecked(true); } )
                        .setNegativeButton(R.string.registro_referido_dialog_action_no, (dialog, which) -> { _checkboxResult.setChecked(false); } )
                        .create()
                        .show();
            }

        } );


        _buttonRegisterRefferedClient.setOnClickListener( v -> registerReferredClient() );


    }


    private void setupSpinnerDepartments(List<DivisionTerriorial> departmentsList) {
        setupSpinner(departmentsList, _spinnerDepartament, FLAG_DEPARTMENT);
    }

    private void setupSpinnerProvinces(List<DivisionTerriorial> provincesList) {
        Log.d(TAG, "setupSpinnerProvinces: onLoadFinished123" + provincesList.size());
        setupSpinner(provincesList, _spinnerProvince, FLAG_PROVINCE);
    }

    private void setupSpinnerDistricts(List<DivisionTerriorial> districtsList) {
        setupSpinner(districtsList, _spinnerDistrict, FLAG_DISTRICT);
    }

    private void setupSpinnerAgencies(List<Agencia> agenciesList) {
        ArrayAdapter<Agencia> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, agenciesList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerAgency.setAdapter(adapter);
    }

    private void setupSpinnerProducts(List<Producto> productsList) {
        ArrayAdapter<Producto> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, productsList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerProduct.setAdapter(adapter);
    }

    private void setupSpinner(List<DivisionTerriorial> dataList, Spinner spinner, int flag) {
        ArrayAdapter<DivisionTerriorial> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, dataList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        switch (flag) {
            case FLAG_DEPARTMENT:
                if (!_departmentPersonReniec.isEmpty()) {
                    spinner.setSelection(getItemSpinner(spinner,_departmentPersonReniec));
                }
                break;
            case FLAG_PROVINCE:
                if (!_provincePersonReniec.isEmpty()) {
                    spinner.setSelection(getItemSpinner(spinner, _provincePersonReniec));
                }
                break;
            case FLAG_DISTRICT:
                if (!_districtPersonReniec.isEmpty()) {
                    spinner.setSelection(getItemSpinner(spinner, _districtPersonReniec));
                }
                break;
        }


    }

    private int getItemSpinner(Spinner spinner, String item) {

        int position = 0;

        for (int i=0; i<spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().trim().equals(item)) {
                position = i;
            }
        }

        return position;

    }

    private void enableRegistry(boolean enable) {

        _textinputedittextDNI.setEnabled(!enable);
        _buttonSearchPerson.setEnabled(!enable);

        _textinputedittextName.setEnabled(enable);
        _textinputedittextAddress.setEnabled(enable);
        _textinputedittextPhone.setEnabled(enable);
        _spinnerDepartament.setEnabled(enable);
        _spinnerProvince.setEnabled(enable);
        _spinnerDistrict.setEnabled(enable);
        _spinnerAgency.setEnabled(enable);
        _spinnerProduct.setEnabled(enable);
        _checkboxResult.setEnabled(enable);
        _buttonRegisterRefferedClient.setEnabled(enable);

    }

    private void visualizeDataReniec(JSONObject jsonPerson) {

        try {

            String name = jsonPerson.getString("apellidoPaterno")+" "
                    + jsonPerson.getString("apellidoMaterno")+" " + jsonPerson.getString("nombres");
            String address = jsonPerson.getString("direccion");
            String phone = jsonPerson.getString("telefono") .equals("null") ? "" : jsonPerson.getString("telefono");
            String department = jsonPerson.getString("domicDptoDes").equals("null") ? "" : jsonPerson.getString("domicDptoDes");
            String province = jsonPerson.getString("domicProvDes").equals("null") ? "" : jsonPerson.getString("domicProvDes");
            String district = jsonPerson.getString("domicDistDes").equals("null") ? "" : jsonPerson.getString("domicDistDes");

            _textinputedittextName.setText(name);
            _textinputedittextAddress.setText(address);
            _textinputedittextPhone.setText(phone);
            _departmentPersonReniec = department;
            _provincePersonReniec = province;
            _districtPersonReniec = district;

            searchLocalDepartments();

        } catch (Exception e) { }

    }


    // endregion


    // region validation

    private void validateIdUser() {

        int idUser = AgendaComercialPreferences.getIdUser(getApplicationContext());

        if (idUser == 0) {
            searchServerIdUsuario();
        }

    }

    private void registerReferredClient() {

        ClienteReferido clienteReferido = new ClienteReferido();

        if (_validarReferido == -1) {
            Toast.makeText(this, "No se pudo validar el DNI referido, porfavor buscalo nuevamente", Toast.LENGTH_SHORT).show();
            return;
        } else if (_validarReferido == 1){
            Toast.makeText(this, "El DNI ingresado ya esta registrado en la campaña actual", Toast.LENGTH_SHORT).show();
            return;
        }
        if (AgendaComercialPreferences.getIdUser(getApplicationContext()) == 0) {
            Toast.makeText(this, getString(R.string.registro_referido_error_id_usuario), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _textinputedittextDNI.getText().toString().trim().length() < 8 ){
            Toast.makeText(this, getString(R.string.registro_referido_msg_dni), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _textinputedittextName.getText().toString().trim().length() == 0 ) {
            Toast.makeText(this, getString(R.string.registro_referido_msg_nombre), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _textinputedittextAddress.getText().toString().trim().length() == 0 ) {
            Toast.makeText(this, getString(R.string.registro_referido_msg_direccion), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _spinnerDepartament.getSelectedItem() == null ) {
            Toast.makeText(this, getString(R.string.registro_referido_msg_departamento), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _spinnerProvince.getSelectedItem() == null ) {
            Toast.makeText(this, getString(R.string.registro_referido_msg_provincia), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _spinnerDistrict.getSelectedItem() == null ) {
            Toast.makeText(this, getString(R.string.registro_referido_msg_distrito), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _spinnerAgency.getSelectedItem() == null ) {
            Toast.makeText(this, getString(R.string.registro_referido_msg_agencia), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _spinnerProduct.getSelectedItem() == null ) {
            Toast.makeText(this, getString(R.string.registro_referido_msg_producto), Toast.LENGTH_SHORT).show();
            return;
        }

        clienteReferido.setDocumento(_textinputedittextDNI.getText().toString());
        clienteReferido.setNombres(_textinputedittextName.getText().toString());
        clienteReferido.setDireccion(_textinputedittextAddress.getText().toString());
        clienteReferido.setDepartamento( ((DivisionTerriorial)_spinnerDepartament.getSelectedItem()).getCodigoUbigeoRENIEC() );
        clienteReferido.setProvincia( ((DivisionTerriorial)_spinnerProvince.getSelectedItem()).getCodigoUbigeoRENIEC() );
        clienteReferido.setDistrito( ((DivisionTerriorial)_spinnerDistrict.getSelectedItem()).getCodigoUbigeoRENIEC() );
        // TODO 1 consultar si es de la TB_Agencias (idAgencia)
        clienteReferido.setIdAgencia( ((Agencia)_spinnerAgency.getSelectedItem()).getIdAgencia() );
        // TODO 2 servicio de devolver idUsuario
        clienteReferido.setIdUsuario( AgendaComercialPreferences.getIdUser(getApplicationContext()) );
        clienteReferido.setIdProducto( ((Producto)_spinnerProduct.getSelectedItem()).getValor() );
        clienteReferido.setTelefono(_textinputedittextPhone.getText().toString());
        if ( _checkboxResult.isChecked() ) {
            clienteReferido.setEstadoResultado(1);
        } else {
            clienteReferido.setEstadoResultado(0);
        }

        insertReferral(clienteReferido);

    }

    private void insertReferral(ClienteReferido clienteReferido) {

        AsyncTask task = new AsyncTask<Object, Void, Integer>() {
            @Override
            protected Integer doInBackground(Object... objects) {
                return _agendaComercialDbHelper.insertReferred(clienteReferido);
            }

            @Override
            protected void onPostExecute(Integer aBoolean) {

                switch (aBoolean) {
                    case AgendaComercialDbHelper.FLAG_INSERTAR_REFERIDO_EXITOSO:
                        Toast.makeText(RegistroReferidoActivity.this, getString(R.string.registro_referido_msg_insertar_referido_exitosamente), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case AgendaComercialDbHelper.FLAG_INSERTAR_REFERIDO_ERROR_EXISTENTE:
                        Toast.makeText(RegistroReferidoActivity.this, getString(R.string.registro_referido_error_insertar_referido_existente), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        Toast.makeText(RegistroReferidoActivity.this, getString(R.string.registro_referido_error_insertar_referido_exitosamente), Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };
        task.execute();

    }

    // endregion


    // region callback

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this) {

            @Override
            public Cursor loadInBackground() {

                Cursor cursor;

                switch (id) {
                    case LOADER_DEPARTAMENTS:
                        cursor = _agendaComercialDbHelper.getAllDepartments();
                        break;
                    case LOADER_PROVINCES:
                        cursor = _agendaComercialDbHelper.getProvince(_ubigeoModelDepartament.getCodigoUbigeoRENIEC());
                        break;
                    case LOADER_DISTRICTS:
                        cursor = _agendaComercialDbHelper.getDistrict(_ubigeoModelProvince.getCodigoUbigeoRENIEC());
                        break;
                    case LOADER_AGENCIES:
                        cursor = _agendaComercialDbHelper.getAllAgencies();
                        break;
                    default:
                        cursor = _agendaComercialDbHelper.getAllProduct();
                        break;
                }

                return cursor;

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {

            case LOADER_DEPARTAMENTS:

                if (data.getCount() > 0){
                    setupSpinnerDepartments(_agendaComercialDbHelper.listDepartments(data));
                } else {
                    searchServerDepartments();
                }

                getSupportLoaderManager().destroyLoader(LOADER_DEPARTAMENTS);
                break;
            case LOADER_PROVINCES:

                if (data.getCount() > 0) {
                    setupSpinnerProvinces(_agendaComercialDbHelper.listProvinces(data));
                } else {
                    searchServerProvinces(_ubigeoModelDepartament);
                }
                getSupportLoaderManager().destroyLoader(LOADER_PROVINCES);
                break;
            case LOADER_DISTRICTS:

                if (data.getCount() > 0) {
                    setupSpinnerDistricts(_agendaComercialDbHelper.listDistricts(data));
                } else {
                    searchServerDistricts(_ubigeoModelProvince);
                }
                getSupportLoaderManager().destroyLoader(LOADER_DISTRICTS);
                break;

            case LOADER_AGENCIES:

                if (data.getCount() > 0) {
                    setupSpinnerAgencies(_agendaComercialDbHelper.listAgencies(data));
                } else {
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_agencias), Toast.LENGTH_SHORT).show();
                }

                break;

            default:

                if (data.getCount() > 0) {
                    setupSpinnerProducts(_agendaComercialDbHelper.listProduct(data));
                } else {
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_productos), Toast.LENGTH_SHORT).show();
                }

                break;

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {  }


    // endregion


    // region sqlite

    private void searchLocalDepartments() {
        getSupportLoaderManager().initLoader(LOADER_DEPARTAMENTS, null, this);
    }

    private void searchLocalProvinces() {
        getSupportLoaderManager().initLoader(LOADER_PROVINCES, null, this);
    }

    private void searchLocalDistricts() {
        getSupportLoaderManager().initLoader(LOADER_DISTRICTS, null, this);
    }

    private void searchLocalAgencies() {
        getSupportLoaderManager().initLoader(LOADER_AGENCIES, null, this);
    }

    private void searchLocalProducts() {
        getSupportLoaderManager().initLoader(LOADER_PRODUCTS, null, this);
    }

    // endregion



    // region network

    private void searchServerDepartments() {

        String url = String.format(SrvCmacIca.GET_DEPARTAMENTOS_BDVENTAS);

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerDepartments(response),
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }
    private void responseServerDepartments(JSONObject response) {

        Log.d(TAG, "responseServerDepartments: " + response);
        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, getString(R.string.registro_referido_msg_departamentos_obtenidos_existosamente), Toast.LENGTH_SHORT).show();

                JSONArray jsonDepartments = response.getJSONArray("Data");

                if(jsonDepartments.length() == 0){
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_departamentos), Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "responseServerDepartments: insertar deparamentos");
                    insertarDepartments(jsonDepartments);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private void insertarDepartments(JSONArray jsonDepartaments) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                return _agendaComercialDbHelper.insertDepartments(jsonDepartaments);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    Log.d(TAG, "onPostExecute: insercion exitosa");
                    searchLocalDepartments();
                } else {
                    Toast.makeText(RegistroReferidoActivity.this, getString(R.string.registro_referido_error_insertar_departamentos), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }


    private void searchServerProvinces(DivisionTerriorial departament) {

        String url = String.format(SrvCmacIca.GET_PROVINCIAS_BDVENTAS, departament.getCodigoUbigeoRENIEC());

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerProvinces(response),
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }
    private void responseServerProvinces(JSONObject response) {

        Log.d(TAG, "responseServerProvinces: " + response);
        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, getString(R.string.registro_referido_msg_provincias_obtenidas_exitosamente), Toast.LENGTH_SHORT).show();

                JSONArray jsonProvinces = response.getJSONArray("Data");

                if(jsonProvinces.length() == 0){
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_provincias), Toast.LENGTH_LONG).show();
                } else {
                    insertarProvinces(jsonProvinces);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    private void insertarProvinces(JSONArray jsonProvinces) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                return _agendaComercialDbHelper.insertProvinces(jsonProvinces);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    searchLocalProvinces();
                } else {
                    Toast.makeText(RegistroReferidoActivity.this, getString(R.string.registro_referido_error_insertar_provincias), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }


    private void searchServerDistricts(DivisionTerriorial province) {

        String url = String.format(SrvCmacIca.GET_DISTRITOS_BDVENTAS, province.getCodigoUbigeoRENIEC());

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerDistricts(response),
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }
    private void responseServerDistricts(JSONObject response) {

        Log.d(TAG, "responseServerDistricts: " + response);
        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, getString(R.string.registro_referido_msg_distritos_obtenidos_exitosamente), Toast.LENGTH_SHORT).show();

                JSONArray jsonDistricts = response.getJSONArray("Data");

                if(jsonDistricts.length() == 0){
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_distritos), Toast.LENGTH_LONG).show();
                } else {
                    insertarDistricts(jsonDistricts);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private void insertarDistricts(JSONArray jsonDistricts) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteDistrict(_ubigeoModelProvince.getCodigoUbigeoRENIEC());
                return _agendaComercialDbHelper.insertDistricts(jsonDistricts);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    searchLocalDistricts();
                } else {
                    Toast.makeText(RegistroReferidoActivity.this, getString(R.string.registro_referido_error_insertar_distritos), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }


    private void searchServerAgencies() {

        String url = SrvCmacIca.GET_ALL_AGENCIAS_BI;

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerAgencies(response),
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );
    }
    private void responseServerAgencies(JSONObject response) {

        Log.d(TAG, "responseServerAgencies: " + response);
        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, getString(R.string.registro_referido_msg_agencias_obtenidos_exitosamente), Toast.LENGTH_SHORT).show();

                JSONArray jsonAgencies = response.getJSONArray("Data");

                if(jsonAgencies.length() == 0){
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_agencias), Toast.LENGTH_LONG).show();
                } else {
                    insertAgencies(jsonAgencies);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private void insertAgencies(JSONArray jsonAgencies) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteAllAgencies();
                return _agendaComercialDbHelper.insertAgencies(jsonAgencies);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    searchLocalAgencies();
                } else {
                    Toast.makeText(RegistroReferidoActivity.this, getString(R.string.registro_referido_error_insertar_distritos), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }


    private void searchServerProducts() {

        int idUser = AgendaComercialPreferences.getIdUser(getApplicationContext());

        String url = String.format(SrvCmacIca.GET_PRODUCTOS_OFRECIDOS_ANALISTA,  idUser) ;

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerProducts(response),
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }
    private void responseServerProducts(JSONObject response) {

        Log.d(TAG, "responseServerProducts: " + response.toString());
        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, getString(R.string.registro_referido_msg_productos_obtenidos_exitosamente), Toast.LENGTH_SHORT).show();

                JSONArray jsonProducts = response.getJSONArray("Data");
                jsonProducts.remove(1);
                jsonProducts.remove(1);
                if(jsonProducts.length() == 0){
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_productos), Toast.LENGTH_LONG).show();
                } else {
                    insertProducts(jsonProducts);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private void insertProducts(JSONArray jsonProducts) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteAllProduct();
                return _agendaComercialDbHelper.insertProduct(jsonProducts);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    searchLocalProducts();
                } else {
                    Toast.makeText(RegistroReferidoActivity.this, getString(R.string.registro_referido_error_insertar_distritos), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();


    }


    private void searchServerReniec(String doi) {


        String url = String.format(SrvCmacIca.GET_BUSQUEDA_PERSONA_RENIEC, doi);


        _progressDialog = ProgressDialog.show(this, getString(R.string.registro_referido_msg_esperar), getString(R.string.registro_referido_msg_obtener_persona_reniec));

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> {
                                    responseServerReniec(response);
                                    validateClientReferido(doi);
                                },


                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerReniec(JSONObject response) {

        Log.d(TAG, "responseServerReniec: " + response);
        _progressDialog.cancel();

        try{

            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, getString(R.string.registro_referido_msg_persona_obtenida_exitosamente), Toast.LENGTH_SHORT).show();

                JSONObject jsonPerson = response.getJSONObject("Data");

                if(jsonPerson.length() == 0){
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_persona), Toast.LENGTH_LONG).show();
                } else {
                    visualizeDataReniec(jsonPerson);
                    enableRegistry(true);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

            //enableRegistry(true);

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void validateClientReferido(String doi) {

        String url = String.format(SrvCmacIca.GET_VALIDAR_CLIENTE_REFERIDOS, doi);


//        _progressDialog = ProgressDialog.show(this, getString(R.string.registro_referido_msg_esperar), getString(R.string.registro_referido_msg_obtener_persona_reniec));

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseValidateClientReferido(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseValidateClientReferido(JSONObject response) {
        Log.d(TAG, "responseValidateClientReferido: " + response);
//        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                JSONArray jsonValidarCliente = response.getJSONArray("Data");
                    JSONObject jobj = jsonValidarCliente.getJSONObject(0);
                    Integer validarCliente = jobj.getInt("Tipo");
                    if(validarCliente == 1){
                        _validarReferido = 1;
                    } else {
                        _validarReferido = 0;
                    }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            _validarReferido = 0;
        }

    }

    private void searchServerIdUsuario() {

        String user = UPreferencias.ObtenerUserLogeo(getApplicationContext());

        // TODO DINAMICO user
		
        String url = String.format(SrvCmacIca.GET_ID_USUARIO_ANALISTA, user);


        _progressDialog = ProgressDialog.show(this, getString(R.string.registro_referido_msg_esperar), getString(R.string.registro_referido_msg_obtener_id_usuario));

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerIdUser(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerIdUser(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, getString(R.string.registro_referido_msg_id_usuario_obtenido_exitosamente), Toast.LENGTH_SHORT).show();

                JSONArray data = response.getJSONArray("Data");
                JSONObject jsonUser = data.getJSONObject(0);

                if(jsonUser.length() == 0){
                    Toast.makeText(this, getString(R.string.registro_referido_error_obtener_id_usuario), Toast.LENGTH_LONG).show();
                    finish();
                } else {

                    AgendaComercialPreferences.setIdUser(
                            getApplicationContext(),
                            jsonUser.getInt("IdUsuario")
                    );
                    enableRegistry(false);

                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
                finish();
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

    }
    // endregion

}
