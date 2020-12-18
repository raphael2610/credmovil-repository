package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.ExpedienteCredito;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.R;

public class CreditoAdapter extends RecyclerView.Adapter<CreditoAdapter.ViewHolder> {

    private List<Credito> _creditoList;
    private CreditoAdapterListener _creditoAdapterListener;

    public CreditoAdapter(List<Credito> creditoList,
                          CreditoAdapterListener creditoAdapterListener) {
        this._creditoList = creditoList;
        this._creditoAdapterListener = creditoAdapterListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(_creditoList.get(position), _creditoAdapterListener);
    }

    @Override
    public int getItemCount() {
        return _creditoList.size();
    }


    public interface CreditoAdapterListener {
        void onClick(Credito credito);
    }


    public void updateList(List<Credito> newsCredits) {

        DiffUtil.Callback callback = new CreditoDiffCallback(_creditoList, newsCredits);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._creditoList.clear();
        this._creditoList.addAll(newsCredits);
        result.dispatchUpdatesTo(this);

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView _cardviewExpedienteCredito;
        private TextView _appcompattextviewNamberContent;
        private TextView _appcompattextviewCreditTypeContent;
        private TextView _textRefundDateContent;
        private TextView _textStateContent;
        private TextView _textAmountContent;


        private ViewHolder(View itemView) {
            super(itemView);
            _cardviewExpedienteCredito = (CardView) itemView.findViewById(R.id.cardviewExpedienteCredito);
            _appcompattextviewNamberContent = (TextView) itemView.findViewById(R.id.appcompattextviewNamberContent);
            _appcompattextviewCreditTypeContent = (TextView) itemView.findViewById(R.id.appcompattextviewCreditTypeContent);
            _textRefundDateContent = (TextView) itemView.findViewById(R.id.textRefundDateContent);
            _textStateContent = (TextView) itemView.findViewById(R.id.textStateContent);
            _textAmountContent = (TextView) itemView.findViewById(R.id.textAmountContent);
        }


        public static ViewHolder from(ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_expediente_credito, parent, false);
            return new ViewHolder(view);

        }

        public void bind(Credito credito, CreditoAdapterListener creditoAdapterListener) {
            _cardviewExpedienteCredito.setOnClickListener(view -> creditoAdapterListener.onClick(credito));
            _appcompattextviewNamberContent.setText(credito.getNumberCredit());
            _appcompattextviewCreditTypeContent.setText(credito.getTypeCredit());
            _textRefundDateContent.setText(credito.getRefundDate());
            _textStateContent.setText(credito.getState());
            _textAmountContent.setText(credito.getAmount());
        }

    }

}
