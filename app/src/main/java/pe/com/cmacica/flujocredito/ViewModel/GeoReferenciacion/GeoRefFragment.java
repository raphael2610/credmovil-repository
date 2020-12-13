package pe.com.cmacica.flujocredito.ViewModel.GeoReferenciacion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.GeoRefClienteModel;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.TipoDireccionModel;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Repositorio.Adaptadores.GeoReferenciacion.AdaptadorClienteGeoref;
import pe.com.cmacica.flujocredito.Utilitarios.DecoracionLineaDivisoria;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.DatabaseHelper;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


/**
 * by MFPE - 2019.
 */
public class GeoRefFragment extends Fragment {

    public GeoRefClienteModel cliente;
    private FloatingActionButton btnRegisterLocation;
    private RecyclerView rvGeoRefClientes;
    private LinearLayoutManager layoutManager;
    private AdaptadorClienteGeoref adaptador;
    private DatabaseHelper dbHelper;

    public GeoRefFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View vista= inflater.inflate(R.layout.fragment_geo_ref, container, false);

        btnRegisterLocation = (FloatingActionButton)vista.findViewById(R.id.btnRegisterLocation);
        rvGeoRefClientes = (RecyclerView)vista.findViewById(R.id.rv_georefClientes);

        btnRegisterLocation.setOnClickListener(v -> {
            if(checkGpsStatus()){
                ValidarPermisos(0);
            }
        });

        dbHelper = new DatabaseHelper(getContext(), "DBCMACICA", null, 1);
        CargaTiposDireccion();
        CargarClientes();

        return vista;
    }

    public boolean checkGpsStatus(){

        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled && !network_enabled){
            Toast.makeText(getActivity(), "El GPS del dispositivo esta desactivado. Activalo y reinicia la aplicación.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public void ValidarPermisos(int requestCode){

        if(ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permisos = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permisos, requestCode);
        } else {
            if(requestCode == 0) {
                RegistrarCliente();
            } else {
                VisualizarCliente();
            }
        }
    }

    public void VisualizarCliente(){
        Intent intent = new Intent(getActivity(), ViewGeoRefActivity.class);
        intent.putExtra("clienteSeleccionado", cliente);
        startActivity(intent);
    }

    public void RegistrarCliente(){
        Intent intent = new Intent(getActivity(), RegisterGeoRefActivity.class);
        startActivity(intent);
    }

    public void CargarClientes(){
        layoutManager = new LinearLayoutManager(getActivity());
        rvGeoRefClientes.setLayoutManager(layoutManager);

        ArrayList<GeoRefClienteModel> clientes = dbHelper.ListarClientes();

        adaptador = new AdaptadorClienteGeoref(getActivity(), dbHelper, clientes, this);
        rvGeoRefClientes.setAdapter(adaptador);
        rvGeoRefClientes.addItemDecoration(new DecoracionLineaDivisoria(getActivity()));
    }

    public void CargaTiposDireccion(){
        if (dbHelper.ListarTipoDireccion().size() == 0){
            dbHelper.InsertarTipoDireccion(new TipoDireccionModel(1,"Dirección domicilio"));
            dbHelper.InsertarTipoDireccion(new TipoDireccionModel(2,"Dirección negocio"));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        CargarClientes();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){
        if(requestCode == 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                RegistrarCliente();
            } else {
                if(shouldShowRequestPermissionRationale(permissions[0])){
                    Toast.makeText(getActivity(), "Se ha denegado el acceso a la ubicación.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Se ha denegado permanentemente el acceso a la ubicación.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Puede activarlo en Ajustes > Aplicaciones > Credi Móvil > Permisos.", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                VisualizarCliente();
            } else {
                if(shouldShowRequestPermissionRationale(permissions[0])){
                    Toast.makeText(getActivity(), "Se ha denegado el acceso a la ubicación.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Se ha denegado permanentemente el acceso a la ubicación.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Puede activarlo en Ajustes > Aplicaciones > Credi Móvil > Permisos.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}