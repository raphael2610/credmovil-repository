package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Direccion;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista.DireccionAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaContract;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaPreferences;

public class GeoreferenciarActivity extends AppCompatActivity
        implements DireccionAdapter.DireccionAdapterListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "GeoreferenciarActivity";
    public static final String EXTRA_CUSTOMER = "customer";
    public static final int LOADER_ADDRESS = 0;
    public static final int FLAG_INSERT_ADDRESS = 1;
    public static final int FLAG_UPDATE_ADDRESS = 2;

    private Toolbar _toolbar;
    private FloatingActionButton _floatingactionbuttonAddAddress;
    private RecyclerView _recyclerviewGeoreference;
    private ConstraintLayout _constraintlayoutAddressesNotFound;
    private DireccionAdapter _direccionAdapter;

    private CarteraAnalistaDbHelper _carteraAnalistaDbHelper;
    private ArrayList<Cliente> _clienteArrayList;
    private Cliente _cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_georeferenciar);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _floatingactionbuttonAddAddress = (FloatingActionButton) findViewById(R.id.floatingactionbuttonAddAddress);
        _recyclerviewGeoreference = (RecyclerView) findViewById(R.id.recyclerviewGeoreference);
        _constraintlayoutAddressesNotFound = (ConstraintLayout) findViewById(R.id.constraintlayoutAddressesNotFound);

        _floatingactionbuttonAddAddress.setOnClickListener(v -> navigateToAnadeDireccion(FLAG_INSERT_ADDRESS, _cliente));

        _carteraAnalistaDbHelper = new CarteraAnalistaDbHelper(this);
        initToolbar();
        setupRecyclerView();
        initializeAndGetInformation();


    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().initLoader(LOADER_ADDRESS, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                return _carteraAnalistaDbHelper.getAllAddressByCustomer(_cliente.getDoi());
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ArrayList<Cliente> clienteArrayList = _carteraAnalistaDbHelper.listCustomers(data);

        if (clienteArrayList.size() >= 1) {
            _constraintlayoutAddressesNotFound.setVisibility(View.GONE);
            _recyclerviewGeoreference.setVisibility(View.VISIBLE);
        } else {
            _constraintlayoutAddressesNotFound.setVisibility(View.VISIBLE);
            _recyclerviewGeoreference.setVisibility(View.GONE);
        }

        if (clienteArrayList.size() == 2 ||
            (_cliente.getFlag()==CarteraAnalistaDbHelper.FLAG_OTRAS_CARTERAS_ANALISTA && _cliente.getCreditos().toUpperCase().equals("SI"))
        ){
            visibleButton(false);
        } else {
            visibleButton(true);
        }

        _direccionAdapter.updateList(clienteArrayList);
        getSupportLoaderManager().destroyLoader(LOADER_ADDRESS);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }


    @Override
    public void onClick(View view, Cliente cliente) {

        switch (view.getId()) {
            case R.id.buttonVisualize:
                navigateToMapaGeoreferenciacion(cliente);
                break;
            case R.id.buttonEdit:
                navigateToAnadeDireccion(FLAG_UPDATE_ADDRESS, cliente);
                break;
        }

    }


    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle(R.string.georeferenciar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeAndGetInformation() {
        Boolean validador = getIntent().getParcelableExtra(EXTRA_CUSTOMER) instanceof Cliente;

        if (validador) {
            _cliente = getIntent().getParcelableExtra(EXTRA_CUSTOMER);
        }

        // TODO validacion de otra cartera
        Log.d(TAG, "initializeAndGetInformation: " + _cliente.getFlag());
        if ( _cliente.getFlag() == CarteraAnalistaDbHelper.FLAG_OTRAS_CARTERAS_ANALISTA
            && _cliente.getCreditos().toUpperCase().equals("SI") ) {
            Log.d(TAG, "initializeAndGetInformation: button false");
            visibleButton(false);
        } else {
            Log.d(TAG, "initializeAndGetInformation: button true");
            visibleButton(true);
        }

        showMessageClientAnotherAnalyst();

    }

    private void setupRecyclerView() {
        _clienteArrayList = new ArrayList<>();
        _direccionAdapter = new DireccionAdapter(this, _clienteArrayList);
        _recyclerviewGeoreference.setAdapter(_direccionAdapter);
    }

    private void visibleButton(boolean visible) {

        if (visible) {
            _floatingactionbuttonAddAddress.setVisibility(View.VISIBLE);
        } else {
            _floatingactionbuttonAddAddress.setVisibility(View.GONE);
        }

    }


    private void showMessageClientAnotherAnalyst() {

        if (_cliente.getFlag() == CarteraAnalistaDbHelper.FLAG_OTRAS_CARTERAS_ANALISTA && _cliente.getCreditos().equals("SI")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.georeferenciar_dialog_title));
            builder.setMessage(getString(R.string.georeferenciar_dialog_content));
            builder.setPositiveButton(R.string.georeferenciar_dialog_action_aceptar, null);
            builder.setCancelable(true);
            builder.create().show();
        }

    }

    private void navigateToAnadeDireccion(int flag, Cliente cliente) {
        Intent intent = new Intent(this, AnadeDireccionActivity.class);
        intent.putExtra(AnadeDireccionActivity.EXTRA_CUSTOMER, cliente);
        intent.putExtra(AnadeDireccionActivity.EXTRA_UPDATE_INSERT, flag);
        startActivity(intent);
    }

    private void navigateToMapaGeoreferenciacion(Cliente cliente) {

        Intent intent = new Intent(this, MapaGeoreferenciacionActivity.class);
        intent.putExtra(MapaGeoreferenciacionActivity.EXTRA_TYPE_MAP, MapaGeoreferenciacionActivity.TYPE_CUSTOMER);
        intent.putExtra(MapaGeoreferenciacionActivity.EXTRA_CUSTOMER, cliente);
        startActivity(intent);

    }

}
