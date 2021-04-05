package pe.com.cmacica.flujocredito.ViewModel.GeoReferenciacion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.GeoRefClienteModel;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.LocalizacionModel;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.TipoDireccionModel;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.DatabaseHelper;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.GeoLocalizacion;

/**
 * by MFPE - 2019.
 */
public class RegisterGeoRefActivity extends AppCompatActivity {

    private TipoDireccionModel tipoDireccionSeleccionada;
    private EditText tvNombre;
    private EditText tvDoi;
    private EditText tvTelefono;
    private EditText tvReferencia;
    private Spinner spnTipoDireccion;
    private TextView tvLongitud;
    private TextView tvLatitud;
    private Button btnSaveLocation;
    private Button btnDeleteLocation;
    private Button btnValidateClient;
    private DatabaseHelper dbHelper;
    private boolean bValidado;
    GeoRefClienteModel clienteIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_geo_ref);
        showToolbar(getResources().getString(R.string.register_location));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        tvNombre = (EditText) findViewById(R.id.tv_register_name);
        tvDoi = (EditText) findViewById(R.id.tv_register_doi);
        tvTelefono = (EditText) findViewById(R.id.tv_register_phone);
        tvReferencia = (EditText) findViewById(R.id.tv_register_reference);
        spnTipoDireccion = (Spinner) findViewById(R.id.spnTipoDireccion);
        tvLongitud = (TextView) findViewById(R.id.tv_register_longitude);
        tvLatitud = (TextView) findViewById(R.id.tv_register_latitude);
        btnSaveLocation = (Button) findViewById(R.id.btnSaveLocation);
        btnDeleteLocation = (Button) findViewById(R.id.btnDeleteLocation);
        btnValidateClient = (Button) findViewById(R.id.btnValidateClient);

        ActivarGuardar(false);

        btnValidateClient.setOnClickListener(v -> {
            ActivarGuardar(true);
            BuscarCliente();
        });

        btnDeleteLocation.setOnClickListener(v -> EliminarLocalizacion());
        btnSaveLocation.setOnClickListener(v -> GuardarLocalizacion());
        spnTipoDireccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoDireccionSeleccionada = (TipoDireccionModel) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        dbHelper = new DatabaseHelper(this, "DBCMACICA", null, 1);

        tvDoi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ActivarGuardar(false);
                tvTelefono.setText("");
                tvTelefono.setFocusable(true);
                tvTelefono.setFocusableInTouchMode(true);
                tvNombre.setText("");
                tvNombre.setFocusable(true);
                tvNombre.setFocusableInTouchMode(true);
                tvReferencia.setText("");
                tvReferencia.setFocusable(true);
                tvReferencia.setFocusableInTouchMode(true);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        ProcesarIntent();
        CargarTiposDireccion();
        CargarLocalizacion();
    }

    private void EliminarLocalizacion(){
        if (clienteIntent != null){
            boolean rpta;
            rpta = dbHelper.EliminarCliente(clienteIntent);

            if (rpta){
                Toast.makeText(this,"Registro eliminado correctamente.",Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this,"Hubo un problema al eliminar el registro.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ProcesarIntent(){
        Intent i = getIntent();
        clienteIntent = (GeoRefClienteModel)i.getSerializableExtra("clienteSeleccionado");
        if (clienteIntent != null){
            tvDoi.setText(clienteIntent.getDoi());
            tvDoi.setFocusable(false);
            tvTelefono.setText(clienteIntent.getTelefono());
            tvTelefono.setFocusable(false);
            tvReferencia.setText(clienteIntent.getReferencia());
            //tvReferencia.setFocusable(false);
            tvNombre.setText(clienteIntent.getNombres());
            tvNombre.setFocusable(false);
            btnSaveLocation.setText("ACTUALIZAR UBICACIÓN");
            ActivarGuardar(true);
            btnDeleteLocation.setVisibility(View.VISIBLE);
        }
    }

    private void CargarTiposDireccion(){
        ArrayList<TipoDireccionModel> arrayTipos = dbHelper.ListarTipoDireccion();

        ArrayAdapter<TipoDireccionModel> adapterSpinner;
        adapterSpinner = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, arrayTipos);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoDireccion.setAdapter(adapterSpinner);

        if (clienteIntent != null){
            int spinnerPosition = adapterSpinner.getPosition(new TipoDireccionModel(clienteIntent.getIdTipoDireccion(),clienteIntent.getTipoDireccion()));
            spnTipoDireccion.setSelection(spinnerPosition);
            spnTipoDireccion.setClickable(false);
            spnTipoDireccion.setEnabled(false);
        }
    }

    private void CargarLocalizacion(){
        GeoLocalizacion geoLocalizacion = new GeoLocalizacion();
        LocalizacionModel location = geoLocalizacion.GetActualPosition(this);

        DecimalFormat df = new DecimalFormat("##.########",new DecimalFormatSymbols(Locale.US));
        df.setMaximumFractionDigits(8);
        tvLongitud.setText(df.format(location.getLongitud()));
        tvLatitud.setText(df.format(location.getLatitud()));
    }

    private void GuardarLocalizacion(){
        if(tvNombre.getText().length() == 0){
            Toast.makeText(this, "Ingrese un nombre.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(tvDoi.getText().length() == 0 || tvDoi.getText().length() < 8){
            Toast.makeText(this, "Ingrese un Doi válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(tvTelefono.getText().length() == 0 || tvTelefono.getText().length() < 9){
            Toast.makeText(this, "Ingrese un teléfono celular de contacto.", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoRefClienteModel cliente = new GeoRefClienteModel();
        cliente.setNombres(tvNombre.getText().toString());
        cliente.setDoi(tvDoi.getText().toString());
        cliente.setTelefono(tvTelefono.getText().toString());
        cliente.setLongitud(Double.parseDouble(tvLongitud.getText().toString()));
        cliente.setLatitud(Double.parseDouble(tvLatitud.getText().toString()));
        cliente.setIdTipoDireccion(tipoDireccionSeleccionada.getId());
        cliente.setReferencia(tvReferencia.getText().toString());

        Boolean rpta = false;

        if (clienteIntent != null){
            rpta = dbHelper.ActualizarCliente(cliente);
        }
        else{
            rpta = dbHelper.GuardarCliente(cliente);
        }

        if (rpta){
            Toast.makeText(this,"Registro guardado correctamente.",Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this,"Hubo un problema al guardar el registro.",Toast.LENGTH_SHORT).show();
        }
    }

    private void showToolbar(String tittle) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void ActivarGuardar(boolean valid){
        bValidado = valid;
        btnSaveLocation.setEnabled(valid);
        btnValidateClient.setEnabled(!valid);
        btnSaveLocation.setBackgroundResource(valid ? R.color.colorPrimary : R.color.colorAccent);
        btnValidateClient.setBackgroundResource(valid ? R.color.colorAccent : R.color.colorPrimary);
    }

    private void BuscarCliente(){
        GeoRefClienteModel cliente = dbHelper.ObtenerClientePorDoi(tvDoi.getText().toString());
        if (cliente != null){
            Toast.makeText(this, "¡Cliente encontrado!", Toast.LENGTH_SHORT).show();
            tvNombre.setText(cliente.getNombres());
            tvNombre.setFocusable(false);
            tvTelefono.setText(cliente.getTelefono());
            tvTelefono.setFocusable(false);
        } else {
            Toast.makeText(this, "¡Cliente no registrado! Complete sus datos.", Toast.LENGTH_SHORT).show();
            tvNombre.setText("");
            tvNombre.setFocusable(true);
            tvNombre.setFocusableInTouchMode(true);
            tvTelefono.setText("");
            tvTelefono.setFocusable(true);
            tvTelefono.setFocusableInTouchMode(true);
            tvReferencia.setText("");
            tvReferencia.setFocusable(true);
            tvReferencia.setFocusableInTouchMode(true);
        }
    }

}