package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Text;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;

public class EditarClienteActivity extends AppCompatActivity {


    public static final String EXTRA_CUSTOMER = "customer";
    public static final int LOADER_GET_CUSTOMER = 0;

    private Toolbar _toolbar;
    private TextInputEditText _textinputedittextName;
    private TextInputEditText _textinputedittextDNI;
    private TextInputEditText _textinputedittextPhone;
    private Button _buttonEditClient;

    private Cliente _cliente;
    private CarteraAnalistaDbHelper _carteraAnalistaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cliente);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _textinputedittextName = (TextInputEditText) findViewById(R.id.textinputedittextName);
        _textinputedittextDNI = (TextInputEditText) findViewById(R.id.textinputedittextDNI);
        _textinputedittextPhone = (TextInputEditText) findViewById(R.id.textinputedittextPhone);
        _buttonEditClient = (Button) findViewById(R.id.buttonEditClient);

        _buttonEditClient.setOnClickListener(v -> updateCustomer());

        _carteraAnalistaHelper = new CarteraAnalistaDbHelper(this);
        initToolbar();
        initializeAndGetInformation();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.editar_cliente_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeAndGetInformation() {
        Boolean validar = getIntent().getParcelableExtra(EXTRA_CUSTOMER)instanceof Cliente;

        if (validar) {
            _cliente = getIntent().getParcelableExtra(EXTRA_CUSTOMER);
        }

        showCustomer(_cliente);
    }

    private void showCustomer(Cliente cliente) {

        if (cliente!=null) {
            _textinputedittextName.setText(cliente.getNombre());
            _textinputedittextDNI.setText(cliente.getDoi());
            _textinputedittextPhone.setText(cliente.getTelefono());
        }

    }

    private void updateCustomer() {

        if (_textinputedittextName.getText().toString().length()==0) {
            Toast.makeText(this, getString(R.string.editar_cliente_msg_nombre), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextPhone.getText().toString().length() < 9) {
            Toast.makeText(this, getString(R.string.editar_cliente_msg_telefono), Toast.LENGTH_SHORT).show();
            return;
        }

        /*
        if (_textinputedittextDNI.getText().toString().length() < 8){
            Toast.makeText(this, getString(R.string.editar_cliente_msg_dni), Toast.LENGTH_SHORT).show();
            return;
        }
         */

        Cliente cliente = new Cliente();
        cliente.setId(_cliente.getId());
        cliente.setNombre(_textinputedittextName.getText().toString());
        cliente.setDoi(_cliente.getDoi());
        cliente.setTelefono(_textinputedittextPhone.getText().toString());
        cliente.setFlag(_cliente.getFlag());


        AsyncTask task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... objects) {
                return _carteraAnalistaHelper.updateCustomer(cliente);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent();
                    intent.putExtra(DetalleUsuarioActivity.EXTRA_CUSTOMER, cliente);
                    setResult(DetalleUsuarioActivity.UPDATE_CUSTOMER_REQUEST, intent);
                    Toast.makeText(EditarClienteActivity.this, getString(R.string.editar_cliente_msg_actualizacion_exitosa), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditarClienteActivity.this, getString(R.string.editar_cliente_error_actualizacion), Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute();

    }


}
