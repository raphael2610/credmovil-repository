package pe.com.cmacica.flujocredito.ViewModel.ExpedienteCredito;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pe.com.cmacica.flujocredito.R;


public class ExpedienteCreditoFragment extends Fragment {


    private Button _buttonConsult;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expediente_credito, container, false);

        _buttonConsult = (Button) view.findViewById(R.id.buttonConsult);
        setupView();

        return view;
    }


    private void setupView() {
        _buttonConsult.setOnClickListener(view -> { navigateToListadoCreditos(); });
    }

    private void navigateToListadoCreditos() {
        Intent intent = new Intent(getContext(), ListadoCreditosActivity.class);
        startActivity(intent);
    }

}