package pe.com.cmacica.flujocredito.ViewModel.GeoReferenciacion;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleySingleton;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.GeoRefClienteModel;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.LocalizacionModel;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.DatabaseHelper;
import pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion.GeoLocalizacion;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


/**
 * by MFPE - 2019.
 */
public class UploadGeoRefFragment extends Fragment {

    private ProgressDialog progressDialog;
    private Button btnDownloadClientes;
    private Button btnLoadClientes;
    private TextView tvRadio;
    private DatabaseHelper dbHelper;
    public UploadGeoRefFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upload_geo_ref, container, false);

        btnDownloadClientes = (Button) v.findViewById(R.id.btnDownloadClientes);
        btnLoadClientes = (Button) v.findViewById(R.id.btnLoadClientes);
        tvRadio = (TextView) v.findViewById(R.id.tv_radio);

        btnDownloadClientes.setOnClickListener(v1 -> {
            ValidarPermisos();
        });

        btnLoadClientes.setOnClickListener(v2 -> {
            progressDialog = ProgressDialog.show(getActivity(),"Espere por favor","Sincronizando clientes...");
            SincronizarClientes();
        });

        dbHelper = new DatabaseHelper(getContext(), "DBCMACICA", null, 1);
        return v;
    }

    public void DescargarClientes(){

        progressDialog = ProgressDialog.show(getActivity(),"Espere por favor","Buscando clientes...");

        if(tvRadio.getText().length() == 0 || tvRadio.getText().toString().equals("0")){
            Toast.makeText(getActivity(), "Ingrese un radio en metros válido.", Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
            return;
        }

        ArrayList<GeoRefClienteModel> clientes = dbHelper.ListarClienteAll();

        if(clientes.size() > 0){
            Toast.makeText(getActivity(), "Se ha verificado que hay clientes registrados que no han sido guardados. Por favor guárdelos antes de continuar.", Toast.LENGTH_LONG).show();
            progressDialog.cancel();
            return;
        }

        GeoLocalizacion geo = new GeoLocalizacion();
        LocalizacionModel posicion = geo.GetActualPosition(getActivity());

        String url = String.format(SrvCmacIca.GET_CLIENTES_GEOREF, posicion.getLatitud(), posicion.getLongitud(), tvRadio.getText());
        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                response -> ProcesarClientesDescargados(response),
                                error -> Toast.makeText(getActivity(), "Error del servicio:" + error.toString(), Toast.LENGTH_LONG).show()
                        )
                );
        progressDialog.cancel();
    }

    public void ProcesarClientesDescargados(JSONObject response){
        try{
            if(response.getBoolean("IsCorrect")){
                JSONArray jsonClientes = response.getJSONArray("Data");

                if(jsonClientes.length() == 0){
                    Toast.makeText(getActivity(), "¡No se encontraron clientes cercanos! Intenta con un radio mayor.", Toast.LENGTH_LONG).show();
                } else {
                    for(int i = 0; i < jsonClientes.length(); i++){
                        JSONObject row = jsonClientes.getJSONObject(i);

                        GeoRefClienteModel cliente = new GeoRefClienteModel();
                        cliente.setNombres(row.getString("Nombres"));
                        cliente.setDoi(row.getString("Doi"));
                        cliente.setTelefono(row.getString("Telefono"));
                        cliente.setLongitud(row.getDouble("Longitud"));
                        cliente.setLatitud(row.getDouble("Latitud"));
                        cliente.setIdTipoDireccion(row.getInt("IdTipoDireccion"));
                        cliente.setReferencia(row.getString("Referencia"));
                        dbHelper.InsertarCliente(cliente);
                    }
                    Toast.makeText(getActivity(), "¡Se realizó la descarga de los clientes!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void SincronizarClientes(){
        ArrayList<GeoRefClienteModel> clientes = dbHelper.ListarClienteAll();

        if(clientes.size() == 0){
            Toast.makeText(getActivity(), "¡No hay clientes pendientes para guardar!", Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
            return;
        }

        Gson gsonpojo = new GsonBuilder().create();
        String json = gsonpojo.toJson(clientes);
        HashMap<String, String> cabeceras = new HashMap<>();

        new RESTService(getActivity()).post(SrvCmacIca.POST_CLIENTES_GEOREF, json,
                response -> ProcesarClientesSincronizados(response),
                error -> Toast.makeText(getActivity(), "Error del servicio:" + error.toString(), Toast.LENGTH_LONG).show(),
                cabeceras);
        progressDialog.cancel();
    }

    public void ProcesarClientesSincronizados(JSONObject response){
        try{
            if(response.getBoolean("IsCorrect")){
                Toast.makeText(getActivity(), "¡Clientes guardados correctamente!", Toast.LENGTH_SHORT).show();
                dbHelper.EliminarClientes();
            } else {
                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void ValidarPermisos(){
        if(ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permisos = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permisos, 0);
        } else {
            DescargarClientes();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                DescargarClientes();
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