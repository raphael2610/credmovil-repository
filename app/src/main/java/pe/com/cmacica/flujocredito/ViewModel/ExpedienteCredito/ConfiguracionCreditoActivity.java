package pe.com.cmacica.flujocredito.ViewModel.ExpedienteCredito;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.R;

public class ConfiguracionCreditoActivity extends AppCompatActivity {

    private static final String TAG = "ConfiguracionCreditoAct";

    public static final String EXTRA_CREDIT = "credit";

    private Toolbar _toolbar;
    private Credito _credit;



    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_credito);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);

        initializeInformation();
        setupView();
    }

    // endregion



    //region setupView

    private void setupView() {
        initToolbar();
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
        } catch (Exception e) {}

    }

    //endregion

}