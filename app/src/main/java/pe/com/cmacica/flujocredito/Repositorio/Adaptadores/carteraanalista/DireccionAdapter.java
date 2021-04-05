package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.Model.carteraanalista.Direccion;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;

public class DireccionAdapter extends RecyclerView.Adapter<DireccionAdapter.ViewHolder> {


    private static final String TAG = "DireccionAdapter";
    private List<Cliente> _customers;
    private DireccionAdapterListener _direccionAdapterListener;

    public DireccionAdapter(DireccionAdapterListener direccionAdapterListener, ArrayList<Cliente> customers) {
        this._customers = customers;
        this._direccionAdapterListener = direccionAdapterListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_direccion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(_customers.get(position), _direccionAdapterListener);
    }

    @Override
    public int getItemCount() {
        return _customers.size();
    }


    public interface DireccionAdapterListener {
        void onClick(View view, Cliente cliente);
    }

    public void updateList(List<Cliente> newsCustomers) {

        DiffUtil.Callback callback = new ClienteDiffCallback(_customers, newsCustomers);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._customers.clear();
        this._customers.addAll(newsCustomers);
        result.dispatchUpdatesTo(this);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView _textHomeAddress;
        TextView _textReferenceContent;
        TextView _textLatitudeContent;
        TextView _textLongitudeContent;
        Button _buttonVisualize;
        Button _buttonEdit;

        ViewHolder(View itemView) {
            super(itemView);
            _textHomeAddress = (TextView) itemView.findViewById(R.id.textHomeAddress);
            _textReferenceContent = (TextView) itemView.findViewById(R.id.textReferenceContent);
            _textLatitudeContent = (TextView) itemView.findViewById(R.id.textLatitudeContent);
            _textLongitudeContent = (TextView) itemView.findViewById(R.id.textLongitudeContent);
            _buttonVisualize = (Button) itemView.findViewById(R.id.buttonVisualize);
            _buttonEdit = (Button) itemView.findViewById(R.id.buttonEdit);
        }

        public void bind(Cliente cliente, DireccionAdapterListener listener) {

            Log.d(TAG, "bind: " + cliente.getIdTipoDireccion());
            if ( cliente.getIdTipoDireccion() == 1 ) {
                _textHomeAddress.setText("Dirección Domicilio");
            } else {
                _textHomeAddress.setText("Dirección Negocio");
            }

            _textReferenceContent.setText(cliente.getReferencia());
            _textLatitudeContent.setText(String.valueOf(cliente.getLatitud()));
            _textLongitudeContent.setText(String.valueOf(cliente.getLongitud()));

            _buttonVisualize.setOnClickListener(v -> listener.onClick(v, cliente));
            _buttonEdit.setOnClickListener(v -> listener.onClick(v, cliente));

            // TODO validacion de otras carteras

            if (cliente.getFlag() == CarteraAnalistaDbHelper.FLAG_OTRAS_CARTERAS_ANALISTA
                && cliente.getCreditos().toUpperCase().equals("SI")) {
                _buttonEdit.setEnabled(false);
            } else {
                _buttonEdit.setEnabled(true);
            }

        }

    }
}
