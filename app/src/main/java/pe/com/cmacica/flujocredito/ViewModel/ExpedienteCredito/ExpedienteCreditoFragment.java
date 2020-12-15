package pe.com.cmacica.flujocredito.ViewModel.ExpedienteCredito;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Cliente;
import pe.com.cmacica.flujocredito.R;


public class ExpedienteCreditoFragment extends Fragment {


    private EditText _edittextDni;
    private ImageView _appcompatimageviewSearch;
    private ImageView _appcompatimageviewAdd;
    private EditText _edittextNameAndLastname;
    private EditText _edittextTypePerson;
    private Button _buttonConsult;
    private ProgressDialog _progressDialog;
    private Cliente _client;



    // region lifeycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expediente_credito, container, false);

        _edittextDni = (EditText) view.findViewById(R.id.edittextDni);
        _appcompatimageviewSearch = (ImageView) view.findViewById(R.id.appcompatimageviewSearch);
        _appcompatimageviewAdd = (ImageView) view.findViewById(R.id.appcompatimageviewAdd);
        _edittextNameAndLastname = (EditText) view.findViewById(R.id.edittextNameAndLastname);
        _edittextTypePerson = (EditText) view.findViewById(R.id.edittextTypePerson);
        _buttonConsult = (Button) view.findViewById(R.id.buttonConsult);
        _client = new Cliente();
        setupView();

        return view;
    }

    // endregion



    // region setupView

    private void setupView() {
        _appcompatimageviewSearch.setOnClickListener(view -> { searchServerCustomerInformation(); });
        _buttonConsult.setOnClickListener(view -> { navigateToListadoCreditos(); });
    }

    // endregion



    // region navigation

    private void navigateToListadoCreditos() {
        Intent intent = new Intent(getContext(), ListadoCreditosActivity.class);
        intent.putExtra(ListadoCreditosActivity.EXTRA_CLIENT, _client);
        startActivity(intent);
    }

    // endregion



    // region network

    private void searchServerCustomerInformation() {

        if (_edittextDni.length() < 8 ){
            Toast.makeText(getContext(), getString(R.string.expediente_credito_error_dni), Toast.LENGTH_SHORT).show();
            _progressDialog.cancel();
            return;
        }

        _progressDialog = ProgressDialog.show(getContext(), getString(R.string.expediente_credito_msg_esperar), getString(R.string.expediente_credito_obtener_informacion_cliente));

        String url = String.format(SrvCmacIca.GET_INFORMACION_CLIENTE, _edittextDni.getText().toString());

        VolleySingleton.getInstance(getContext())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> {
                                    responseServerCustomerInformation(response);
                                },
                                error -> {
                                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        )
                );

    }

    private void responseServerCustomerInformation(JSONObject response) {

        _progressDialog.cancel();

        try {

            if (response.getBoolean("IsCorrect")) {

            } else {
                Toast.makeText(getContext(), response.getString("Message"), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    // endregion


}