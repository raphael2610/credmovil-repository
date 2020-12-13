package pe.com.cmacica.flujocredito.ViewModel.AgendaComercial;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.Cliente;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;

public class ActualizarDatosClienteActivity extends AppCompatActivity {

    private static final String TAG = "ActualizarDatosClienteA";

    public static final String EXTRA_VISIT = "visit";

    private Toolbar _toolbar;
    private TextInputEditText _textinputedittextPhoneOne;
    private TextInputEditText _textinputedittextPhoneTwo;
    private TextInputEditText _textinputedittextPhoneThree;
    private TextInputEditText _textinputedittextEmail;
    private TextInputEditText _textinputedittextAddress;
    private TextInputEditText _textinputedittextBusinessAddress;
    private TextInputEditText _textinputedittextHousingReference;
    private TextInputEditText _textinputedittextBusinessReference;
    private Button _buttonUpdateData;
    private ProgressDialog _progressDialog;

    private AgendaComercialDbHelper _agendaComercialDbHelper;
    private ClienteVisita _clientVisit;


    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_datos_cliente);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _buttonUpdateData = (Button) findViewById(R.id.buttonUpdateData);
        _textinputedittextPhoneOne = (TextInputEditText) findViewById(R.id.textinputedittextPhoneOne);
        _textinputedittextPhoneTwo = (TextInputEditText) findViewById(R.id.textinputedittextPhoneTwo);
        _textinputedittextPhoneThree = (TextInputEditText) findViewById(R.id.textinputedittextPhoneThree);
        _textinputedittextEmail = (TextInputEditText) findViewById(R.id.textinputedittextEmail);
        _textinputedittextAddress = (TextInputEditText) findViewById(R.id.textinputedittextAddress);
        _textinputedittextBusinessAddress = (TextInputEditText) findViewById(R.id.textinputedittextBusinessAddress);
        _textinputedittextHousingReference = (TextInputEditText) findViewById(R.id.textinputedittextHousingReference);
        _textinputedittextBusinessReference = (TextInputEditText) findViewById(R.id.textinputedittextBusinessReference);

        _agendaComercialDbHelper = new AgendaComercialDbHelper(this);
        listenerView();
        initializeAndGetInformation();

        initToolbar();
    }

    // endregion


    // region ui controller

    private void initializeAndGetInformation() {

        try {
            _clientVisit = getIntent().getParcelableExtra(EXTRA_VISIT);
        } catch (Exception e) { }

    }


    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.actualizar_datos_cliente_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void listenerView() {

        _buttonUpdateData.setOnClickListener( v ->  updateDataClient());

    }

    // endregion


    // region validation

    private void updateDataClient() {

        int idClient = _clientVisit==null ? 0 : _clientVisit.getIdCliente();

        // TODO DINAMICO idClient
//        idClient = 139;

        if (idClient == 0) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_error_obtener_id_cliente), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextPhoneOne.getText().toString().trim().length() < 9 &&
                _textinputedittextPhoneTwo.getText().toString().trim().length() < 9 &&
                _textinputedittextPhoneThree.getText().toString().trim().length() < 9 ) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_msg_telefonos), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( (_textinputedittextPhoneOne.getText().toString().trim().length() > 0 && _textinputedittextPhoneOne.getText().toString().trim().length() < 9) ||
                (_textinputedittextPhoneTwo.getText().toString().trim().length() > 0 && _textinputedittextPhoneTwo.getText().toString().trim().length() < 9) ||
                (_textinputedittextPhoneThree.getText().toString().trim().length() > 0 && _textinputedittextPhoneThree.getText().toString().trim().length() < 9)
        ) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_msg_telefonos_validos), Toast.LENGTH_SHORT).show();
            return;
        }


        if (_textinputedittextEmail.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_msg_correo), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmail(_textinputedittextEmail.getText().toString())) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_error_correo), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_msg_direccion_vivienda), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextBusinessAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_msg_direccion_negocio), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextHousingReference.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_msg_referencia_vivienda), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextBusinessReference.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.actualizar_datos_cliente_msg_referencia_negocio), Toast.LENGTH_SHORT).show();
            return;
        }


        try {

            String phone = _textinputedittextPhoneOne.getText().toString() + "/" +
                    _textinputedittextPhoneTwo.getText().toString() + "/" +
                    _textinputedittextPhoneThree.getText().toString();
            Cliente cliente = new Cliente();
            cliente.setIdClienteDni(idClient);
            cliente.setTelefono(phone);
            cliente.setCorreo(_textinputedittextEmail.getText().toString());
            cliente.setDireccionHogar(_textinputedittextAddress.getText().toString());
            cliente.setDireccionNegocio(_textinputedittextBusinessAddress.getText().toString());
            cliente.setReferenciaHogar(_textinputedittextHousingReference.getText().toString());
            cliente.setReferenciaNegocio(_textinputedittextBusinessReference.getText().toString());

            updateCustomerServer(cliente);

        } catch (Exception e) { }

    }

    private boolean isEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // endregion


    // region network

    private void updateCustomerServer(Cliente cliente) {



        _progressDialog = ProgressDialog.show(this, getString(R.string.actualizar_datos_cliente_msg_esperar), getString(R.string.actualizar_datos_cliente_msg_actualizar_cliente));


        try {

            ArrayList<Cliente> allClient = new ArrayList<>();
            allClient.add(cliente);

            if (allClient!=null) {

                Gson gsonpojo = new GsonBuilder().create();
                String json = gsonpojo.toJson(allClient);
                HashMap<String, String> cabeceras = new HashMap<>();

                Log.d(TAG, "synchronizeCustomers: " + json);

                new RESTService(this).post(
                        SrvCmacIca.POST_ACTUALIZAR_CLIENTE,
                        json,
                        response -> responseServerUpdateCustomer(response),
                        error -> {
                            _progressDialog.cancel();
                            Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        },
                        cabeceras
                );

            } else {
                Log.d(TAG, "synchronizeCustomers: error");
            }

        } catch (Exception e) {
            _progressDialog.cancel();
            Toast.makeText(this, getString(R.string.cartera_analista_error_sincronizar_clientes), Toast.LENGTH_SHORT).show();
        }


    }


    private void responseServerUpdateCustomer(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, "¡Cliente actualizado correctamente!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    // endregion
}
