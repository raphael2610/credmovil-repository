package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteReferido;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;

public class ClienteReferidoAdapter extends RecyclerView.Adapter<ClienteReferidoAdapter.ViewHolder> {

    private static final String TAG = "ClienteReferidoAdapter";

    private List<ClienteReferido> _clienteReferidoList;
    private Context _context;

    public ClienteReferidoAdapter(List<ClienteReferido> clienteReferidoList,
                                  Context context) {
        this._clienteReferidoList = clienteReferidoList;
        this._context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind( _context, _clienteReferidoList.get(position) );
    }

    @Override
    public int getItemCount() {
        return _clienteReferidoList.size();
    }


    public void updateList(List<ClienteReferido> newsClientReferred) {

        DiffUtil.Callback callback = new ClienteReferidoDiffCallback(_clienteReferidoList, newsClientReferred);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._clienteReferidoList.clear();
        this._clienteReferidoList.addAll(newsClientReferred);
        result.dispatchUpdatesTo(this);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView _textName;
        private TextView _textPhone;
        private TextView _textDNI;
        private TextView _textAddress;
        private View _viewIndicator;

        public ViewHolder(View itemView) {
            super(itemView);

            _textName = (TextView) itemView.findViewById(R.id.textName);
            _textPhone = (TextView) itemView.findViewById(R.id.textPhone);
            _textDNI = (TextView) itemView.findViewById(R.id.textDNI);
            _textAddress = (TextView) itemView.findViewById(R.id.textAddress);
            _viewIndicator = (View) itemView.findViewById(R.id.viewIndicator);
        }

        public static ViewHolder from(ViewGroup viewGroup) {

            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.item_cliente_referido, viewGroup, false);
            return new ViewHolder(view);

        }

        public void bind(Context context,  ClienteReferido clienteReferido) {

            _textName.setText(clienteReferido.getNombres());
            _textPhone.setText(clienteReferido.getTelefono());
            _textDNI.setText(clienteReferido.getDocumento());
            _textAddress.setText(clienteReferido.getDireccion());

            if (clienteReferido.getSincronizar() == AgendaComercialDbHelper.FLAG_SYNCRONIZED) {
                _viewIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.ColorSentinel));
            } else {
                _viewIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.ColorEditTextPopUp));
            }

        }

    }

}
