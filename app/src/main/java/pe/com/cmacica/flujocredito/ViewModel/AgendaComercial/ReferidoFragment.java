package pe.com.cmacica.flujocredito.ViewModel.AgendaComercial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteReferido;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial.ClienteReferidoAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;


public class ReferidoFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = "ReferidoFragment";
    public static final int LOADER_REFERRED = 1;


    private Button _buttonSynchronize;
    private RecyclerView _recyclerviewReferred;
    private ProgressDialog _progressDialog;

    private ClienteReferidoAdapter _clienteReferidoAdapter;
    private AgendaComercialDbHelper _agendaComercialDbHelper;

    //region lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_referido, container, false);

        _recyclerviewReferred = (RecyclerView) view.findViewById(R.id.recyclerviewReferred);
        _buttonSynchronize = (Button) view.findViewById(R.id.buttonSynchronize);

        _agendaComercialDbHelper = new AgendaComercialDbHelper(getContext());
        listenerView();
        setupRecyclerView();
        searchLocalReferred();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        searchLocalReferred();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_referido, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_add) {
            navigateToRegisterReferred();
        }

        return super.onOptionsItemSelected(item);
    }

    // endregion


    // region ui controller

    private void listenerView() {

        _buttonSynchronize.setOnClickListener( v -> synchronizeReferred() );

    }

    private void setupRecyclerView() {

        _clienteReferidoAdapter = new ClienteReferidoAdapter(new ArrayList<>(), getContext());
        _recyclerviewReferred.setAdapter(_clienteReferidoAdapter);

    }


    // endregion


    // region callback

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext()) {
            @Override
            public Cursor loadInBackground() {
                return _agendaComercialDbHelper.getAllReferred();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        List<ClienteReferido> clienteReferidoList = _agendaComercialDbHelper.listReferred(data);
        _clienteReferidoAdapter.updateList(clienteReferidoList);

        getLoaderManager().destroyLoader(LOADER_REFERRED);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    // endregion



    // region dataLocal

    private void searchLocalReferred() {
        getLoaderManager().initLoader(LOADER_REFERRED, null, this);
    }

    // endregion


    // region navigation
    private void navigateToRegisterReferred() {

        startActivity(
                new Intent(getActivity(), RegistroReferidoActivity.class)
        );

    }
    // endregion


    // region network

    private void synchronizeReferred() {

        Log.d(TAG, "synchronizeReferred: ");
        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.registro_referido_msg_esperar), getString(R.string.registro_referido_msg_sincronizando_referidos));


        try {

            Cursor cursor = _agendaComercialDbHelper.getAllReferred(AgendaComercialDbHelper.FLAG_NOT_SYNCRONIZED);
            List<ClienteReferido> allReferred = _agendaComercialDbHelper.listReferred(cursor);

            if (allReferred!=null) {

                Gson gsonpojo = new GsonBuilder().create();
                String json = gsonpojo.toJson(allReferred);
                HashMap<String, String> cabeceras = new HashMap<>();
                Log.d(TAG, "synchronizeReferred: " + json);


                new RESTService(getActivity()).post(
                        SrvCmacIca.POST_REFERIDOS,
                        json,
                        response -> responseServerSynchronize(response),
                        error -> {
                            _progressDialog.cancel();
                            Toast.makeText(getActivity(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        },
                        cabeceras
                );

            } else {
                Log.d(TAG, "synchronizeReferred: error");
            }

        } catch (Exception e) {
            _progressDialog.cancel();
            Toast.makeText(getContext(), getString(R.string.registro_referido_error_sincronizacion_referidos), Toast.LENGTH_SHORT).show();
        }

    }

    private void responseServerSynchronize(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(getActivity(), "¡Clientes referidos guardados correctamente!", Toast.LENGTH_SHORT).show();

                boolean valor = false;
                valor = _agendaComercialDbHelper.updateReferredSynchronizationStatus();
                searchLocalReferred();
                Log.d(TAG, "responseServerSynchronize: " + valor);
                _agendaComercialDbHelper.deleteAllReferidosSynchronize();
            } else {
                Log.d(TAG, "responseServerSynchronize: " + response.getString("Message"));
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    // endregion


}
