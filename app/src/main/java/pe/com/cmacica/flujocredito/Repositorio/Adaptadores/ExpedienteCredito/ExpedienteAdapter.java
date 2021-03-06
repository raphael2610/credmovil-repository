package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.ExpedienteCredito;

import android.support.v7.util.DiffUtil;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Expediente;
import pe.com.cmacica.flujocredito.R;

public class ExpedienteAdapter extends RecyclerView.Adapter<ExpedienteAdapter.ViewHolder> {

    private List<Expediente> _proceedingList;
    private ExpedienteAdapterListener _expedienteAdapterListener;


    public ExpedienteAdapter(List<Expediente> proceedingList,
                             ExpedienteAdapterListener expedienteAdapterListener) {
        this._proceedingList = proceedingList;
        this._expedienteAdapterListener = expedienteAdapterListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(_proceedingList.get(position), _expedienteAdapterListener);
    }

    @Override
    public int getItemCount() {
        return _proceedingList.size();
    }


    public interface ExpedienteAdapterListener {
        void onClick(Expediente expediente);
    }


    public void updateList(List<Expediente> newsProceeding) {

        DiffUtil.Callback callback = new ExpedienteDiffCallback(_proceedingList, newsProceeding);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._proceedingList.clear();
        this._proceedingList.addAll(newsProceeding);
        result.dispatchUpdatesTo(this);

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView _cardviewFile;
        private TextView _textNameFile;
        private TextView _textDateContent;
        private TextView _textUserContent;
//        private TextView _textSizeContent;


        private ViewHolder(View itemView) {
            super(itemView);
            _cardviewFile = (CardView) itemView.findViewById(R.id.cardviewFile);
            _textNameFile = (TextView) itemView.findViewById(R.id.textNameFile);
            _textDateContent = (TextView) itemView.findViewById(R.id.textDateContent);
            _textUserContent = (TextView) itemView.findViewById(R.id.textUserContent);
//            _textSizeContent = (TextView) itemView.findViewById(R.id.textSizeContent);
        }


        public static ViewHolder from(ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_file, parent, false);
            return new ViewHolder(view);

        }


        public void bind(Expediente expediente, ExpedienteAdapterListener expedienteAdapterListener) {
            _cardviewFile.setOnClickListener(view -> {
                expedienteAdapterListener.onClick(expediente);
            });
            _textNameFile.setText(expediente.getName());
            _textDateContent.setText(expediente.getDate());
            _textUserContent.setText(expediente.getUser());
//            _textSizeContent.setText(expediente.getSize());
        }


    }


}
