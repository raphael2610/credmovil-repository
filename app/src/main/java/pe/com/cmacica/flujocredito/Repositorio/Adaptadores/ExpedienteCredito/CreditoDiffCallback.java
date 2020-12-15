package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.ExpedienteCredito;

import android.support.v7.util.DiffUtil;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Credito;

public class CreditoDiffCallback extends DiffUtil.Callback {

    private List<Credito> _oldList;
    private List<Credito> _newList;



    public CreditoDiffCallback(List<Credito> oldList,
                               List<Credito> newList) {
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
