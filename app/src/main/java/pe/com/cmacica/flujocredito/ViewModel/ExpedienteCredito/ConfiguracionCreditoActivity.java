package pe.com.cmacica.flujocredito.ViewModel.ExpedienteCredito;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import pe.com.cmacica.flujocredito.R;

public class ConfiguracionCreditoActivity extends AppCompatActivity {

    private static final String TAG = "ConfiguracionCreditoAct";

    private Toolbar _toolbar;



    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_credito);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);

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

    //endregion

}