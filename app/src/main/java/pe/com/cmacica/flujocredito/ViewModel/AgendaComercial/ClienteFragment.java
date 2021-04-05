package pe.com.cmacica.flujocredito.ViewModel.AgendaComercial;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;


public class ClienteFragment extends Fragment
        implements RegistroResultadoActivity.RegistrarResultadolistener {

    private static final String TAG = "ClienteFragment";
    public static final String EXTRA_VISIT = "visit";

    private TextInputEditText _textinputedittextDNI;
    private TextInputEditText _textinputedittextAge;
    private TextInputEditText _textinputedittextSex;
    private TextInputEditText _textinputedittextName;
    private TextInputEditText _textinputedittextPhone;
    private TextInputEditText _textinputedittextAgency;
    private TextInputEditText _textinputedittextAddress;
    private ClienteVisita _clienteVisita;
    private Button _buttonSeeOffers;
    private Button _buttonUpdateData;

    private AgendaComercialDbHelper _agenciaComercialDbHelper;



    // region lifecycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeAndGetInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cliente, container, false);

        _textinputedittextDNI = (TextInputEditText) view.findViewById(R.id.textinputedittextDNI);
        _textinputedittextAge = (TextInputEditText) view.findViewById(R.id.textinputedittextAge);
//        _textinputedittextSex = (TextInputEditText) view.findViewById(R.id.textinputedittextSex);
        _textinputedittextName = (TextInputEditText) view.findViewById(R.id.textinputedittextName);
        _textinputedittextPhone = (TextInputEditText) view.findViewById(R.id.textinputedittextPhone);
        _textinputedittextAgency = (TextInputEditText) view.findViewById(R.id.textinputedittextAgency);
        _textinputedittextAddress = (TextInputEditText) view.findViewById(R.id.textinputedittextAddress);
        _buttonSeeOffers = (Button) view.findViewById(R.id.buttonSeeOffers);
        _buttonUpdateData = (Button) view.findViewById(R.id.buttonUpdateData);

        _agenciaComercialDbHelper = new AgendaComercialDbHelper(getContext());
        showClientVisit();
        listenerView();

        return view;
    }

    public static ClienteFragment newInstance(ClienteVisita clienteVisita) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_VISIT, clienteVisita);
        ClienteFragment fragment = new ClienteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // endregion



    // region callback

    @Override
    public void onClick() {
        Log.d(TAG, "onClick: ");

        // TODO 1 - validamos cada campo
        if (_textinputedittextName.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), getString(R.string.cliente_msg_nombre), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextPhone.getText().toString().trim().length() == 9) {
            Toast.makeText(getContext(), getString(R.string.cliente_msg_telefono), Toast.LENGTH_SHORT).show();
            return;
        }

        if (_textinputedittextAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), getString(R.string.cliente_msg_direccion), Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO 2 - actualizacion del cliente local
        _clienteVisita.setDni(_textinputedittextDNI.getText().toString());
        // edad
        // sexo
        _clienteVisita.setNombres(_textinputedittextName.getText().toString());
        _clienteVisita.setTelefono(_textinputedittextPhone.getText().toString());
        // agencia
        _clienteVisita.setDireccion(_textinputedittextAddress.getText().toString());
        _clienteVisita.setFlag(AgendaComercialDbHelper.FLAG_ACTUALIZADO);


        AsyncTask task = new AsyncTask<Object, Void, Integer>() {
            @Override
            protected Integer doInBackground(Object... objects) {
                return _agenciaComercialDbHelper.updateDataClient(_clienteVisita);
            }

            @Override
            protected void onPostExecute(Integer integer) {

                if (integer == 1) {
                    Toast.makeText(getContext(), getString(R.string.cliente_msg_actualizacion), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.cliente_error_actualizacion), Toast.LENGTH_SHORT).show();
                }

            }
        };

        task.execute();

    }

    // endregion



    // region ui controller

    private void initializeAndGetInformation() {

        try {
            _clienteVisita = getArguments().getParcelable(EXTRA_VISIT);
        } catch (Exception e) { }

    }


    private void listenerView() {

        _buttonSeeOffers.setOnClickListener( v -> navigateToOfertasCliente() );
        _buttonUpdateData.setOnClickListener( v -> navigateToActualizarDatosCliente() );

    }

    private void showClientVisit() {

        if (_clienteVisita!=null) {
            _textinputedittextDNI.setText(_clienteVisita.getDni());
            _textinputedittextAge.setText( String.valueOf( _clienteVisita.getEdad() ) );
//            _textinputedittextSex.setText(" ");
            _textinputedittextName.setText(_clienteVisita.getNombres());
            _textinputedittextPhone.setText(_clienteVisita.getTelefono());
            _textinputedittextAgency.setText(_clienteVisita.getDescAgencia());
            _textinputedittextAddress.setText(_clienteVisita.getDireccion());
        }

    }

    // endregion


    // region navigation

    private void navigateToOfertasCliente() {

        Intent Ofertas = new Intent(getActivity(),OfertasClienteActivity.class);
        Ofertas.putExtra("visit", _clienteVisita);
        startActivity(
                Ofertas
        );


    }

    private void navigateToActualizarDatosCliente() {
        Intent Ofertas = new Intent(getActivity(),ActualizarDatosClienteActivity.class);
        Ofertas.putExtra("visit", _clienteVisita);
        startActivity(
                Ofertas
        );
//        startActivity(
//                new Intent(getActivity(), ActualizarDatosClienteActivity.class)
//        );

    }

    //private void

    // endregion



}
