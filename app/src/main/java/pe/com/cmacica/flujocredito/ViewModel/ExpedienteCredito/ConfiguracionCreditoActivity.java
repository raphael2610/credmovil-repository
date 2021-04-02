package pe.com.cmacica.flujocredito.ViewModel.ExpedienteCredito;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.NavigableMap;

import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Cliente;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;
import pe.com.cmacica.flujocredito.ViewModel.Digitacion.FragmentoIgrFueraNegDet;

public class ConfiguracionCreditoActivity extends AppCompatActivity {

    private static final String TAG = "ConfiguracionCreditoAct";

    public static final String EXTRA_CREDIT = "credit";
    public static final String EXTRA_CLIENT = "client";

    private Toolbar _toolbar;
    private Credito _credit;
    private Cliente _client;
    private CardView _cardviewPersonalInformation;
    private ImageView _imageviewPersonalInformation;
    private TextView _textviewPersonalInformation;
    private CardView _cardviewEvaluation;
    private ImageView _imageviewEvaluation;
    private TextView _textviewEvaluation;
    private CardView _cardviewWarranty;
    private ImageView _imageviewWarranty;
    private TextView _textviewWarranty;
    private CardView _cardviewVisit;
    private ImageView _imageviewVisit;
    private TextView _textviewVisit;
    private CardView _cardviewInvestmentPlan;
    private ImageView _imageviewInvestmentPlan;
    private TextView _textviewInvestmentPlan;
    private CardView _cardviewCommittee;
    private ImageView _imageviewCommittee;
    private TextView _textviewCommittee;

    private ImageView _imageviewicon;
    private TextView _textviewicon;
    private EditText _txtanalist;
    private TextView _textviewfecha;
    private EditText _txtfecha;

    private ProgressDialog _progressDialog;




    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_credito2);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);

        _cardviewPersonalInformation = (CardView) findViewById(R.id.cardviewPersonalInformation);
        _imageviewPersonalInformation = (ImageView) findViewById(R.id.imageviewPersonalInformation);
        _textviewPersonalInformation = (TextView) findViewById(R.id.textviewPersonalInformation);

        _cardviewEvaluation = (CardView) findViewById(R.id.cardviewEvaluation);
        _imageviewEvaluation = (ImageView) findViewById(R.id.imageviewEvaluation);
        _textviewEvaluation = (TextView) findViewById(R.id.textviewEvaluation);

        _cardviewWarranty = (CardView) findViewById(R.id.cardviewWarranty);
        _imageviewWarranty = (ImageView) findViewById(R.id.imageviewWarranty);
        _textviewWarranty = (TextView) findViewById(R.id.textviewWarranty);

        _cardviewVisit = (CardView) findViewById(R.id.cardviewVisit);
        _imageviewVisit = (ImageView) findViewById(R.id.imageviewVisit);
        _textviewVisit = (TextView) findViewById(R.id.textviewVisit);

        _cardviewInvestmentPlan = (CardView) findViewById(R.id.cardviewInvestmentPlan);
        _imageviewInvestmentPlan = (ImageView) findViewById(R.id.imageviewInvestmentPlan);
        _textviewInvestmentPlan = (TextView) findViewById(R.id.textviewInvestmentPlan);

        _cardviewCommittee = (CardView) findViewById(R.id.cardviewCommittee);
        _imageviewCommittee = (ImageView) findViewById(R.id.imageviewCommittee);
        _textviewCommittee = (TextView) findViewById(R.id.textviewCommittee);

        _imageviewicon = (ImageView) findViewById(R.id.imageviewicon);
        _textviewicon = (TextView) findViewById(R.id.textviewicon);
        _txtanalist = (EditText) findViewById(R.id.txtanalist);

        _textviewfecha = (TextView) findViewById(R.id.textviewfecha);
        _txtfecha = (EditText) findViewById(R.id.txtfecha);


        initializeInformation();
        setupView();
    }

    // endregion



    //region setupView

    private void setupView() {
        initToolbar();

//        showAudit();

        _cardviewPersonalInformation.setOnClickListener(view -> navigationToListadoExpedientes(1));
        _imageviewPersonalInformation.setOnClickListener(view -> navigationToListadoExpedientes(1));
        _textviewPersonalInformation.setOnClickListener(view -> navigationToListadoExpedientes(1));

        _cardviewEvaluation.setOnClickListener(view -> navigationToListadoExpedientes(2));
        _imageviewEvaluation.setOnClickListener(view -> navigationToListadoExpedientes(2));
        _textviewEvaluation.setOnClickListener(view -> navigationToListadoExpedientes(2));

        _cardviewWarranty.setOnClickListener(view -> navigationToListadoExpedientes(3));
        _imageviewWarranty.setOnClickListener(view -> navigationToListadoExpedientes(3));
        _textviewWarranty.setOnClickListener(view -> navigationToListadoExpedientes(3));

        _cardviewVisit.setOnClickListener(view -> navigationToListadoExpedientes(4));
        _imageviewVisit.setOnClickListener(view -> navigationToListadoExpedientes(4));
        _textviewVisit.setOnClickListener(view -> navigationToListadoExpedientes(4));

        _cardviewInvestmentPlan.setOnClickListener(view -> navigationToListadoExpedientes(5));
        _imageviewInvestmentPlan.setOnClickListener(view -> navigationToListadoExpedientes(5));
        _textviewInvestmentPlan.setOnClickListener(view -> navigationToListadoExpedientes(5));

        _cardviewCommittee.setOnClickListener(view -> navigationToListadoExpedientes(6));
        _imageviewCommittee.setOnClickListener(view -> navigationToListadoExpedientes(6));
        _textviewCommittee.setOnClickListener(view -> navigationToListadoExpedientes(6));

    }

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(getString(R.string.configuracion_credito_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeInformation() {

        try {
            _credit = getIntent().getParcelableExtra(EXTRA_CREDIT);
            _client = getIntent().getParcelableExtra(EXTRA_CLIENT);

            _txtanalist.setText(_credit.getUserAudit());
            _txtfecha.setText(_credit.getDateAudit());

        } catch (Exception e) {}

    }

    //endregion


    // region navigation

    private void navigationToListadoExpedientes(int configuration) {

        Intent intent = new Intent(this, ListadoExpedientesActivity.class);
        intent.putExtra(ListadoExpedientesActivity.EXTRA_CREDIT, _credit);
        intent.putExtra(ListadoExpedientesActivity.EXTRA_CONFIGURATION, configuration);
        intent.putExtra(ListadoExpedientesActivity.EXTRA_CLIENT, _client);
        startActivity(intent);

    }

    // endregion


    // region network


//    private void showAudit() {
//
//        String personCode = _client.getPersonCode();
//        String user = UPreferencias.ObtenerUserLogeo(getApplicationContext());
//        // TODO dinamico user
//        user = "ERMM";
//
//        _progressDialog = ProgressDialog.show(this, getString(R.string.listado_creditos_msg_esperar), getString(R.string.listado_creditos_msg_obtener_creditos));
//
//
//        if (personCode.equals("")) {
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//            _progressDialog.cancel();
//            return;
//        }
//
//
//        String url = String.format(SrvCmacIca.GET_LISTADO_CREDITOS, _client.getPersonCode(), user);
//        String hola = "devRaphael";
//
//        VolleySingleton.getInstance(this)
//                .addToRequestQueue(
//                        new JsonObjectRequest(
//                                Request.Method.GET,
//                                url,
//                                response -> {
//                                    responseAudit(response);
//                                },
//                                error -> {
//                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//
//                        )
//                );
//
//    }
//
//
//    private void responseAudit(JSONObject response) {
//
//        _progressDialog.cancel();
//
//        try {
//
//            if (response.getBoolean("IsCorrect")) {
//
//                JSONObject data = response.getJSONObject("Data");
//                JSONArray expedientesCreditos = data.getJSONArray("ExpedienteCredito");
//
//                if (expedientesCreditos.length() >= 1) {
//
//                    JSONObject expedienteCredito = expedientesCreditos.getJSONObject(0);
//                    String userAudit = expedienteCredito.getString("cUserAudit");
//                    String auditDate = expedienteCredito.getString("dFechaAudit");
//
//                    _txtanalist.setText(userAudit);
//                    _txtfecha.setText(auditDate);
//                }
//
//            } else {
//                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception ex) {
//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//    }

    // endregion

}