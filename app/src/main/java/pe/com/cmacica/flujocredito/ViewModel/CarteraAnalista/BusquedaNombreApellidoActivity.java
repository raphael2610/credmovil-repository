package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Credito;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista.BusquedaClienteAdapter;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista.CreditoAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.Common;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaContract;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaPreferences;

public class BusquedaNombreApellidoActivity extends AppCompatActivity
        implements BusquedaClienteAdapter.BusquedaClienteListener {

    private static final String TAG = "BusquedaNombreApellidoA";
    private static final int LOADER_SEARCH_CUSTOMER = 1;

    private Toolbar _toolbar;
    private TextInputEditText _textinputedittextName;
    private TextInputEditText _textinputedittextLastName;
    private TextInputEditText _textinputedittextMotherLastName;
    private Button _buttonSearchCustomer;
    private RecyclerView _recyclerviewBusquedaNombreApellido;
    private ConstraintLayout _constraintlayoutSave;
    private TextInputEditText _textinputedittextDNI;
    private TextInputEditText _textinputedittextPhone;
    private Button _buttonSaveCustomer;
    private ProgressDialog _progressDialog;

    private BusquedaClienteAdapter _busquedaClienteAdapter;
    private CarteraAnalistaDbHelper _carteraAnalistaDbHelper;


    // region lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_nombre_apellido);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _textinputedittextName = (TextInputEditText) findViewById(R.id.textinputedittextName);
        _textinputedittextLastName = (TextInputEditText) findViewById(R.id.textinputedittextLastName);
        _textinputedittextMotherLastName = (TextInputEditText) findViewById(R.id.textinputedittextMotherLastName);
        _buttonSearchCustomer = (Button) findViewById(R.id.buttonSearchCustomer);
        _recyclerviewBusquedaNombreApellido = (RecyclerView) findViewById(R.id.recyclerviewBusquedaNombreApellido);
        _constraintlayoutSave = (ConstraintLayout) findViewById(R.id.constraintlayoutSave);
        _textinputedittextDNI = (TextInputEditText) findViewById(R.id.textinputedittextDNI);
        _textinputedittextPhone = (TextInputEditText) findViewById(R.id.textinputedittextPhone);
        _buttonSaveCustomer = (Button) findViewById(R.id.buttonSaveCustomer);


        _carteraAnalistaDbHelper = new CarteraAnalistaDbHelper(this);

        initToolbar();
        listenerView();
        setupRecyclerView();

        //searchDBCmaica();

    }
    // endregion


    // region callback

    @Override
    public void onClick(Cliente cliente) {

        // TODO 3 escuchador de click a cada item para insertar de manera local
        // TODO 3.1 validacion de si el registro existe (no insertar)
        // TODO 3.2 el registro no existe (lo inserta)

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Object... objects) {
                return _carteraAnalistaDbHelper.insertCustomerSearchName(cliente);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    CarteraAnalistaPreferences.setValidationGetCustomer(getApplicationContext(), false);
                    Toast.makeText(BusquedaNombreApellidoActivity.this, getString(R.string.busqueda_nombre_apellido_msg_registro_existoso), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(BusquedaNombreApellidoActivity.this, getString(R.string.busqueda_nombre_apellido_msg_registro_no_existoso), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }

    // endregion


    // region ui controller

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.busqueda_nombre_apellido_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void listenerView() {

        _buttonSearchCustomer.setOnClickListener(v -> searchCustomer() );
        _buttonSaveCustomer.setOnClickListener(v -> saveCustomer());

    }

    private void setupRecyclerView() {
        List<Cliente> clienteList = new ArrayList<>();
        _busquedaClienteAdapter = new BusquedaClienteAdapter(this, clienteList);
        _recyclerviewBusquedaNombreApellido.setAdapter(_busquedaClienteAdapter);
    }

    // endregion



    // region logica
    private void searchCustomer() {

        Common.hideKeyboard(this, _textinputedittextDNI);
        CarteraAnalistaPreferences.limpiarFiltros(getApplicationContext());

        _progressDialog = ProgressDialog.show(this,getString(R.string.registrar_usuario_msg_esperar),getString(R.string.registrar_usuario_msg_validacion_campos));


        if ( _textinputedittextLastName.getText().toString().trim().length() == 0 ) {
            _progressDialog.cancel();
            Toast.makeText(this, getString(R.string.busqueda_nombre_apellido_msg_apellido_paterno), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( _textinputedittextMotherLastName.getText().toString().trim().length() == 0 ) {
            _progressDialog.cancel();
            Toast.makeText(this, getString(R.string.busqueda_nombre_apellido_msg_apellido_materno), Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO 1 buscar en los servidores de cmacica
        _progressDialog.setMessage(getString(R.string.registrar_usuario_msg_buscando_clientes_locales));
        searchDBCmaica();

    }


    private void saveCustomer() {



    }
    // endregion


    // region navigation
    private void navigateRegistrarUsuario() {
        startActivity(
                new Intent(this, RegistrarUsuarioActivity.class)
        );
    }
    // endregion

    // region network


    private void searchDBCmaica() {

        String user = UPreferencias.ObtenerUserLogeo(this);

        String url = String.format(
                SrvCmacIca.GET_CLIENTES_CON_GEOPOSICION,
                _textinputedittextName.getText().toString().isEmpty() ? "%" : _textinputedittextName.getText().toString(),
                _textinputedittextLastName.getText().toString(),
                _textinputedittextMotherLastName.getText().toString(),
                user
        );

        Log.d(TAG, "searchDBCmaica: " + url);

        VolleySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServer(response),
                                error -> {
                                    //_progressDialog.cancel();
                                    //enableDisableField(true);
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        )
                );

    }


    private void responseServer(JSONObject response) {

        Log.d(TAG, "responseServer: " + response);
        try {
            if(response.getBoolean("IsCorrect")){
                JSONArray jsonClientes = response.getJSONArray("Data");

                if(jsonClientes.length() == 0){
                    _progressDialog.cancel();
                    Toast.makeText(this, getString(R.string.registrar_usuario_msg_cliente_no_encontrado), Toast.LENGTH_LONG).show();
                    navigateRegistrarUsuario();
                    finish();
                } else {
                    // TODO 2 mostrar en recyclerview el resultados de la busqueda
                    _progressDialog.cancel();
                    listCustomer(jsonClientes);
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


    private void listCustomer(JSONArray jsonCustomers) {


        try {

            List<Cliente> clienteList = new ArrayList<>();

            for (int i = 0; i < jsonCustomers.length(); i++) {

                JSONObject row = jsonCustomers.getJSONObject(i);

                Cliente cliente = new Cliente();

                cliente.setPersCod(row.getString("PersCod"));
                cliente.setNombre(row.getString("PersNombre"));
                cliente.setDoi(row.getString("NumeroDocumento"));
                cliente.setTelefonoUno(row.getString("PersTelefono"));
                cliente.setTelefonoDos(row.getString("PersTelefono2"));
                cliente.setDireccionDomicilio(row.getString("PersDireccDomicilio"));
                cliente.setCreditos(row.getString("Creditos"));
                cliente.setGeoposicion(row.getString("GeoPosicion"));

                cliente.setTelefono(row.getString("Telefono"));
                cliente.setIdTipoDireccion(row.getInt("TipoDireccion"));
                cliente.setReferencia(row.getString("Direccion"));
                cliente.setLongitud(row.getDouble("Longitud"));
                cliente.setLatitud(row.getDouble("Latitud"));

                if (row.getString("CarteraAnalista").toUpperCase().equals("SI")) {
                    cliente.setFlag(CarteraAnalistaDbHelper.FLAG_ACTUAL_ANALISTA);
                } else {
                    cliente.setFlag(CarteraAnalistaDbHelper.FLAG_OTRAS_CARTERAS_ANALISTA);
                }


                clienteList.add(cliente);
            }

            _busquedaClienteAdapter.updateList(clienteList);

        } catch (Exception e) {

        }

    }


    // endregion


}

