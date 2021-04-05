package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.AgendaComercial.AgendaComercialDbHelper;

public class VisitaAdapter extends RecyclerView.Adapter<VisitaAdapter.ViewHolder> {

    private static final String TAG = "VisitaAdapter";

    private List<ClienteVisita> _clienteVisitaList;
    private VisitaAdapterListener _visitaAdapterListener;
    private Context _context;


    public VisitaAdapter(List<ClienteVisita> clienteVisitaList,
                         VisitaAdapterListener visitaAdapterListener,
                         Context context) {
        _clienteVisitaList = clienteVisitaList;
        _visitaAdapterListener = visitaAdapterListener;
        _context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(_clienteVisitaList.get(position), _visitaAdapterListener, _context);
    }

    @Override
    public int getItemCount() {
        return _clienteVisitaList.size();
    }


    public interface VisitaAdapterListener {
        void onClick(ClienteVisita clienteVisita);
    }


    public void updateList(List<ClienteVisita> newsClientVisits) {

        DiffUtil.Callback callback = new VisitaDiffCallback(_clienteVisitaList, newsClientVisits);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        this._clienteVisitaList.clear();
        this._clienteVisitaList.addAll(newsClientVisits);
        result.dispatchUpdatesTo(this);

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView _cardviewVisita;
        private TextView _textName;
        private TextView _textDNI;
        private TextView _textAddress;
        private TextView _textVisitDate;
        private TextView _textPhone;
        private View _viewIndicatorResult;


        public ViewHolder(View itemView) {
            super(itemView);

            _cardviewVisita = (CardView) itemView.findViewById(R.id.cardviewVisita);
            _textName = (TextView) itemView.findViewById(R.id.textName);
            _textDNI = (TextView) itemView.findViewById(R.id.textDNI);
            _textAddress = (TextView) itemView.findViewById(R.id.textAddress);
            _textVisitDate = (TextView) itemView.findViewById(R.id.textVisitDate);
            _textPhone = (TextView) itemView.findViewById(R.id.textPhone);
            _viewIndicatorResult = (View) itemView.findViewById(R.id.viewIndicatorResult);

        }

        public static ViewHolder from(ViewGroup viewGroup) {

            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.item_visita, viewGroup, false);
            return new ViewHolder(view);

        }

        public void bind(ClienteVisita clienteVisita,
                         VisitaAdapterListener visitaAdapterListener,
                         Context context) {

            _cardviewVisita.setOnClickListener(v -> visitaAdapterListener.onClick(clienteVisita));

            _textName.setText(clienteVisita.getNombres());
            _textDNI.setText(clienteVisita.getDni());
            _textAddress.setText(clienteVisita.getDireccion());
            _textPhone.setText(clienteVisita.getTelefono());

            try {
                if (clienteVisita.getFechaVisita() != null) {
                    _textVisitDate.setText( clienteVisita.getFechaVisita().substring(0,2) + "\n"
                            + showMonthText(clienteVisita.getFechaVisita().substring(3,5)) );
                }

                if (clienteVisita.getResultado() == AgendaComercialDbHelper.FLAG_CON_RESULTADO) {
                    _viewIndicatorResult.setBackgroundColor(ContextCompat.getColor(context, R.color.ColorSentinel));
                } else {
                    _viewIndicatorResult.setBackgroundColor(ContextCompat.getColor(context, R.color.ColorEditTextPopUp));
                }

            } catch (Exception e) {
                _textVisitDate.setText(clienteVisita.getFechaVisita());
            }

        }

        private String showMonthText(String month) {

            String meses[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
                    "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

            return meses[Integer.valueOf(month)-1];

        }


    }

}
