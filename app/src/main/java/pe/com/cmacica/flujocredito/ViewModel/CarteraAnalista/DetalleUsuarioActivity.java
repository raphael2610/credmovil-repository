package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;


import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Credito;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista.CreditoAdapter;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;

public class DetalleUsuarioActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "DetalleUsuarioActivity";
    public static final String EXTRA_CUSTOMER = "customer";
    public static final int UPDATE_CUSTOMER_REQUEST = 10;
    private static final int GET_CREDITS = 0;

    private Toolbar _toolbar;
    private TextView _textNameContent;
    private TextView _textIdentityDocumentContent;
    private TextView _textPhoneContent;
    private TextView _textGeoreferencedContent;
    private TextView _buttonEditPersonalInformation;
    private Button _buttonGeoreference;
    private RecyclerView _recyclerviewCredits;
    private ProgressBar _progressbarCredits;
    private ConstraintLayout _constraintlayoutCreditsNotFound;
    private TextView _textErrorTitle;
    private TextView _textErrorContent;

    private CarteraAnalistaDbHelper _carteraAnalistaDbHelper;
    private CreditoAdapter _creditoAdapter;
    private Cliente _cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_usuario);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _textNameContent = (TextView) findViewById(R.id.textNameContent);
        _textIdentityDocumentContent = (TextView) findViewById(R.id.textIdentityDocumentContent);
        _textPhoneContent = (TextView) findViewById(R.id.textPhoneContent);
        _textGeoreferencedContent = (TextView) findViewById(R.id.textGeoreferencedContent);
        _recyclerviewCredits = (RecyclerView) findViewById(R.id.recyclerviewCredits);
        _progressbarCredits = (ProgressBar) findViewById(R.id.progressbarCredits);
        _buttonEditPersonalInformation = (TextView) findViewById(R.id.buttonEditPersonalInformation);
        _buttonGeoreference = (Button) findViewById(R.id.buttonGeoreference);
        _constraintlayoutCreditsNotFound = (ConstraintLayout) findViewById(R.id.constraintlayoutCreditsNotFound);
        _textErrorTitle = (TextView) findViewById(R.id.textErrorTitle);
        _textErrorContent = (TextView) findViewById(R.id.textErrorContent);

        _buttonEditPersonalInformation.setOnClickListener(v -> navigateToEditarCliente());
        _buttonGeoreference.setOnClickListener(v -> navigateToGeoreferenciar());

        initToolbar();

        _carteraAnalistaDbHelper = new CarteraAnalistaDbHelper(getApplicationContext());
        initializeAndGetInformation();
        showCredit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Cursor cursor = _carteraAnalistaDbHelper.getAllAddressByCustomer(_cliente.getDoi());
        Log.d(TAG, "onStart: " + cursor.getCount());
        if (cursor.getCount() >= 1) {
            _textGeoreferencedContent.setText("SI");
        } else {
            _textGeoreferencedContent.setText("NO");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == UPDATE_CUSTOMER_REQUEST) {
            showCustomer(data.getParcelableExtra(EXTRA_CUSTOMER));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                return _carteraAnalistaDbHelper.getCredit(_cliente.getDoi());
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Credito> creditos = _carteraAnalistaDbHelper.listCredit(data);

        if (creditos.size() == 0) {
            // TODO 2 si no tiene creditos locales, busca en cmacica
            getCreditUser();
        } else {
            // TODO 4 carga los datos de creditos
            _progressbarCredits.setVisibility(View.GONE);
            _recyclerviewCredits.setVisibility(View.VISIBLE);
            setupRecyclerView(creditos);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    private void initToolbar() {

        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.detalle_usuario_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initializeAndGetInformation() {
        Boolean validar = getIntent().getParcelableExtra(EXTRA_CUSTOMER) instanceof Cliente;

        if (validar) {
            _cliente = getIntent().getParcelableExtra(EXTRA_CUSTOMER);
        }

        showCustomer(_cliente);
    }

    private void showCustomer(Cliente cliente) {
        if (cliente!=null) {
            _cliente = cliente;
            _textNameContent.setText(cliente.getNombre());
            _textIdentityDocumentContent.setText(cliente.getDoi());
            showPhone(cliente);
            //_textPhoneContent.setText(cliente.getTelefonoUno() + " / " + cliente.getTelefonoDos());

            if (cliente.getFlag() == CarteraAnalistaDbHelper.FLAG_INSERTADO) {
                _buttonEditPersonalInformation.setVisibility(View.VISIBLE);
            }


            _textGeoreferencedContent.setText(cliente.getGeoposicion());
            /*
            Cursor cursor = _carteraAnalistaDbHelper.getAllAddressByCustomer(_cliente.getDoi());
            if (cursor.getCount() >= 1) {
                _textGeoreferencedContent.setText("SI");
            } else {
                _textGeoreferencedContent.setText("NO");
            }
             */

        }
    }


    private void showPhone(Cliente cliente) {

        try {

            String phoneOne = cliente.getTelefonoUno() == null ? "" : cliente.getTelefonoUno().trim();
            String phoneTwo = cliente.getTelefonoDos() == null ? "" : cliente.getTelefonoDos().trim();

            if (phoneOne.isEmpty() == false && phoneTwo.isEmpty() == false) {
                _textPhoneContent.setText(String.format("%s/%s", cliente.getTelefonoUno(), cliente.getTelefonoDos()));
                return;
            }

            if (phoneOne.isEmpty() == false && phoneTwo.isEmpty() == true) {
                _textPhoneContent.setText(String.format("%s", cliente.getTelefonoUno()));
                return;
            }

            if (phoneOne.isEmpty() == true && phoneTwo.isEmpty() == false) {
                _textPhoneContent.setText(String.format("%s", cliente.getTelefonoDos()));
                return;
            }

            if (phoneOne.isEmpty() == true && phoneTwo.isEmpty() == true) {
                _textPhoneContent.setText(String.format("%s", phoneOne, phoneTwo));
                return;
            }

        } catch (Exception e) { }

    }

    private void setupRecyclerView(ArrayList<Credito> creditoList) {
        _creditoAdapter = new CreditoAdapter(creditoList);
        _recyclerviewCredits.setAdapter(_creditoAdapter);
    }


    private void navigateToGeoreferenciar() {
        Intent intent = new Intent(this, GeoreferenciarActivity.class);
        intent.putExtra(GeoreferenciarActivity.EXTRA_CUSTOMER, _cliente);
        startActivity(intent);
    }

    private void navigateToEditarCliente() {
        Intent intent = new Intent(this, EditarClienteActivity.class);
        intent.putExtra(EditarClienteActivity.EXTRA_CUSTOMER, _cliente);
        startActivityForResult(intent, UPDATE_CUSTOMER_REQUEST);
    }

    private void showCreditsNotFount(String content) {
        _constraintlayoutCreditsNotFound.setVisibility(View.VISIBLE);
        _textErrorContent.setText(content);

        _recyclerviewCredits.setVisibility(View.GONE);
    }


    private void showCredit() {
        // TODO 1 obtener los creditos locales
        getSupportLoaderManager().initLoader(GET_CREDITS, null, this);
    }

    private void getCreditUser() {

        String url = String.format(SrvCmacIca.GET_CREDITOS_CLIENTE, _cliente.getDoi());

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> responseServer(response),
                                error -> {
                                    _progressbarCredits.setVisibility(View.GONE);
                                    showCreditsNotFount("Error " + error.getMessage());
                                    //Toast.makeText(this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        )
                );

    }

    private void responseServer(JSONObject response) {

        _progressbarCredits.setVisibility(View.GONE);
        _recyclerviewCredits.setVisibility(View.VISIBLE);

        try {
            if(response.getBoolean("IsCorrect")){
                JSONArray jsonCreditos = response.getJSONArray("Data");

                if(jsonCreditos.length() == 0){
                    showCreditsNotFount(getString(R.string.detalle_usuario_msg_creditos_no_encontrados));
                    //Toast.makeText(this, getString(R.string.detalle_usuario_msg_creditos_no_encontrados), Toast.LENGTH_LONG).show();
                } else {

                    ArrayList<Credito> creditoList = new ArrayList<>();
                    for (int i = 0; i < jsonCreditos.length(); i++) {
                        JSONObject row = jsonCreditos.getJSONObject(i);

                        Credito credito = new Credito();
                        credito.setNombre(row.getString("Descripcion"));
                        credito.setNumero(row.getString("CtaCod"));
                        credito.setEstado(row.getString("ConsDescripcion"));
                        credito.setMonto(row.getDouble("Monto"));
                        creditoList.add(credito);
                        Log.d(TAG, "responseServer: " + credito.getNumero());
                    }

                    // TODO 3 insertamos en la base de datos locales los creditos
                    AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Object... objects) {
                            return _carteraAnalistaDbHelper.insertCredit(creditoList, _cliente.getDoi());
                        }

                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            // TODO 4 carga los datos de creditos
                            if (aBoolean) {
                                setupRecyclerView(creditoList);
                            } else {
                                showCreditsNotFount(getString(R.string.detalle_usuario_error_cargar_creditos));
                                //Toast.makeText(DetalleUsuarioActivity.this, getString(R.string.detalle_usuario_error_cargar_creditos), Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    task.execute();

                }
            } else {
                showCreditsNotFount(response.getString("Message"));
                //Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            showCreditsNotFount(e.getMessage());
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


}
