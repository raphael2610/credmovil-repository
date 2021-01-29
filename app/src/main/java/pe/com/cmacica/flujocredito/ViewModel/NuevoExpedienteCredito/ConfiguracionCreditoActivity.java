package pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Cliente;

import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito.ListadoExpedientesActivity;

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



    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_credito);

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

        initializeInformation();
        setupView();
    }

    // endregion



    //region setupView

    private void setupView() {
        initToolbar();

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

}