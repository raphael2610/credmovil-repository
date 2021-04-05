package pe.com.cmacica.flujocredito.ViewModel.AgendaComercial;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial.OfertaClienteAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialPreferences;

public class OfertasClienteActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "OfertasClienteActivity";


//    no va
    public static final String EXTRA_VISIT = "visit";
    public static final int LOADER_OFFERS = 1;

    private Toolbar _toolbar;
    private RecyclerView _recyclerviewOffers;
    private ProgressDialog _progressDialog;

    private OfertaClienteAdapter _ofertaClienteAdapter;
    private ClienteVisita _clientVisit;
    private AgendaComercialDbHelper _agendaComercialDbHelper;

    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertas_cliente);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _recyclerviewOffers = (RecyclerView) findViewById(R.id.recyclerviewOffers);

        _agendaComercialDbHelper = new AgendaComercialDbHelper(this);
        initializeAndGetInformation();
        initToolbar();
        setupRecyclerView();
//        searchServerOffersClient();
    }

    // endregion


    // region callback

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                return _agendaComercialDbHelper.getAllOfferClient();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        _ofertaClienteAdapter.updateList(
                _agendaComercialDbHelper.listOfferClient(data)
        );

        getSupportLoaderManager().destroyLoader(LOADER_OFFERS);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }


    // endregion


    // region ui controller

    private void initializeAndGetInformation() {

        try {
            _clientVisit = getIntent().getParcelableExtra(EXTRA_VISIT);
            searchServerOffersClient();
        } catch (Exception e) { }

    }

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.ofertas_cliente_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void setupRecyclerView() {
        _ofertaClienteAdapter = new OfertaClienteAdapter(new ArrayList<>());
        _recyclerviewOffers.setAdapter(_ofertaClienteAdapter);
    }

    // endregion


    // region source local

    private void searchLocalOffers() {
        getSupportLoaderManager().initLoader(LOADER_OFFERS, null, this);
    }

    // endregion



    // region network

    private void searchServerOffersClient() {

        int idUser = AgendaComercialPreferences.getIdUser(this);
        int idClient = _clientVisit==null ? 0 : _clientVisit.getIdCliente();

        // TODO DINAMICO idCliente, idUser
//        idUser = 309;
//        idClient = 217;


        if (idUser == 0) {
            Toast.makeText(this, getString(R.string.ofertas_cliente_error_obtener_usuario), Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }

        if (idClient == 0) {
            Toast.makeText(this, getString(R.string.ofertas_cliente_error_id_cliente), Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }

        _progressDialog = ProgressDialog.show(this, getString(R.string.ofertas_cliente_msg_esperar), getString(R.string.ofertas_cliente_msg_obtener_ofertas));


        String url = String.format(SrvCmacIca.GET_VALIDAR_CLIENTE_REGISTRO_RESULTADO, idUser, idClient);

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> {

                                    responseServerOffersClient(response);
                                    },
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );
    }

    private void responseServerOffersClient(JSONObject response) {

        Log.d(TAG, "responseServerOffersClient: " + response);
        _progressDialog.cancel();


        try{

            if(response.getBoolean("IsCorrect")){
//                Toast.makeText(this, getString(R.string.ofertas_cliente_msg_ofertas_obtenidas), Toast.LENGTH_SHORT).show();

                JSONArray jsonProvinces = response.getJSONArray("Data");

                JSONObject jobj = jsonProvinces.getJSONObject(0);
                Integer id = jobj.getInt("IdUsuario");

                if(id == 0){
                    Toast.makeText(this, getString(R.string.ofertas_cliente_msg_obtener_ofertas_vacia), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.ofertas_cliente_msg_ofertas_obtenidas), Toast.LENGTH_SHORT).show();
                    insertOffersClient(jsonProvinces);
                }

//                if(jsonProvinces.length() == 0){
//                    Toast.makeText(this, getString(R.string.ofertas_cliente_msg_obtener_ofertas_vacia), Toast.LENGTH_LONG).show();
//                } else {
//                    insertOffersClient(jsonProvinces);
//                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void insertOffersClient(JSONArray jsonOfferClient) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteaAllOfferClient();
                return _agendaComercialDbHelper.insertOfferClient(jsonOfferClient);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    searchLocalOffers();
                } else {
                    Toast.makeText(OfertasClienteActivity.this, getString(R.string.ofertas_cliente_error_insertar_ofertas), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();


    }

    // endregion


}
