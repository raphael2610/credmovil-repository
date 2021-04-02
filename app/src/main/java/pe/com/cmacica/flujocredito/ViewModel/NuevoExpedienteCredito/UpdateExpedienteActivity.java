package pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class UpdateExpedienteActivity extends AppCompatActivity {

    private static final String TAG = "UpdateExpedienteActivit";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String EXTRA_FILE = "file";
    public static final String EXTRA_CREDIT = "credit";
    public static final String EXTRA_CONFIGURATION = "configuration";
    public static final String EXTRA_CLIENT = "client";

    private Toolbar _toolbar;
    private ImageView _imageExpediente;
    private Button _buttonTakePhoto;
    private Button _buttonUpdate;
    private ProgressDialog _progressDialog;
    private Credito _credit;
    private Cliente _client;
    private int _configuration = 0;
    private String _user = "";
    private Expediente _expediente;


    // region lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expediente);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _imageExpediente = (ImageView) findViewById(R.id.imageExpediente);
        _buttonTakePhoto = (Button) findViewById(R.id.buttonTakePhoto);
        _buttonUpdate = (Button) findViewById(R.id.buttonUpdate);

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
        _buttonUpdate.setOnClickListener(view -> {
            saveFile();
        });

    }

    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(R.string.update_expediente_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeAndGetInformation() {

        try {
            _credit = getIntent().getParcelableExtra(EXTRA_CREDIT);
            _configuration = getIntent().getIntExtra(EXTRA_CONFIGURATION, 0);
            _client = getIntent().getParcelableExtra(EXTRA_CLIENT);
            _expediente = getIntent().getParcelableExtra(EXTRA_FILE);
            _user = UPreferencias.ObtenerUserLogeo(this);

            byte[] decodedString = Base64.decode(_expediente.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            _imageExpediente.setImageBitmap(bitmap);
        } catch (Exception e) { }

    }
    // endregion



    // region callback

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            _imageExpediente.setImageBitmap(imageBitmap);
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



    // region network

    private void saveFile() {

        _progressDialog = ProgressDialog.show(this, getString(R.string.update_expediente_msg_esperar), getString(R.string.update_expediente_msg_actualizar_expediente));

        try {

            int idFile = _expediente.getId();

            // convert bitmap to string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = ( (BitmapDrawable) (_imageExpediente.getDrawable()) ).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            String user = UPreferencias.ObtenerUserLogeo(this);
            user = "ERMN";

            JSONObject json = new JSONObject();

            json.put("nIdObj", idFile);
            json.put("ccImagen", imageString);
            json.put("Usuario", user);

            HashMap<String, String> cabeceras = new HashMap<>();

            new RESTService(this).post(
                    SrvCmacIca.UPDATE_EXPEDIENTE,
                    json.toString(),
                    response -> responseServerUpdateFile(response),
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

    private void responseServerUpdateFile(JSONObject response) {

        _progressDialog.cancel();

        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(this, "¡Documento actualizado correctamente!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    // endregion

}