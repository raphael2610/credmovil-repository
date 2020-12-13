package pe.com.cmacica.flujocredito.ViewModel.ExpedienteCredito;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import pe.com.cmacica.flujocredito.R;


public class ListadoCreditosActivity extends AppCompatActivity {

    private static final String TAG = "ListadoCreditosActivity";
    private Toolbar _toolbar;
    private RecyclerView _recyclerviewOffers;
    private ProgressDialog _progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_creditos);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _recyclerviewOffers = (RecyclerView) findViewById(R.id.recyclerviewOffers) ;

        initToolbar();
    }


    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.listado_creditos_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}