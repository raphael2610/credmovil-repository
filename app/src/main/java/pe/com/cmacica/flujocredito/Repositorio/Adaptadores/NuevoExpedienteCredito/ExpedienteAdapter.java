package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.NuevoExpedienteCredito;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;
import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Expediente;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.NuevoExpedienteCredito.ExpedienteDiffCallback;

public class ExpedienteAdapter extends RecyclerView.Adapter<ExpedienteAdapter.ViewHolder> {

    private List<Expediente> _proceedingList;
    private Context _context;
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
        holder.bind(_context, _proceedingList.get(position), _expedienteAdapterListener);
    }

    @Override
    public int getItemCount() {
        return _proceedingList.size();
    }


    public interface ExpedienteAdapterListener {
            void onUpdateFile(Expediente expediente);
            void onDeleteFile(Expediente expediente);
    }


    public void updateList(List<Expediente> newsProceeding) {

        DiffUtil.Callback callback = new ExpedienteDiffCallback(_proceedingList, newsProceeding);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._proceedingList.clear();
        this._proceedingList.addAll(newsProceeding);
        result.dispatchUpdatesTo(this);

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        _context = recyclerView.getContext();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView _textNameFile;
        private TextView _textDateContent;
        private TextView _textUserContent;
        private TextView _textSizeContent;
        private ImageView _prueba;
        private Button _buttonUpdate;
        private Button _buttonDelete;


        private ViewHolder(View itemView) {
            super(itemView);
            _textNameFile = (TextView) itemView.findViewById(R.id.textNameExpediente);
            _textDateContent = (TextView) itemView.findViewById(R.id.textDateContent);
            _textUserContent = (TextView) itemView.findViewById(R.id.textUserContent);
            _textSizeContent = (TextView) itemView.findViewById(R.id.textSizeContent);
            _prueba = (ImageView) itemView.findViewById(R.id.prueba);
            _buttonUpdate = (Button) itemView.findViewById(R.id.buttonUpdate);
            _buttonDelete = (Button) itemView.findViewById(R.id.buttonDelete);
        }


        public static ViewHolder from(ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_expediente, parent, false);
            return new ViewHolder(view);

        }


        public void bind(Context context, Expediente expediente, ExpedienteAdapterListener expedienteAdapterListener) {

            _textNameFile.setText(expediente.getName());
            _textDateContent.setText(expediente.getDate());
            _textUserContent.setText(expediente.getUser());
            _textSizeContent.setText(expediente.getSize());
            _buttonUpdate.setOnClickListener(v -> expedienteAdapterListener.onUpdateFile(expediente));
            _buttonDelete.setOnClickListener(v -> expedienteAdapterListener.onDeleteFile(expediente));


            try {
                byte[] decodedString = Base64.decode(expediente.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                _prueba.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }


    }


}
