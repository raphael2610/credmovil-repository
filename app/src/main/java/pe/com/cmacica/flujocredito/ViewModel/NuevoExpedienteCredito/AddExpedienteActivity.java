package pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
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
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Cliente;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Expediente;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.TipoExpediente;
import pe.com.cmacica.flujocredito.Model.carteraanalista.TipoDireccion;
import pe.com.cmacica.flujocredito.R;

public class AddExpedienteActivity extends AppCompatActivity {

    private static final String TAG = "AddExpedienteActivity";

    public static final String EXTRA_CREDIT = "credit";
    public static final String EXTRA_CONFIGURATION = "configuration";
    public static final String EXTRA_CLIENT = "client";

    private Toolbar _toolbar;
    private ProgressDialog _progressDialog;
    private Spinner _spinnerType;
    private Credito _credit;
    private Cliente _client;
    private int _configuration = 0;


    // region lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expediente);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _spinnerType = (Spinner) findViewById(R.id.spinnerType);

        setupView();
    }
    // endregion



    // region setupView

    private void setupView() {
        initToolbar();
        initializeAndGetInformation();
    }

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.add_expediente_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeAndGetInformation() {

        try {
            _credit = getIntent().getParcelableExtra(EXTRA_CREDIT);
            _configuration = getIntent().getIntExtra(EXTRA_CONFIGURATION, 0);
            _client = getIntent().getParcelableExtra(EXTRA_CLIENT);
            searchServerTypes();
        } catch (Exception e) { }

    }

    // endregion


    // region network

    private void searchServerTypes() {

        _progressDialog = ProgressDialog.show(this, getString(R.string.add_expediente_msg_esperar), getString(R.string.add_expediente_msg_obtener_tipos));

        String url = String.format(SrvCmacIca.GET_LISTADO_TIPOS_EXPEDIENTES, _configuration);

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> {
                                    responseServerTypes(response);
                                },
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerTypes(JSONObject response) {

        _progressDialog.cancel();

        try {

            if (response.getBoolean("IsCorrect")) {

                JSONArray jsonTypes = response.getJSONArray("Data");

                if (jsonTypes.length() == 0) {
                    Toast.makeText(this, getString(R.string.add_expediente_error_not_types), Toast.LENGTH_SHORT).show();
                } else {
                    loadListTypes(jsonTypes);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void loadListTypes(JSONArray jsonTypes) throws JSONException {

        List<TipoExpediente> typeFiles = new ArrayList<>();

        for (int i=0; i<jsonTypes.length(); i++) {

            JSONObject typeFile = jsonTypes.getJSONObject(i);

            TipoExpediente tipoExpediente = new TipoExpediente();
            tipoExpediente.setIdCar(typeFile.getInt("nIdCar"));
            tipoExpediente.setTypeName(typeFile.getString("NombreTipo"));
            tipoExpediente.setCodeType(typeFile.getInt("cTipoCod"));

            typeFiles.add(tipoExpediente);

        }

        ArrayAdapter<TipoExpediente> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, typeFiles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerType.setAdapter(adapter);

    }

    // endregion


}