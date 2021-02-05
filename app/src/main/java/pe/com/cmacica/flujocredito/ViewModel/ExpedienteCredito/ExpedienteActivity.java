package pe.com.cmacica.flujocredito.ViewModel.ExpedienteCredito;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Expediente;
import pe.com.cmacica.flujocredito.R;

public class ExpedienteActivity extends AppCompatActivity {

    private static final String TAG = "ExpedienteActivity";
    public static final String EXTRA_FILE = "file";

    private Toolbar _toolbar;
    private ImageView _imageFile;
    private Button _buttonAccept;
    private Expediente _file;

    // region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expediente2);

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _imageFile = (ImageView) findViewById(R.id.imageFile);
        _buttonAccept = (Button) findViewById(R.id.buttonAccept);

        initializeInformation();
        setupView();
    }

    // endregion


    // region setupView

    private void setupView() {

        initToolbar();

        try {
            byte[] decodedString = Base64.decode(_file.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            _imageFile.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        _buttonAccept.setOnClickListener(view -> {
            finish();
        });

    }


    private void initToolbar() {
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(getString(R.string.expediente_one_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeInformation() {

        try {
            _file = getIntent().getParcelableExtra(EXTRA_FILE);
        } catch (Exception e) {}

    }

    // endregion


}