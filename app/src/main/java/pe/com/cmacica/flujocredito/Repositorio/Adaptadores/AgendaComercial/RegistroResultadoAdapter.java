package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.AgendaComercial;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class RegistroResultadoAdapter extends FragmentPagerAdapter {


    private List<Fragment> _dataSource;
    private List<String> _dataSourceTitle;

    public RegistroResultadoAdapter(FragmentManager fragmentManager,
                                    List<Fragment> dataSource,
                                    List<String> dataSourceTitle) {
        super(fragmentManager);
        _dataSource = dataSource;
        _dataSourceTitle = dataSourceTitle;
    }


    @Override
    public Fragment getItem(int position) {
        return _dataSource.get(position);
    }




    @Override
    public int getCount() {
        return _dataSource.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return _dataSourceTitle.get(position);
    }


}
