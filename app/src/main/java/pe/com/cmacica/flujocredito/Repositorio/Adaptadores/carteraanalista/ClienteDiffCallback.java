package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista;

import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Cliente;

public class ClienteDiffCallback extends DiffUtil.Callback {

    private List<Cliente> _oldList;
    private List<Cliente> _newList;

    public ClienteDiffCallback(List<Cliente> oldList, List<Cliente> newList) {
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
        return _oldList.get(oldItemPosition).equals(_newList.get(newItemPosition));
    }
}
