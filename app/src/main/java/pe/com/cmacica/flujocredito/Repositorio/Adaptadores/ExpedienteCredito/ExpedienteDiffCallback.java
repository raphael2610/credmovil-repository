package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.ExpedienteCredito;

import android.support.v7.util.DiffUtil;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.ExpedienteCredito.Expediente;

public class ExpedienteDiffCallback extends DiffUtil.Callback {

    private List<Expediente> _oldList;
    private List<Expediente> _newList;



    public ExpedienteDiffCallback(List<Expediente> oldList,
                                  List<Expediente> newList) {
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
