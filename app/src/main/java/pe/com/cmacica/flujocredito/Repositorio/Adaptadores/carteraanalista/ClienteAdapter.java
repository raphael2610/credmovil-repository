package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.util.ContentLengthInputStream;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.carteraanalista.CarteraAnalistaDbHelper;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ViewHolder> {


    private static final String TAG = "ClienteAdapter";

    private ClienteAdapterListener _clienteAdapterListener;
    private List<Cliente> _clienteList;

    public ClienteAdapter(ClienteAdapterListener clienteAdapterListener, List<Cliente> clienteList) {
        this._clienteAdapterListener = clienteAdapterListener;
        this._clienteList = clienteList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(_clienteList.get(position), _clienteAdapterListener);
    }

    @Override
    public int getItemCount() {
        return _clienteList.size();
    }


    public interface ClienteAdapterListener{
        void onClick(Cliente cliente);
    }


    public void updateList(List<Cliente> newsCustomers) {

        Log.d(TAG, "updateList: " + newsCustomers.size());

        DiffUtil.Callback callback = new ClienteDiffCallback(_clienteList, newsCustomers);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._clienteList.clear();
        this._clienteList.addAll(newsCustomers);
        result.dispatchUpdatesTo(this);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        View _itemCustomer;
        TextView _textTitle;
        TextView _textDocument;
        TextView _textPhone;
        ImageView _imageIcon;
        TextView _textGeoposition;


        public ViewHolder(View itemView) {
            super(itemView);
            _itemCustomer = itemView.findViewById(R.id.itemCustomer);
            _textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            _textDocument = (TextView) itemView.findViewById(R.id.textDocument);
            _textPhone = (TextView) itemView.findViewById(R.id.textPhone);
            _imageIcon = (ImageView) itemView.findViewById(R.id.imageIcon);
            _textGeoposition = (TextView) itemView.findViewById(R.id.textGeoposition);
        }


        public void bind(Cliente cliente, ClienteAdapterListener clienteAdapterListener) {
            _itemCustomer.setOnClickListener(v -> clienteAdapterListener.onClick(cliente));

            showPhone(cliente);
            _textTitle.setText(cliente.getNombre());
            _textDocument.setText(String.format("Documento: %s", cliente.getDoi()));
            showPhone(cliente);
            //_textPhone.setText(String.format("Teléfono: %s/%s", cliente.getTelefonoUno(), cliente.getTelefonoDos()));
            _textGeoposition.setText(String.format("GeoPosicionado: %s", cliente.getGeoposicion().toLowerCase()));

            switch (cliente.getFlag()) {
                case CarteraAnalistaDbHelper.FLAG_ACTUAL_ANALISTA:
                    _imageIcon.setImageResource(R.drawable.ic_file_document);
                    break;
                case CarteraAnalistaDbHelper.FLAG_INSERTADO:
                    _imageIcon.setImageResource(R.drawable.ic_person_add);
                    break;
                case CarteraAnalistaDbHelper.FLAG_OTRAS_CARTERAS_ANALISTA:
                    _imageIcon.setImageResource(R.drawable.ic_account_balance);
                    break;
                default:
                    _imageIcon.setImageResource(R.drawable.ic_file_document);
                    break;
            }


        }


        private void showPhone(Cliente cliente) {

            try {

                String phoneOne = cliente.getTelefonoUno() == null ? "" : cliente.getTelefonoUno().trim();
                String phoneTwo = cliente.getTelefonoDos() == null ? "" : cliente.getTelefonoDos().trim();

                if (phoneOne.isEmpty() == false && phoneTwo.isEmpty() == false) {
                    _textPhone.setText(String.format("Teléfono: %s/%s", cliente.getTelefonoUno(), cliente.getTelefonoDos()));
                    return;
                }

                if (phoneOne.isEmpty() == false && phoneTwo.isEmpty() == true) {
                    _textPhone.setText(String.format("Teléfono: %s", cliente.getTelefonoUno()));
                    return;
                }

                if (phoneOne.isEmpty() == true && phoneTwo.isEmpty() == false) {
                    _textPhone.setText(String.format("Teléfono: %s", cliente.getTelefonoDos()));
                    return;
                }

                if (phoneOne.isEmpty() == true && phoneTwo.isEmpty() == true) {
                    _textPhone.setText(String.format("Teléfono: %s", phoneOne, phoneTwo));
                    return;
                }

            } catch (Exception e) { }

        }
    }

}
