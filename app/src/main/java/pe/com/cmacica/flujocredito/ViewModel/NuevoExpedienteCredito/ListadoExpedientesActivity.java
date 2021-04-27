package pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Cliente;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Expediente;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.TipoExpediente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.NuevoExpedienteCredito.ExpedienteAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;
import pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito.Manager.UploadImageWorker;

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
        updateInterface();

    }

    @Override
    protected void onStart() {
        super.onStart();
        searchServerFiles();
    }

    private void updateInterface(){

        WorkManager.getInstance(getApplicationContext())
                .getWorkInfosForUniqueWorkLiveData(UploadImageWorker.TAG)
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                        for (WorkInfo wi : workInfos) {
                            if (wi.getState() == WorkInfo.State.SUCCEEDED) {
                                Log.d(TAG, "onChanged: ");
                                Toast.makeText(ListadoExpedientesActivity.this, "Se ha guardado exitosamente el expediente", Toast.LENGTH_SHORT).show();
                                searchServerFiles();
                            }
                        }
                    }
                });

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desea actualizar este documento?");
        builder.setPositiveButton("SI", (dialog, which) ->  navigateToUpdateExpedienteActivity(expediente));
        builder.setNegativeButton("NO", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void onDeleteFile(Expediente expediente) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desea eliminar este documento?");
        builder.setPositiveButton("SI", (dialog, which) -> deleteFile(expediente));
        builder.setNegativeButton("NO", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

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
        Log.d("RAPHAEL", url);

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
        try {

            int idFile = expediente.getId();

            String user = UPreferencias.ObtenerUserLogeo(this);
            user = "ERMN";

            JSONObject json = new JSONObject();

            json.put("nIdObj", idFile);
            json.put("Usuario", user);


            HashMap<String, String> cabeceras = new HashMap<>();

            new RESTService(this).post(
                    SrvCmacIca.DELETE_EXPEDIENTE,
                    json.toString(),
                    response ->responseServerDeleteFile(response),
                    error -> {
                        _progressDialog.cancel();
                        Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    },
                    cabeceras
            );

        } catch (Exception e) {
            _progressDialog.cancel();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }






    }


    private void responseServerDeleteFile(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, "¡Documento eliminado correctamente!", Toast.LENGTH_SHORT).show();
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