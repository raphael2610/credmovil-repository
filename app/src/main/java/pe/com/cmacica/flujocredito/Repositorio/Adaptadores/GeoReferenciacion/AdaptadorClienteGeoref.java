package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.GeoReferenciacion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import pe.com.cmacica.flujocredito.Base.ItemClickListener;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.GeoRefClienteModel;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.DatabaseHelper;
import pe.com.cmacica.flujocredito.ViewModel.GeoReferenciacion.GeoRefFragment;
import pe.com.cmacica.flujocredito.ViewModel.GeoReferenciacion.RegisterGeoRefActivity;

public class AdaptadorClienteGeoref extends RecyclerView.Adapter<AdaptadorClienteGeoref.ViewHolder> implements ItemClickListener {

    private List<GeoRefClienteModel> Lista;
    public Context context;
    private DatabaseHelper dbHelper;
    private GeoRefFragment fragment;

    public AdaptadorClienteGeoref(Context context, DatabaseHelper dbHelper, List<GeoRefClienteModel> lista, GeoRefFragment fragment){
        this.context = context;
        Lista = lista;
        this.dbHelper = dbHelper;
        this.fragment = fragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvListNombre;
        TextView tvListDoi;
        TextView tvListTelefono;
        public ItemClickListener listener;

        public ViewHolder(View v, ItemClickListener listener) {
            super(v);
            tvListNombre = (TextView) v.findViewById(R.id.tvListNombre);
            tvListDoi = (TextView) v.findViewById(R.id.tvListDoi);
            tvListTelefono = (TextView) v.findViewById(R.id.tvListTelefono);
            this.listener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public AdaptadorClienteGeoref.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_cliente_georef,parent,false);
        return new AdaptadorClienteGeoref.ViewHolder(vista, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvListNombre.setText(Lista.get(position).getNombres());
        holder.tvListDoi.setText("Documento: " + Lista.get(position).getDoi());
        holder.tvListTelefono.setText("Teléfono: " + Lista.get(position).getTelefono());
    }

    @Override
    public int getItemCount() {
        return Lista.size();
    }

    @Override
    public void onItemClick(View view, int position) {
        ArrayList<String> direcciones = dbHelper.ListarDireccionesPorDoi(Lista.get(position).getDoi());
        String[] finalItems = direcciones.toArray(new String[direcciones.size()]);
        final String[] CategorySelected ={ finalItems[0] };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Seleccionar ubicación");

        builder.setSingleChoiceItems(finalItems, 0, (dialog, which) -> {
            CategorySelected[0] = finalItems[which];
        });
        builder.setNegativeButton("VISUALIZAR", (dialog, which) -> {
            String[] parts = CategorySelected[0].split("-");
            Integer tipoDir = parts[1].contains("domicilio") ? 1 : 2;
            GeoRefClienteModel cliente = dbHelper.ObtenerClientePorId(Integer.parseInt(parts[0].trim()), tipoDir);

            fragment.cliente = cliente;

            if(fragment.checkGpsStatus()){
                fragment.ValidarPermisos(1);
            }

        });
        builder.setPositiveButton("ACTUALIZAR", (dialog, which) -> {
            String[] parts = CategorySelected[0].split("-");
            Integer tipoDir = parts[1].contains("domicilio") ? 1 : 2;
            GeoRefClienteModel cliente = dbHelper.ObtenerClientePorId(Integer.parseInt(parts[0].trim()), tipoDir);

            Intent intent = new Intent(context, RegisterGeoRefActivity.class);
            intent.putExtra("clienteSeleccionado", cliente);
            context.startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}