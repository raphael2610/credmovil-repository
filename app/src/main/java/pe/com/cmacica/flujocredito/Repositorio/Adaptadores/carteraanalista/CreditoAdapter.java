package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Credito;
import pe.com.cmacica.flujocredito.R;

public class CreditoAdapter extends RecyclerView.Adapter<CreditoAdapter.ViewHolder> {

    private List<Credito> _creditoList;

    public CreditoAdapter(List<Credito> creditoList) {
        this._creditoList = creditoList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_credito, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(_creditoList.get(position));
    }

    @Override
    public int getItemCount() {
        return _creditoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView _textCredits;
        TextView _textNumberContent;
        TextView _textStateContent;
        TextView _textAmountContent;

        public ViewHolder(View itemView) {
            super(itemView);

            _textCredits = (TextView) itemView.findViewById(R.id.textCredits);
            _textNumberContent = (TextView) itemView.findViewById(R.id.textNumberContent);
            _textStateContent = (TextView) itemView.findViewById(R.id.textStateContent);
            _textAmountContent = (TextView) itemView.findViewById(R.id.textAmountContent);
        }

        public void bind(Credito credito) {
            _textCredits.setText(credito.getNombre());
            _textNumberContent.setText(credito.getNumero());
            _textStateContent.setText(credito.getEstado());
            _textAmountContent.setText(String.valueOf(credito.getMonto()));
        }

    }
}
