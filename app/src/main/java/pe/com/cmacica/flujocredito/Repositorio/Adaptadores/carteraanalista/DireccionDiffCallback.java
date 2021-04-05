package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.carteraanalista;



import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import pe.com.cmacica.flujocredito.Model.carteraanalista.Direccion;

public class DireccionDiffCallback extends DiffUtil.Callback {

    private List<Direccion> _oldList;
    private List<Direccion> _newList;

    public DireccionDiffCallback(List<Direccion> oldList, List<Direccion> newList) {
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
