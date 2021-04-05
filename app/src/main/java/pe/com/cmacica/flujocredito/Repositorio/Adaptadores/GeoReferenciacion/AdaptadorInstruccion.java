package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.GeoReferenciacion;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.InstruccionModel;
import pe.com.cmacica.flujocredito.R;

public class AdaptadorInstruccion extends RecyclerView.Adapter<AdaptadorInstruccion.ViewHolder> {

    private List<InstruccionModel> Lista;

    public AdaptadorInstruccion(List<InstruccionModel> lista){
        Lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIndicacion;
        TextView tvIndicacion;

        public ViewHolder(View v) {
            super(v);
            ivIndicacion = (ImageView) v.findViewById(R.id.iv_indicacion);
            tvIndicacion = (TextView) v.findViewById(R.id.tv_indicacion);
        }
    }

    @Override
    public AdaptadorInstruccion.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_instruccion,parent,false);
        return new AdaptadorInstruccion.ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ivIndicacion.setImageResource(Lista.get(position).getImagen());
        holder.tvIndicacion.setText(Lista.get(position).getIndicacion());
    }

    @Override
    public int getItemCount() {
        return Lista.size();
    }
}