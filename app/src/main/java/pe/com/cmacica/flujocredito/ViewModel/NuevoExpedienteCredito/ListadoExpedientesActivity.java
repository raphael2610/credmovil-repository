package pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.NuevoExpedienteCredito.ExpedienteAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;

public class ListadoExpedientesActivity extends AppCompatActivity
                                implements ExpedienteAdapter.ExpedienteAdapterListener {

    private static final String TAG = "ListadoExpedientesActiv";

    public static final String EXTRA_CREDIT = "credit";
    public static final String EXTRA_CONFIGURATION = "configuration";
    public static final String EXTRA_CLIENT = "client";

    private Toolbar _toolbar;
    private RecyclerView _recyclerviewFiles;
    private ExpedienteAdapter _proceedingAdapter;
    private ProgressDialog _progressDialog;
    private FloatingActionButton _floatingactionbuttonAddExpediente;
    private Credito _credit;
    private Cliente _client;
    private int _configuration = 0;


    // region lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_expedientes2);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _recyclerviewFiles = (RecyclerView) findViewById(R.id.recyclerviewExpedientes);
        _floatingactionbuttonAddExpediente = (FloatingActionButton) findViewById(R.id.floatingactionbuttonAddExpediente);

        setupView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        searchServerFiles();
    }

    // endregion


    // region setupView
    private void setupView() {
        initializeAndGetInformation();
        initToolbar();
        setupRecyclerView();

        _floatingactionbuttonAddExpediente.setOnClickListener(view -> {
            navigateToAddExpedienteActivity();
        });

    }

    private void initializeAndGetInformation() {

        try {
            _credit = getIntent().getParcelableExtra(EXTRA_CREDIT);
            _configuration = getIntent().getIntExtra(EXTRA_CONFIGURATION, 0);
            _client = getIntent().getParcelableExtra(EXTRA_CLIENT);
//            searchServerFiles();
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
        _proceedingAdapter = new ExpedienteAdapter(expedienteList, this);
        _recyclerviewFiles.setAdapter(_proceedingAdapter);
    }

    private void loadListFiles(JSONArray jsonFiles) throws JSONException {

        List<Expediente> proceedings = new ArrayList<>();

        for (int i=0; i<jsonFiles.length(); i++) {

            JSONObject proceeding = jsonFiles.getJSONObject(i);

            Expediente expediente = new Expediente();
            expediente.setId(proceeding.getInt("nIdObj"));
            expediente.setName(proceeding.getString("cObjNombre"));
            expediente.setDate(proceeding.getString("FechaCreacion"));
            expediente.setImage(proceeding.getString("cImagen"));
            expediente.setUser(proceeding.getString("Usuario"));

            proceedings.add(expediente);

        }

        _proceedingAdapter.updateList(proceedings);

    }

    // endregion


    // region callback

    @Override
    public void onUpdateFile(Expediente expediente) {
        navigateToUpdateExpedienteActivity(expediente);
    }

    @Override
    public void onDeleteFile(Expediente expediente) {
        deleteFile(expediente);
    }


    // endregion



    // region navigation
    private void navigateToUpdateExpedienteActivity(Expediente expediente) {

        Intent intent = new Intent(this, UpdateExpedienteActivity.class);
        intent.putExtra(UpdateExpedienteActivity.EXTRA_FILE, expediente);
        intent.putExtra(UpdateExpedienteActivity.EXTRA_CLIENT, _client);
        intent.putExtra(UpdateExpedienteActivity.EXTRA_CONFIGURATION, _configuration);
        intent.putExtra(UpdateExpedienteActivity.EXTRA_CREDIT, _credit);
        startActivity(intent);

    }
    // endregion



    // region network

    private void searchServerFiles() {

        _progressDialog = ProgressDialog.show(this, getString(R.string.listado_expediente_msg_esperar), getString(R.string.listado_expediente_msg_obtener_expedientes));

        String account = _credit.getNumberCredit();
//        account = "108493041000034584";
        String personCode = _client.getPersonCode();
//        personCode = "1081800207730";

        if (account.equals("")) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }

        if (personCode.equals("")) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }

        if (_configuration == 0) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }

        String url = String.format(SrvCmacIca.GET_LISTADO_EXPEDIENTES, account, _configuration, personCode);
        Log.d("CRISTIAN", url);

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

//                JSONObject data = response.getJSONObject("Data");
                JSONArray jsonFiles = response.getJSONArray("Data");

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

    private void deleteFile(Expediente expediente) {


        _progressDialog = ProgressDialog.show(this, getString(R.string.listado_expediente_msg_esperar), getString(R.string.listado_expediente_msg_eliminar_expediente));

        int idFile = expediente.getId();

        String user = UPreferencias.ObtenerUserLogeo(this);
        user = "CTMR";


        String url = String.format(SrvCmacIca.DELETE_EXPEDIENTE, idFile, _configuration, user);

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                response -> {
                                    responseServerDeleteFile(response);
                                },
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    _progressDialog.cancel();
                                }

                        )
                );

    }


    private void responseServerDeleteFile(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, "¡Expediente eliminado correctamente!", Toast.LENGTH_SHORT).show();
                searchServerFiles();
            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    // endregion


    // region navigation

    private void navigateToAddExpedienteActivity() {

        Intent intent = new Intent(this, AddExpedienteActivity.class);
        intent.putExtra(AddExpedienteActivity.EXTRA_CLIENT, _client);
        intent.putExtra(AddExpedienteActivity.EXTRA_CONFIGURATION, _configuration);
        intent.putExtra(AddExpedienteActivity.EXTRA_CREDIT, _credit);
        startActivity(intent);

    }

    // endregion

}