package pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Cliente;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.TipoExpediente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.AppUtil;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.UPreferencias;
import pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito.Manager.UploadImageWorker;

public class AddExpedienteActivity extends AppCompatActivity {

    private static final String TAG = "AddExpedienteActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String EXTRA_CREDIT = "credit";
    public static final String EXTRA_CONFIGURATION = "configuration";
    public static final String EXTRA_CLIENT = "client";

    private Toolbar _toolbar;
    private ImageView _imageExpediente;
    private Button _buttonTakePhoto;
    private Button _buttonSave;
    private ProgressDialog _progressDialog;
    private Spinner _spinnerType;
    private Credito _credit;
    private Cliente _client;
    private int _configuration = 0;
    private String _user = "";

    private Bitmap bitmapImage;



    // region lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expediente);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _imageExpediente = (ImageView) findViewById(R.id.imageExpediente);
        _buttonTakePhoto = (Button) findViewById(R.id.buttonTakePhoto);
        _buttonSave = (Button) findViewById(R.id.buttonSave);
        _spinnerType = (Spinner) findViewById(R.id.spinnerType);

        setupView();
    }
    // endregion



    // region setupView

    private void setupView() {
        initToolbar();
        initializeAndGetInformation();
        _buttonTakePhoto.setOnClickListener(view -> {
                dispatchTakePictureIntent();
        });
        _buttonSave.setOnClickListener(view -> saveFile());
    }

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.add_expediente_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeAndGetInformation() {

        try {
            _credit = getIntent().getParcelableExtra(EXTRA_CREDIT);
            _configuration = getIntent().getIntExtra(EXTRA_CONFIGURATION, 0);
            _client = getIntent().getParcelableExtra(EXTRA_CLIENT);
            _user = UPreferencias.ObtenerUserLogeo(this);
            searchServerTypes();
        } catch (Exception e) { }

    }

    // endregion


    // region callback

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bitmapImage = imageBitmap;
            _imageExpediente.setImageBitmap(imageBitmap);
        }
    }

    // endregion


    // region network

    private void searchServerTypes() {

        _progressDialog = ProgressDialog.show(this, getString(R.string.add_expediente_msg_esperar), getString(R.string.add_expediente_msg_obtener_tipos));

        String url = String.format(SrvCmacIca.GET_LISTADO_TIPOS_EXPEDIENTES, _configuration);

        VolleySingleton.getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> {
                                    responseServerTypes(response);
                                },
                                error -> {
                                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerTypes(JSONObject response) {

        _progressDialog.cancel();

        try {

            if (response.getBoolean("IsCorrect")) {

                JSONArray jsonTypes = response.getJSONArray("Data");

                if (jsonTypes.length() == 0) {
                    Toast.makeText(this, getString(R.string.add_expediente_error_not_types), Toast.LENGTH_SHORT).show();
                } else {
                    loadListTypes(jsonTypes);
                }

            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void loadListTypes(JSONArray jsonTypes) throws JSONException {

        List<TipoExpediente> typeFiles = new ArrayList<>();

        for (int i=0; i<jsonTypes.length(); i++) {

            JSONObject typeFile = jsonTypes.getJSONObject(i);

            TipoExpediente tipoExpediente = new TipoExpediente();
            tipoExpediente.setIdCar(typeFile.getInt("nIdCar"));
            tipoExpediente.setTypeName(typeFile.getString("NombreTipo"));
            tipoExpediente.setCodeType(typeFile.getInt("cTipoCod"));

            typeFiles.add(tipoExpediente);

        }

        ArrayAdapter<TipoExpediente> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, typeFiles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerType.setAdapter(adapter);

    }

    private void saveFile() {

        _progressDialog = ProgressDialog.show(this, getString(R.string.add_expediente_msg_esperar), getString(R.string.add_expediente_msg_obtener_tipos));

        try {

            String codigoCliente = _client.getPersonCode();
            String codigoCuenta = _credit.getNumberCredit();

            // convert bitmap to string
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            Bitmap bitmap = ( (BitmapDrawable) (_imageExpediente.getDrawable()) ).getBitmap();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] imageBytes = baos.toByteArray();
//            String imageString = Base64.encaleodeToString(imageBytes, Base64.DEFAULT);
            String imageString = AppUtil.compressImage(bitmapImage);

            String user = UPreferencias.ObtenerUserLogeo(this);
            user = "ERMN";

            TipoExpediente tipoExpediente = (TipoExpediente) (_spinnerType.getSelectedItem());
            int tipoCodigo = tipoExpediente.getCodeType();

//            JSONObject json = new JSONObject();
//
//            json.put("nIdCar", _configuration);
//            json.put("cPerscod", codigoCliente);
//            json.put("cCtaCod", codigoCuenta);
//            json.put("ccImagen", imageString);
//            json.put("Usuario", user);
//            json.put("cTipoCod", tipoExpediente.getCodeType());
//
//            HashMap<String, String> cabeceras = new HashMap<>();
//
//            new RESTService(this).post(
//                    SrvCmacIca.SAVE_EXPEDIENTE,
//                    json.toString(),
//                    response -> responseServerAddFile(response),
//                    error -> {
//                        _progressDialog.cancel();
//                        Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
//                    },
//                    cabeceras
//            );

            Constraints uploadImageConstraint = new Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data uploadImageData = new Data.Builder()
                    .putInt("idCar", _configuration)
                    .putString("codigoCliente", codigoCliente)
                    .putString("codigoCuenta", codigoCuenta)
//                    .putString("image", imageString)
                    .putString("user", user)
                    .putInt("tipoCodigo", tipoCodigo)
                    .build();

            getSharedPreferences(Constantes.SHARED_PREF_NUEVO_EXPEDIENTE_CREDITO, MODE_PRIVATE)
                        .edit()
                        .putString(Constantes.PREF_IMAGE_SERVER, imageString)
                        .commit();

            OneTimeWorkRequest uploadImageWorkRequest =
                    new OneTimeWorkRequest.Builder(UploadImageWorker.class)
                            .setBackoffCriteria(
                                    BackoffPolicy.LINEAR,
                                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                    TimeUnit.MILLISECONDS
                            )
                            .setConstraints(uploadImageConstraint)
                            .setInputData(uploadImageData)
                            .build();

            WorkManager.getInstance(this).enqueueUniqueWork(
                    UploadImageWorker.TAG,
                    ExistingWorkPolicy.KEEP,
                    uploadImageWorkRequest
            );

            _progressDialog.cancel();

            finish();

        } catch (Exception e) {
            _progressDialog.cancel();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void responseServerAddFile(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, "¡Expediente guardado correctamente!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    // endregion


    // region takeImage

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // endregion


}