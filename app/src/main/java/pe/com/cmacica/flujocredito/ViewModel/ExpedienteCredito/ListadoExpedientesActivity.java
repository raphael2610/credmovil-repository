package pe.com.cmacica.flujocredito.ViewModel.ExpedienteCredito;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Expediente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.ExpedienteCredito.ExpedienteAdapter;

public class ListadoExpedientesActivity extends AppCompatActivity {

    private static final String TAG = "ListadoExpedientesActiv";

    public static final String EXTRA_CREDIT = "credit";
    public static final String EXTRA_CONFIGURATION = "configuration";

    private Toolbar _toolbar;
    private RecyclerView _recyclerviewFiles;
    private ExpedienteAdapter _proceedingAdapter;
    private ProgressDialog _progressDialog;
    private Credito _credit;
    private int _configuration = 0;



    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_expedientes);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _recyclerviewFiles = (RecyclerView) findViewById(R.id.recyclerviewFiles);

        setupView();
    }

    // endregion


    // region setupView

    private void setupView() {
        initializeAndGetInformation();
        initToolbar();
        setupRecyclerView();
    }

    private void initializeAndGetInformation() {

        try {
            _credit = getIntent().getParcelableExtra(EXTRA_CREDIT);
            _configuration = getIntent().getIntExtra(EXTRA_CONFIGURATION, 0);
            searchServerFiles();
        } catch (Exception e) { }

    }

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.listado_expediente_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        List<Expediente> expedienteList = new ArrayList<>();
        _proceedingAdapter = new ExpedienteAdapter(expedienteList);
        _recyclerviewFiles.setAdapter(_proceedingAdapter);
    }

    private void loadListFiles(JSONArray jsonFiles) throws JSONException {

        List<Expediente> proceedings = new ArrayList<>();

        for (int i=0; i<jsonFiles.length(); i++) {

            JSONObject proceeding = jsonFiles.getJSONObject(i);

            Expediente expediente = new Expediente();
            expediente.setId(i);
            expediente.setName(proceeding.getString("cObjNombre"));
            expediente.setDate(proceeding.getString("FechaCreacion"));

            proceedings.add(expediente);

        }

        _proceedingAdapter.updateList(proceedings);

    }

    // endregion



    // region network

    private void searchServerFiles() {

        _progressDialog = ProgressDialog.show(this, getString(R.string.listado_expediente_msg_esperar), getString(R.string.listado_expediente_msg_obtener_expedientes));

        String account = _credit.getNumberCredit();

        if (account.equals("")) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }

        if (_configuration == 0) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }

        String url = String.format(SrvCmacIca.GET_LISTADO_EXPEDIENTES, account, String.valueOf(_configuration));

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> {
                                    responseServerFiles(response);
                                },
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerFiles(JSONObject response) {

        _progressDialog.cancel();

        try {

            if (response.getBoolean("IsCorrect")) {

                JSONObject data = response.getJSONObject("Data");
                JSONArray jsonFiles = data.getJSONArray("HistorialExpedientes");

                if (jsonFiles.length() == 0) {
                    Toast.makeText(this, getString(R.string.listado_expediente_error_not_proceedings), Toast.LENGTH_SHORT).show();
                } else {
                    loadListFiles(jsonFiles);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    // endregion

}