package pe.com.cmacica.flujocredito.ViewModel.AgendaComercial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ResultadoVisita;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial.VisitaAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialPreferences;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;

public class AgendaComercialFragment extends Fragment
        implements VisitaAdapter.VisitaAdapterListener,
        LoaderManager.LoaderCallbacks<Cursor> {



    private static final String TAG = "AgendaComercialFragment";
    public static final int LOADER_VISITS = 1;
    public static final int LOADER_SEARCH = 2;
    public static final String EXTRA_NAME = "name";

    private ConstraintLayout _constraintlayoutVisitNotFound;
    private ConstraintLayout _constraintlayoutMain;
    private RecyclerView _recyclerviewAgendaComercial;
    private ProgressDialog _progressDialog;
    private TextView _textContent;
    private TextInputEditText _textinputedittextSearch;
    private Button _buttonGetSynchronize;

    private VisitaAdapter _visitaAdapter;
    private AgendaComercialDbHelper _agendaComercialDbHelper;
    private List<ClienteVisita> _clienteVisitaList;
    private boolean _flagSynchronize = false;


    // region lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agenda_comercial, container, false);

        _constraintlayoutVisitNotFound = (ConstraintLayout) view.findViewById(R.id.constraintlayoutVisitNotFound);
        _constraintlayoutMain = (ConstraintLayout) view.findViewById(R.id.constraintlayoutMain);
        _recyclerviewAgendaComercial = (RecyclerView) view.findViewById(R.id.recyclerviewAgendaComercial);
        _textContent = (TextView) view.findViewById(R.id.textContent);
        _textinputedittextSearch = (TextInputEditText) view.findViewById(R.id.textinputedittextSearch);
        _buttonGetSynchronize = (Button) view.findViewById(R.id.buttonGetSynchronize);

        _agendaComercialDbHelper = new AgendaComercialDbHelper(getContext());

        listenerView();
        setupRecyclerView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        searchLocalVisits();
    }

    // endregion


    // region callback

    @Override
    public void onClick(ClienteVisita clienteVisita) {

        if (clienteVisita.getResultado() == AgendaComercialDbHelper.FLAG_CON_RESULTADO) {

            new AlertDialog.Builder( getContext() )
                    .setTitle(getString(R.string.agenda_comercial_dialog_title))
                    .setMessage(getString(R.string.agenda_comercial_dialog_content))
                    .setPositiveButton(R.string.agenda_comercial_dialog_action_ok, (dialog, which) -> { dialog.dismiss(); } )
                    .create()
                    .show();

        } else {
            navigateToRegistrarUsuario(clienteVisita);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader = new CursorLoader(getContext());

        switch (id) {
            case LOADER_VISITS:
                cursorLoader = new CursorLoader(getContext()) {
                    @Override
                    public Cursor loadInBackground() {
                        return _agendaComercialDbHelper.getAllVisits();
                    }
                };
                break;
            case LOADER_SEARCH:
                cursorLoader = new CursorLoader(getContext()) {
                    @Override
                    public Cursor loadInBackground() {
                        return _agendaComercialDbHelper.filterVisits(args.getString(EXTRA_NAME));
                    }
                };
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        List<ClienteVisita> _filterVisitList;
        switch (loader.getId()) {
            case LOADER_VISITS:
                _clienteVisitaList = _agendaComercialDbHelper.listVisits(data);
                _visitaAdapter.updateList(_clienteVisitaList);
                validationSynchronize(_clienteVisitaList);
                getLoaderManager().destroyLoader(LOADER_VISITS);
                break;
            case LOADER_SEARCH:
                _filterVisitList = _agendaComercialDbHelper.listVisits(data);
                _visitaAdapter.updateList(_filterVisitList);
                getLoaderManager().destroyLoader(LOADER_SEARCH);
                break;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    // endregion


    // region ui controller

    private void setupRecyclerView() {
        List<ClienteVisita> clienteVisitas = new ArrayList<>();

    _visitaAdapter = new VisitaAdapter(clienteVisitas,this, getContext());
        _recyclerviewAgendaComercial.setAdapter(_visitaAdapter);
}

    private void showVisitNotFound(String message) {
        _textContent.setText(message);
        _constraintlayoutVisitNotFound.setVisibility(View.VISIBLE);
        _constraintlayoutMain.setVisibility(View.GONE);
    }

    private void hideVisitNotFound() {
        _textContent.setText(R.string.agenda_comercial_msg_pendientes_visita_content);
        _constraintlayoutVisitNotFound.setVisibility(View.GONE);
        _constraintlayoutMain.setVisibility(View.VISIBLE);
    }

    private void listenerView() {

        _buttonGetSynchronize.setOnClickListener( v -> {

            if (_flagSynchronize == true) {
                synchronizeResultVisit();
            } else {
                searchServerIdUsuario();
            }


        } );

        _textinputedittextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_NAME, String.valueOf(s).trim());
                Fragment fragment = getFragmentManager().findFragmentByTag(AgendaComercialFragment.class.getSimpleName());
                getLoaderManager().initLoader(LOADER_SEARCH, bundle, (AgendaComercialFragment)fragment);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }

    // endregion ui controller


    // region validation

    private void validationSynchronize(List<ClienteVisita> clienteVisitaList) {

        _flagSynchronize = false;

        for (ClienteVisita clienteVisita : clienteVisitaList) {

            if (clienteVisita.getResultado() == AgendaComercialDbHelper.FLAG_CON_RESULTADO) {
                if (clienteVisita.getSincronizar() == AgendaComercialDbHelper.FLAG_NOT_SYNCRONIZED) {
                    _flagSynchronize = true;
                    _buttonGetSynchronize.setText(getString(R.string.agenda_comercial_action_sincronizar));
                }
            }

        }

    }

    // enregion

    // region navigation
    private void navigateToRegistrarUsuario(ClienteVisita clienteVisita) {
        Intent intent = new Intent(getActivity(), RegistroResultadoActivity.class);
        intent.putExtra(RegistroResultadoActivity.EXTRA_VISIT, clienteVisita);
        startActivity(intent);
    }
    // endregion



    // region data source local

    private void searchLocalVisits() {
        getLoaderManager().initLoader(LOADER_VISITS, null, this);
    }

    // endregion



    // region network

    private void searchServerIdUsuario() {

        String user = UPreferencias.ObtenerUserLogeo(getContext());

        // TODO DINAMICO user
//        user = "JEEV";

        String url = String.format(SrvCmacIca.GET_ID_USUARIO_ANALISTA, user);


        _progressDialog = ProgressDialog.show(getContext()
                ,getString(R.string.registro_referido_msg_esperar), getString(R.string.registro_referido_msg_obtener_id_usuario)
        );

        VolleySingleton.getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerIdUser(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerIdUser(JSONObject response) {

//       _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
//               Toast.makeText(getContext(), getString(R.string.registro_referido_msg_id_usuario_obtenido_exitosamente), Toast.LENGTH_SHORT).show();

                JSONArray data = response.getJSONArray("Data");
                JSONObject jsonUser = data.getJSONObject(0);

                if(jsonUser.length() == 0){
                    Toast.makeText(getContext(), getString(R.string.registro_referido_error_obtener_id_usuario), Toast.LENGTH_LONG).show();
                } else {

                    AgendaComercialPreferences.setIdUser(
                            getContext(),
                            jsonUser.getInt("IdUsuario")
                    );
                    searchServerProductsOffered();
                }

            } else {
                Toast.makeText(getContext(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }

            //enableRegistry(true);

        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }



    private void searchServerProductsOffered() {

        int idUser = AgendaComercialPreferences.getIdUser(getContext());

        String url = String.format(SrvCmacIca.GET_PRODUCTOS_OFRECIDOS_ANALISTA,  idUser) ;

//        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.agenda_comercial_msg_esperar), getString(R.string.agenda_comercial_msg_obtener_productos_ofrecidos));

        VolleySingleton.getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerProductsOffered(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );


    }

    private void responseServerProductsOffered(JSONObject response) {

//        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")) {
                JSONArray jsonResults = response.getJSONArray("Data");

                if(jsonResults.length() == 0){
                    Toast.makeText(getActivity(), getString(R.string.agenda_comercial_error_obtener_productos_ofrecidos), Toast.LENGTH_LONG).show();
                } else {
                    insertProductsOffered(jsonResults);
                }
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void insertProductsOffered(JSONArray jsonProductsOffered) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteAllProduct();
                return _agendaComercialDbHelper.insertProduct(jsonProductsOffered);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    searchServerTypeOfContact();
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.agenda_comercial_error_guardar_productos_ofrecidos), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }





    private void searchServerTypeOfContact() {

        String url = SrvCmacIca.GET_TIPOS_CONTACTO;

//        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.agenda_comercial_msg_esperar), getString(R.string.agenda_comercial_msg_obtener_tipos_contacto));

        VolleySingleton.getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerTypeOfContract(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerTypeOfContract(JSONObject response) {

//        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")) {
                JSONArray jsonResults = response.getJSONArray("Data");

                if(jsonResults.length() == 0){
                    Toast.makeText(getActivity(), getString(R.string.agenda_comercial_error_obtener_tipos_contacto), Toast.LENGTH_LONG).show();
                } else {
                    insertTypeOfContract(jsonResults);
                }
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void insertTypeOfContract(JSONArray jsonTypeOfContract) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteAllContactType();
                return _agendaComercialDbHelper.insertContactType(jsonTypeOfContract);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    searchServerResultPresencial();
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.agenda_comercial_error_guardar_tipos_contacto), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }





    private void searchServerResultPresencial() {

        int idUser = AgendaComercialPreferences.getIdUser(getContext());

        if (idUser == 0) {
            Toast.makeText(getContext(), getString(R.string.agenda_comercial_error_obtener_id_user), Toast.LENGTH_SHORT).show();
            return;
        }

        String url = String.format(SrvCmacIca.GET_RESULTADOS_VISITA, idUser, Constantes.TYPE_CONTACT_FACE_FACE)  ;

//        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.resultado_msg_esperar), getString(R.string.resultado_msg_obteniendo_resultado_visita));

        VolleySingleton.getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerResultPresencial(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerResultPresencial(JSONObject response) {

        Log.d(TAG, "responseServerResultPresencial: " + response);
//        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")) {
                JSONArray jsonResults = response.getJSONArray("Data");

                if(jsonResults.length() == 0){
                    Toast.makeText(getActivity(), getString(R.string.agenda_comercial_error_obtener_resultados), Toast.LENGTH_LONG).show();
                } else {
                    insertResultsPresencial(jsonResults);
                }
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void insertResultsPresencial(JSONArray jsonResultsPresencial) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteaAllResultVisit(Constantes.TYPE_CONTACT_FACE_FACE);
                return _agendaComercialDbHelper.insertResultVisit(jsonResultsPresencial, Constantes.TYPE_CONTACT_FACE_FACE);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    //getVisit();
                    //searchServerVisit();
                    searchServerResultCall();
//                    Toast.makeText(getContext(), getString(R.string.resultado_msg_obteniendo_resultado_visita), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.resultado_error_guardar_resultados_visita), Toast.LENGTH_SHORT).show();
                }

            }
        };
        task.execute();

    }



    private void searchServerResultCall() {

        int idUser = AgendaComercialPreferences.getIdUser(getContext());

        if (idUser == 0) {
            Toast.makeText(getContext(), getString(R.string.agenda_comercial_error_obtener_id_user), Toast.LENGTH_SHORT).show();
            return;
        }

        String url = String.format(SrvCmacIca.GET_RESULTADOS_VISITA, idUser, Constantes.TYPE_CONTACT_CALL)  ;

//        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.resultado_msg_esperar), getString(R.string.resultado_msg_obteniendo_resultado_visita));

        VolleySingleton.getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerResultCall(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );


    }

    private void responseServerResultCall(JSONObject response) {

//        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")) {
                JSONArray jsonResults = response.getJSONArray("Data");

                if(jsonResults.length() == 0){
                    Toast.makeText(getActivity(), getString(R.string.agenda_comercial_error_obtener_resultados), Toast.LENGTH_LONG).show();
                } else {
                    insertResultsCall(jsonResults);
                }
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void insertResultsCall(JSONArray jsonResultsCall) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteaAllResultVisit(Constantes.TYPE_CONTACT_CALL);
                return _agendaComercialDbHelper.insertResultVisit(jsonResultsCall, Constantes.TYPE_CONTACT_CALL);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    //getVisit();
                    searchServerOffer();
//                    Toast.makeText(getContext(), getString(R.string.resultado_msg_obteniendo_resultado_visita), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.resultado_error_guardar_resultados_visita), Toast.LENGTH_SHORT).show();
                }

            }
        };
        task.execute();

    }


    private void searchServerOffer() {

        int idUser = AgendaComercialPreferences.getIdUser(getContext());

        if (idUser == 0) {
            Toast.makeText(getContext(), getString(R.string.agenda_comercial_error_obtener_id_user), Toast.LENGTH_SHORT).show();
            return;
        }

        String url = String.format(SrvCmacIca.GET_OFERTAS_CLIENTE, 2, idUser, "")  ;

//        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.agenda_comercial_msg_esperar), getString(R.string.agenda_comercial_msg_obtener_ofertas_cliente));

        VolleySingleton.getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerOffer(response),
                                error -> {
                                    _progressDialog.cancel();
                                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerOffer(JSONObject response) {

//        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")) {
                JSONArray jsonOffer = response.getJSONArray("Data");

                if(jsonOffer.length() == 0){
                    Toast.makeText(getActivity(), getString(R.string.agenda_comercial_error_obtener_ofertas_cliente), Toast.LENGTH_LONG).show();
                } else {
                    insertOffer(jsonOffer);
                }
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void insertOffer(JSONArray jsonOffer) {

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteAllOffers();
                return _agendaComercialDbHelper.insertOffers(jsonOffer);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    //getVisit();
                    searchServerVisit();
//                    Toast.makeText(getContext(), getString(R.string.resultado_msg_obteniendo_resultado_visita), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.resultado_error_guardar_resultados_visita), Toast.LENGTH_SHORT).show();
                }

            }
        };
        task.execute();

    }


    private void searchServerVisit() {


        int idUser = AgendaComercialPreferences.getIdUser(getContext());
//       idUser = 309;
        if (idUser == 0) {
            Toast.makeText(getContext(), getString(R.string.agenda_comercial_error_obtener_id_user), Toast.LENGTH_SHORT).show();
            return;
        }

        String url = String.format(SrvCmacIca.GET_VISITAS, idUser);

//        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.agenda_comercial_msg_esperar), getString(R.string.agenda_comercial_msg_obteniendo_visitas));

        VolleySingleton.getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServerVisit(response),
                                error -> {
                                    _progressDialog.cancel();
                                    showVisitNotFound( "Error: " + error.getMessage() );
                                    //Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        )
                );

    }

    private void responseServerVisit(JSONObject response) {

    _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")) {
                JSONArray jsonVisits = response.getJSONArray("Data");

                if(jsonVisits.length() == 0){
                    showVisitNotFound(getString(R.string.agenda_comercial_error_no_visitas));
                    //Toast.makeText(getActivity(), getString(R.string.cartera_analista_error_not_customers), Toast.LENGTH_LONG).show();
                } else {
                    insertVisits(jsonVisits);
                }
            } else {
                showVisitNotFound(response.getString("Message"));
                //Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            showVisitNotFound(ex.getMessage());
            //Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void insertVisits(JSONArray jsonVisits) {
        Log.d(TAG, "insertVisits: " + jsonVisits);

        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                _agendaComercialDbHelper.deleteAllVisitSynchronize();
                return _agendaComercialDbHelper.insertVisit(jsonVisits);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    Fragment fragment = getFragmentManager().findFragmentByTag(AgendaComercialFragment.class.getSimpleName());
                    getLoaderManager().initLoader(LOADER_VISITS, null, (AgendaComercialFragment)fragment);
                    Toast.makeText(getContext(), getString(R.string.agenda_comercial_msg_descarga_visita), Toast.LENGTH_SHORT).show();
                    //getProductsOffered();
                    _textinputedittextSearch.getText().clear();

                } else {
                    Toast.makeText(getContext(), getString(R.string.agenda_comercial_error_guardar_visitas), Toast.LENGTH_SHORT).show();
                    _textinputedittextSearch.getText().clear();
                }

            }
        };
        task.execute();

    }


    private void synchronizeResultVisit() {

        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.cartera_analista_msg_esperar), getString(R.string.cartera_analista_msg_sincronizando_clientes));


        try {

            Cursor cursor = _agendaComercialDbHelper.getAllResultSynchronize();
            List<ResultadoVisita> allResultVisit = _agendaComercialDbHelper.listResultVisitsSynchronize(cursor);

            if (allResultVisit!=null) {

                Log.d(TAG, "synchronizeResultVisit: " + allResultVisit.size());
                Gson gsonpojo = new GsonBuilder().create();
                String json = gsonpojo.toJson(allResultVisit);
                HashMap<String, String> cabeceras = new HashMap<>();

                Log.d(TAG, "synchronizeResultVisit: " + json);


                new RESTService(getActivity()).post(
                        // TODO DINAMICO modificar ruta
                        SrvCmacIca.POST_RESULTADOS_VISITA,
                        json,
                        response -> responseServerSynchronize(response),
                        error -> {
                            _progressDialog.cancel();
                            Toast.makeText(getActivity(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        },
                        cabeceras
                );

            } else {
                Log.d(TAG, "synchronizeResultVisit: error");
            }

        } catch (Exception e) {
            Log.d(TAG, "synchronizeResultVisit: " + e.getMessage());
            _progressDialog.cancel();
            Toast.makeText(getContext(), getString(R.string.cartera_analista_error_sincronizar_clientes), Toast.LENGTH_SHORT).show();
        }

    }


    private void responseServerSynchronize(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(getActivity(), "¡Clientes sincronizados correctamente!", Toast.LENGTH_SHORT).show();

                // TODO sincronizacion
                int countDeleteResult = 0;
                int countDeleteVisit = 0;
                countDeleteResult = _agendaComercialDbHelper.deleteAllResultVisitSynchronize();
                countDeleteVisit = _agendaComercialDbHelper.deleteAllVisitSynchronize();

                if (countDeleteResult > 0 && countDeleteVisit > 0) {
                    _flagSynchronize = false;
                    _buttonGetSynchronize.setText(getString(R.string.agenda_comercial_action_obtener));
                    searchServerIdUsuario();
                } else {
                    Toast.makeText(getContext(), "Ocurrio un error al eliminar los resultados", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    //endregion

}
