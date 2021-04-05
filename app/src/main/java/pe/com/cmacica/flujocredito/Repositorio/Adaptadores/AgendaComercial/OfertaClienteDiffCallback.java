package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial;



import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.OfertaCliente;

public class OfertaClienteDiffCallback extends DiffUtil.Callback {

    private List<OfertaCliente> _oldList;
    private List<OfertaCliente> _newList;

    public OfertaClienteDiffCallback(List<OfertaCliente> oldList,
                                     List<OfertaCliente> newList) {
        this._oldList = oldList;
        this._newList = newList;
    }


    @Override
    public int getOldListSize() {
        return _oldList.size();
    }

    @Override
    public int getNewListSize() {
        return _newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return _oldList.get(oldItemPosition).getId() == _newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return _oldList.get(oldItemPosition).equals( _newList.get(newItemPosition) );
    }

}
