package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.OfertaCliente;
import pe.com.cmacica.flujocredito.R;

public class OfertaClienteAdapter extends RecyclerView.Adapter<OfertaClienteAdapter.ViewHolder> {

    private static final String TAG = "OfertaClienteAdapter";

    private List<OfertaCliente> _dataSource;


    public OfertaClienteAdapter(List<OfertaCliente> dataSource) {
        this._dataSource = dataSource;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind( _dataSource.get(position) );
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }


    public void updateList(List<OfertaCliente> newsOffers) {

        DiffUtil.Callback callback = new OfertaClienteDiffCallback(_dataSource, newsOffers);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._dataSource.clear();
        this._dataSource.addAll(newsOffers);
        result.dispatchUpdatesTo(this);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView _textDescriptionContent;
        private TextView _textAmountCCContent;
        private TextView _textAmountSCContent;

        public ViewHolder(View itemView) {
            super(itemView);
            _textDescriptionContent = (TextView) itemView.findViewById(R.id.textDescriptionContent);
            _textAmountCCContent = (TextView) itemView.findViewById(R.id.textAmountCCContent);
            _textAmountSCContent = (TextView) itemView.findViewById(R.id.textAmountSCContent);
        }


        public static ViewHolder from(ViewGroup viewGroup) {

            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.item_oferta_cliente, viewGroup, false);
            return new ViewHolder(view);

        }

        public void bind(OfertaCliente offerClient) {

            _textDescriptionContent.setText(offerClient.getOferta());
            _textAmountCCContent.setText( "S/ " + String.valueOf(offerClient.getMontoOfertaCC()) );
            _textAmountSCContent.setText( "S/ " + String.valueOf(offerClient.getMontoOfertaSC()) );

        }

    }

}
