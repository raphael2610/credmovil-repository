package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import android.os.Bundle;

import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.LocalizacionModel;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.Model.carteraanalista.TipoDireccion;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.Common;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.GeoLocalizacion;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaContract;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaPreferences;
import pe.com.cmacica.flujocredito.ViewModel.ActividadPrincipal;

public class RegistrarUsuarioActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "RegistrarUsuarioActivit";

    public static final int LOADER_SEARCH_CUSTOMER = 0;
    public static final int LOADER_TYPE_ADDRESS = 1;

    private ProgressDialog _progressDialog;
    private Toolbar _toolbar;
    private Spinner _spinner;
    private Button _buttonSave;
    private Button _buttonSearchCustomer;
    private TextInputEditText _textinputedittextName;
    private TextInputEditText _textinputedittextPhone;
    private TextInputEditText _textinputedittextReference;
    private TextInputEditText _textinputedittextDNI;
    private TextInputLayout _textinputlayoutDNI;
    private RadioGroup _radiogroupOptions;
    private RadioButton _radiobuttonDNI;
    private RadioButton _radiobuttonName;

    private CarteraAnalistaDbHelper  _carteraAnalistaDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _buttonSave = (Button) findViewById(R.id.buttonSave);
        _buttonSearchCustomer = (Button) findViewById(R.id.buttonSearchCustomer);
        _textinputedittextName = (TextInputEditText) findViewById(R.id.textinputedittextName);
        _textinputedittextPhone = (TextInputEditText) findViewById(R.id.textinputedittextPhone);
        _textinputedittextDNI = (TextInputEditText) findViewById(R.id.textinputedittextDNI);
        _radiogroupOptions = (RadioGroup) findViewById(R.id.radiogroupOptions);
        _radiobuttonDNI = (RadioButton) findViewById(R.id.radiobuttonDNI);
        _radiobuttonName = (RadioButton) findViewById(R.id.radiobuttonName);

        _carteraAnalistaDbHelper = new CarteraAnalistaDbHelper(this);

        listenerView();

        initToolbar();
        enableDisableField(false);
    }



    private void listenerView() {
        _textinputedittextDNI.setOnEditorActionListener( (v, actionId, event) -> {searchCustomer(); return false;} );
        _textinputedittextPhone.setOnEditorActionListener((v, actionId, event) -> {saveCustomer(); return false; } );
        _buttonSearchCustomer.setOnClickListener(v -> searchCustomer());
        _buttonSave.setOnClickListener(v -> saveCustomer());
        _radiogroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radiobuttonName) {
                navigateToBusquedaNombreApellido();
                finish();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader = new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                return _carteraAnalistaDbHelper.searchCustomer(_textinputedittextDNI.getText().toString());
            }
        };

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() >= 1) {
            _progressDialog.cancel();
            showDialog();
        } else {
            // TODO 2 Pedir a la base de datos CMAICA si lo tiene registrado
            _progressDialog.setMessage(getString(R.string.registrar_usuario_msg_buscando_clientes_db_cmacica));
            searchDBCmaica();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }


    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.registrar_usuario_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void searchCustomer() {

        Common.hideKeyboard(this, _textinputedittextDNI);
        CarteraAnalistaPreferences.limpiarFiltros(getApplicationContext());

        _progressDialog = ProgressDialog.show(this,getString(R.string.registrar_usuario_msg_esperar),getString(R.string.registrar_usuario_msg_validacion_campos));


        if (_textinputedittextDNI.getText().length() < 8 )  {
            _progressDialog.cancel();
            Toast.makeText(this, getString(R.string.anade_direccion_msg_dni), Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO 1 Buscar en la base de datos local y comprobar que no haya usuario registrado
        _progressDialog.setMessage(getString(R.string.registrar_usuario_msg_buscando_clientes_locales));
        getSupportLoaderManager().initLoader(LOADER_SEARCH_CUSTOMER, null, this);



    }


    private void searchDBCmaica() {

        String user = UPreferencias.ObtenerUserLogeo(this);

        String url = String.format(SrvCmacIca.GET_CLIENTE, _textinputedittextDNI.getText().toString(), user);

        VolleySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServer(response),
                                error -> {
                                    // TODO 3 Habilitar los campos para que registro al cliente
                                    _progressDialog.cancel();
                                    enableDisableField(true);
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        )
                );

    }


    private void responseServer(JSONObject response) {

        try {
            if(response.getBoolean("IsCorrect")){
                JSONArray jsonClientes = response.getJSONArray("Data");

                if(jsonClientes.length() == 0){
                    _progressDialog.cancel();
                    Toast.makeText(this, getString(R.string.registrar_usuario_msg_cliente_no_encontrado), Toast.LENGTH_LONG).show();
                    enableDisableField(true);
                } else {
                    insertarCustomers(jsonClientes);
                }
            } else {
                _progressDialog.cancel();
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            _progressDialog.cancel();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void insertarCustomers(JSONArray jsonCustomers) {

        _progressDialog.setMessage(getString(R.string.registrar_usuario_msg_insertando_usuario_cmaica));

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                return _carteraAnalistaDbHelper.insertCustomer(jsonCustomers);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                _progressDialog.cancel();
                if (aBoolean) {
                    CarteraAnalistaPreferences.setValidationGetCustomer(getApplicationContext(), false);
                    Toast.makeText(RegistrarUsuarioActivity.this, getString(R.string.registrar_usuario_msg_registro_exitoso), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegistrarUsuarioActivity.this, getString(R.string.registrar_usuario_error_registro), Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.execute();

    }

    private void saveCustomer() {

        if (_textinputedittextName.getText().length() == 0) {
            Toast.makeText(this, getString(R.string.anade_direccion_msg_name_customer), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextPhone.getText().length() < 9) {
            Toast.makeText(this, getString(R.string.anade_direccion_msg_phone_customer), Toast.LENGTH_SHORT).show();
            return;
        }


        Cliente cliente = new Cliente();
        cliente.setDoi(_textinputedittextDNI.getText().toString());
        cliente.setNombre(_textinputedittextName.getText().toString().toUpperCase());
        cliente.setTelefono(_textinputedittextPhone.getText().toString());

        //cliente.setReferencia(_textinputedittextReference.getText().toString());
        //cliente.setIdTipoDireccion(tipoDireccion.getId());
        //cliente.setLatitud(_location.getLatitude());
        //cliente.setLongitud(_location.getLongitude());
        cliente.setIdTipoDireccion(0);
        cliente.setSincronizar(CarteraAnalistaDbHelper.FLAG_NOT_SYNCRONIZED);

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... voids) {
                return _carteraAnalistaDbHelper.insertCustomer(cliente, CarteraAnalistaDbHelper.FLAG_INSERTADO);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    //setFlagSyncronization(CarteraAnalistaDbHelper.FLAG_NOT_SYNCRONIZED);
                    CarteraAnalistaPreferences.setValidationGetCustomer(getApplicationContext(), false);
                    Toast.makeText(RegistrarUsuarioActivity.this, getString(R.string.registrar_usuario_msg_registro_exitoso), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(RegistrarUsuarioActivity.this, getString(R.string.registrar_usuario_error_registro), Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.execute();

    }


    private void enableDisableField(boolean enable) {

        _textinputedittextDNI.setEnabled(!enable);
        _buttonSearchCustomer.setEnabled(!enable);
        _textinputedittextName.setEnabled(enable);
        _textinputedittextPhone.setEnabled(enable);
        _buttonSave.setEnabled(enable);
        if (!enable) {
            _buttonSave.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.TextSecondary));
        } else {
            _buttonSave.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.registrar_usuario_dialog_title))
                .setMessage(getString(R.string.registrar_usuario_dialog_content))
                .setPositiveButton(R.string.registrar_usuario_dialog_action_aceptar, (dialog, which) -> onBackPressed())
                .setOnDismissListener(dialog -> onBackPressed())
                .create()
                .show();
    }


    private void setFlagSyncronization(int flag) {
        getApplicationContext().getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE)
                .edit().putInt(Constantes.PREF_SINCRONIZACION, flag).commit();
    }


    private void navigateToBusquedaNombreApellido() {
        startActivity(
                new Intent(this, BusquedaNombreApellidoActivity.class)
        );
    }

}
