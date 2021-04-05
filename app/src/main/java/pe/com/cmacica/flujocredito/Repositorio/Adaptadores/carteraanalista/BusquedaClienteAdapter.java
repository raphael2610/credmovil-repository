package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.R;

import pe.com.cmacica.flujocredito.Repositorio.Mapeo.ContratoDbCmacIca;

public class BusquedaClienteAdapter extends RecyclerView.Adapter<BusquedaClienteAdapter.ViewHolder> {

    private static final String TAG = "BusquedaClienteAdapter";

    public BusquedaClienteListener _busquedaClienteListener;
    public List<Cliente> _clienteList;


    public BusquedaClienteAdapter(
            BusquedaClienteListener busquedaClienteListener,
            List<Cliente> clienteList) {
        this._busquedaClienteListener = busquedaClienteListener;
        this._clienteList = clienteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(_clienteList.get(position), _busquedaClienteListener);
    }

    @Override
    public int getItemCount() {
        return _clienteList.size();
    }


    public void updateList(List<Cliente> newsCustomers) {

        DiffUtil.Callback callback = new ClienteDiffCallback(_clienteList, newsCustomers);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._clienteList.clear();
        this._clienteList.addAll(newsCustomers);
        result.dispatchUpdatesTo(this);

    }

    public interface BusquedaClienteListener {
        void onClick(Cliente cliente);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout _constraintlayoutBusquedaCliente;
        private TextView _textName;
        private TextView _textDNI;


        public ViewHolder(View itemView) {
            super(itemView);
            _constraintlayoutBusquedaCliente = (ConstraintLayout) itemView.findViewById(R.id.constraintlayoutBusquedaCliente);
            _textName = (TextView) itemView.findViewById(R.id.textName);
            _textDNI = (TextView) itemView.findViewById(R.id.textDNI);
        }


        public static ViewHolder from(ViewGroup viewGroup) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.item_busqueda_cliente, viewGroup, false);
            return new ViewHolder(view);
        }


        public void bind(Cliente cliente, BusquedaClienteListener busquedaClienteListener) {
            _constraintlayoutBusquedaCliente.setOnClickListener(v -> busquedaClienteListener.onClick(cliente));
            _textName.setText(cliente.getNombre());
            _textDNI.setText(cliente.getDoi());
        }

    }


}
