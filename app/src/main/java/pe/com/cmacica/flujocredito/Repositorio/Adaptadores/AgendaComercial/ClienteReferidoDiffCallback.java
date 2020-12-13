package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial;

import android.support.v7.util.DiffUtil;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.AgendaComercial.ClienteReferido;

public class ClienteReferidoDiffCallback extends DiffUtil.Callback {

    private List<ClienteReferido> _oldList;
    private List<ClienteReferido> _newList;

    public ClienteReferidoDiffCallback(List<ClienteReferido> oldList,
                                       List<ClienteReferido> newList) {
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
