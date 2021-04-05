package pe.com.cmacica.flujocredito.ViewModel.AgendaComercial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial.RegistroResultadoAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;

public class RegistroResultadoActivity extends AppCompatActivity {

    private static final String TAG = "RegistroResultadoActivi";
    public static final String EXTRA_VISIT = "visit";

    private Toolbar _toolbar;
    private TabLayout _tablayoutRegistroResultado;
    private ViewPager _viewpagerRegistroResultado;
    private FloatingActionButton _floatingactionbuttonRegistroResultado;
    private ProgressDialog _progressDialog;

    private RegistroResultadoAdapter _registroResultadoAdapter;
    private ClienteVisita _clienteVisita;
    private AgendaComercialDbHelper _agendaComercialDbHelper;


    // region lyfecicle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_resultado);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _tablayoutRegistroResultado = (TabLayout) findViewById(R.id.tablayoutRegistroResultado);
        _viewpagerRegistroResultado = (ViewPager) findViewById(R.id.viewpagerRegistroResultado);
        _floatingactionbuttonRegistroResultado = (FloatingActionButton) findViewById(R.id.floatingactionbuttonRegistroResultado);

        _agendaComercialDbHelper = new AgendaComercialDbHelper(this);

        initializeAndGetInformation();
        initToolbar();
        configurationTabLayoutViewPager();
        listenerView();
    }

    // endregion


    // region callback

    public interface RegistrarResultadolistener {
        void onClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (_viewpagerRegistroResultado!=null) {
            if (_registroResultadoAdapter!=null) {

                Fragment fragment = _registroResultadoAdapter.getItem(1);
                fragment.onActivityResult(requestCode, resultCode, data);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (_viewpagerRegistroResultado!=null) {
            if (_registroResultadoAdapter!=null) {

                Fragment fragment = _registroResultadoAdapter.getItem(1);
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);

            }
        }

    }

    // endregion


    // region ui controller

    private void initToolbar() {
        _toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        _toolbar.setNavigationOnClickListener(v -> onBackPressed());
        _toolbar.setTitle( getString(R.string.registro_resultado_title) );
        _toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
    }

    private void configurationTabLayoutViewPager() {

        List<Fragment> dataSource = new ArrayList<>();

        dataSource.add(ClienteFragment.newInstance(_clienteVisita));
        dataSource.add(ResultadoFragment.newInstance(_clienteVisita));

        List<String> dataSourceTitle = new ArrayList<>();
        dataSourceTitle.add(getString(R.string.cliente_title) );
        dataSourceTitle.add(getString(R.string.resultado_title));

        _registroResultadoAdapter = new RegistroResultadoAdapter(
                getSupportFragmentManager(),
                dataSource,
                dataSourceTitle
        );
        _viewpagerRegistroResultado.setAdapter(_registroResultadoAdapter);

        _tablayoutRegistroResultado.setupWithViewPager(_viewpagerRegistroResultado);
    }

    private void initializeAndGetInformation() {

        try {
            _clienteVisita = getIntent().getParcelableExtra(EXTRA_VISIT);
        } catch (Exception e) { }

    }

    private void changeFragment() {
        _viewpagerRegistroResultado.setCurrentItem(1);
    }

    private void listenerView() {

        _floatingactionbuttonRegistroResultado.setOnClickListener(v -> {

            if (_tablayoutRegistroResultado.getSelectedTabPosition() == 1) {
                ((RegistrarResultadolistener)getSupportFragmentManager().getFragments().get(1)).onClick();
            } else {
                changeFragment();
                ((RegistrarResultadolistener)getSupportFragmentManager().getFragments().get(0)).onClick();
            }

        } );


        _tablayoutRegistroResultado.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.d(TAG, "onTabSelected: " + tab.getPosition());
                if (tab.getPosition() == 1) {
                    _floatingactionbuttonRegistroResultado.setVisibility(View.VISIBLE);
                } else {
                    _floatingactionbuttonRegistroResultado.setVisibility(View.GONE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

    }

    // endregion


    // region network

    /*
    private void synchronizeResultVisits() {

        Log.d(TAG, "synchronizeCustomers: ");
        _progressDialog = ProgressDialog.show(this, getString(R.string.registro_resultado_msg_esperar), getString(R.string.registro_resultado_msg_sincronizando_resultado_visita));


        try {

            List<ResultadoVisita> resultadoVisitas = _agendaComercialDbHelper.getAllResultsSynchronize();
            if (resultadoVisitas!=null) {

                Log.d(TAG, "synchronizeCustomers: " + resultadoVisitas.size());
                Gson gsonpojo = new GsonBuilder().create();
                String json = gsonpojo.toJson(resultadoVisitas);
                HashMap<String, String> cabeceras = new HashMap<>();

                Log.d(TAG, "synchronizeCustomers: " + json);

                new RESTService(this).post(
                        SrvCmacIca.POST_GEOCLIENTES,
                        json,
                        response -> responseServerSynchronize(response),
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


    private void responseServerSynchronize(JSONObject response) {


        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, getString(R.string.registro_resultado_msg_resultados_guardados_exitosamente), Toast.LENGTH_SHORT).show();

                // TODO sincronizacion
                //_carteraAnalistaDbHelper.deleteAllCustomer();
                //_clienteAdapter.updateList(new ArrayList<>());

                //saveValidation(true);
                //showHideGetCustomer(true);

                AgendaComercialPreferences.setFlagSyncronization(this, CarteraAnalistaDbHelper.FLAG_SYNCRONIZED);
                //_buttonSynchronize.setText(getString(R.string.cartera_analista_action_get_customers));
            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    */


    // endregion

}
