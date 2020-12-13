package pe.com.cmacica.flujocredito.ViewModel.CarteraAnalista;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;


public class FiltroBusquedaFragment extends DialogFragment {


    private ImageView _imageClose;
    private ImageView _imageCheck;
    private Button _buttonCurrentAnalyst;
    private Button _buttonCmacica;
    private TextView _textRadioContent;
    private AppCompatSeekBar _seekbarRadio;

    private int _radio;
    private int _tipoCartera;
    private boolean _buttonCmacicaSeleccionado = false;


    public FiltroBusquedaFragment() { }

    public static FiltroBusquedaFragment newInstance(String param1, String param2) {
        FiltroBusquedaFragment fragment = new FiltroBusquedaFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Theme_FlujoCredito);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_filtro_busqueda, container, false);

        _imageClose = (ImageView) root.findViewById(R.id.imageClose);
        _imageCheck = (ImageView) root.findViewById(R.id.imageCheck);
        _buttonCurrentAnalyst = (Button) root.findViewById(R.id.buttonCurrentAnalyst);
        _buttonCmacica = (Button) root.findViewById(R.id.buttonCmacica);
        _textRadioContent = (TextView) root.findViewById(R.id.textRadioContent);
        _seekbarRadio = (AppCompatSeekBar) root.findViewById(R.id.seekbarRadio);

        getPreferences();
        listenerView();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        } catch (Exception e) {}
    }


    private void listenerView() {

        _imageCheck.setOnClickListener(v -> saveFilterSearch() );
        _imageClose.setOnClickListener(v -> dismiss());

        _seekbarRadio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _radio = progress;
                _textRadioContent.setText(progress + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        _buttonCurrentAnalyst.setOnClickListener(v -> buttonSelected(v));
        _buttonCmacica.setOnClickListener(v -> buttonSelected(v));

    }

    private void buttonSelected(View view) {

        if (view.getId() == R.id.buttonCurrentAnalyst) {
            _buttonCmacicaSeleccionado = false;
            _buttonCurrentAnalyst.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            _buttonCurrentAnalyst.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

            _buttonCmacica.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            _buttonCmacica.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.outlined_button));
        } else {
            _buttonCmacicaSeleccionado = true;
            _buttonCurrentAnalyst.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            _buttonCurrentAnalyst.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.outlined_button));

            _buttonCmacica.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            _buttonCmacica.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }

    }

    private void getPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE);

        _radio = sharedPreferences.getInt(Constantes.PREF_RADIO, 0);
        _tipoCartera = sharedPreferences.getInt(Constantes.PREF_TIPO_CARTERA, 0);

        _textRadioContent.setText(String.valueOf(_radio));
        _seekbarRadio.setProgress(_radio);

        if (_tipoCartera == Constantes.OTROS) {
            buttonSelected(_buttonCmacica);
        } else {
            buttonSelected(_buttonCurrentAnalyst);
        }

    }

    private void insertPreferences(int radio, int tipoCartera) {

        SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences(Constantes.SHARED_PREF_ANALISTA, Context.MODE_PRIVATE);

        sharedPreferences.edit().putInt(Constantes.PREF_RADIO, radio).apply();
        sharedPreferences.edit().putInt(Constantes.PREF_TIPO_CARTERA, tipoCartera).apply();

    }

    private void saveFilterSearch() {
        insertPreferences(_radio, _buttonCmacicaSeleccionado==true ? Constantes.OTROS : Constantes.ACTUAL_ANALISTA);
    }

}
