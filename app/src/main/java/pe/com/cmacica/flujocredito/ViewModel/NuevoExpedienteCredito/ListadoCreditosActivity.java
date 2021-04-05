package pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;

import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

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
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.ExpedienteCredito.CreditoAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;

public class ListadoCreditosActivity extends AppCompatActivity
                                    implements CreditoAdapter.CreditoAdapterListener {

    private static final String TAG = "ListadoCreditosActivity";

    public static final String EXTRA_CLIENT = "client";

    private Toolbar _toolbar;
    private Credito _credit;
    private Cliente _client;
    private RecyclerView _recyclerviewCredits;
    private CreditoAdapter _creditoAdapter;
    private ProgressDialog _progressDialog;
    private Button _buttonCreateCreditFictional;


    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_creditos2);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _buttonCreateCreditFictional = (Button) findViewById(R.id.buttonCreateCreditFictional);
        _recyclerviewCredits = (RecyclerView) findViewById(R.id.recyclerviewCredits) ;

        initializeAndGetInformation();
        setupView();
    }

    // endregion



    // region setupView

    private void setupView() {
        initToolbar();
        setupRecyclerView();
        _buttonCreateCreditFictional.setOnClickListener(view -> {
            createCreditFictional();
        });
    }

    private void initializeAndGetInformation() {

        try {
            _client = getIntent().getParcelableExtra(EXTRA_CLIENT);
            searchServerCredits();
        } catch (Exception e) { }

    }


    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.listado_creditos_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        List<Credito> creditoList = new ArrayList<>();
        _creditoAdapter = new CreditoAdapter(creditoList, this);
        _recyclerviewCredits.setAdapter(_creditoAdapter);
    }

    private void loadListCredits(JSONArray jsonCredits) throws JSONException {

        List<Credito> credits = new ArrayList<>();

        for (int i=0; i<jsonCredits.length(); i++) {

            JSONObject credit = jsonCredits.getJSONObject(i);

            Credito credito = new Credito();
            credito.setId(i);
            credito.setPersonCode(credit.getString("cPersCod"));
            credito.setNumberCredit(credit.getString("cCtaCod"));
            credito.setTypeCredit(credit.getString("cTipoCred"));
            credito.setRefundDate(credit.getString("dSolicitado"));
            credito.setState(credit.getString("cEstadoDesc"));
            credito.setAmount(credit.getString("nPrestamo"));
            credits.add(credito);

        }

        _creditoAdapter.updateList(credits);

    }

    // endregion



    // region callback

    @Override
    public void onClick(Credito credito) {
        navigateToConfigurationCredito(credito);
    }

    // endregion



    // region navigation
    private void navigateToConfigurationCredito(Credito credito) {
        Intent intent = new Intent(this, ConfiguracionCreditoActivity.class);
        intent.putExtra(ConfiguracionCreditoActivity.EXTRA_CREDIT, credito);
        intent.putExtra(ConfiguracionCreditoActivity.EXTRA_CLIENT, _client);
        startActivity(intent);
    }
    // endregion



    // region network

    private void searchServerCredits() {

        String personCode = _client.getPersonCode();
        String user = UPreferencias.ObtenerUserLogeo(getApplicationContext());
        // TODO dinamico user
        user = "ERMM";

        _progressDialog = ProgressDialog.show(this, getString(R.string.listado_creditos_msg_esperar), getString(R.string.listado_creditos_msg_obtener_creditos));


        if (personCode.equals("")) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }


        String url = String.format(SrvCmacIca.GET_LISTADO_CREDITOS, _client.getPersonCode(), user);
        String hola = "devRaphael";

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> {
                                    responseServerCredits(response);
                                },
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );


    }

    private void responseServerCredits(JSONObject response) {

        _progressDialog.cancel();

        try {

            if (response.getBoolean("IsCorrect")) {

                JSONObject data = response.getJSONObject("Data");
                JSONArray jsonCredits = data.getJSONArray("ExpedienteCredito");

                if (jsonCredits.length() == 0) {
                    Toast.makeText(this, getString(R.string.listado_creditos_error_not_credits), Toast.LENGTH_SHORT).show();
                } else {
                    loadListCredits(jsonCredits);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void createCreditFictional() {

        _progressDialog = ProgressDialog.show(this, getString(R.string.listado_expediente_msg_esperar), "Creando Credito Fictisio");

        String personCode = _client.getPersonCode();

        String user = UPreferencias.ObtenerUserLogeo(getApplicationContext());
        // TODO dinamico user
        user = "ERMM";

        String url = String.format(SrvCmacIca.CREATE_CREDIT_FICTIONAL, personCode, user);

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                response -> {
                                    responseServerCreateCreditFictional(response);
                                },
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    _progressDialog.cancel();
                                }

                        )
                );


    }

    private void responseServerCreateCreditFictional(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, "¡Creación de Credito Ficticio correctamente!", Toast.LENGTH_SHORT).show();
                searchServerCredits();
            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    // endregion

}