package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial;

import android.support.v7.util.DiffUtil;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteVisita;

public class VisitaDiffCallback extends DiffUtil.Callback {

    private List<ClienteVisita> _oldList;
    private List<ClienteVisita> _newList;

    public VisitaDiffCallback(List<ClienteVisita> oldList, List<ClienteVisita> newList) {
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
