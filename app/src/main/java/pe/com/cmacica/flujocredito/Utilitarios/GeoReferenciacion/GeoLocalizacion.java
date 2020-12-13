package pe.com.cmacica.flujocredito.Utilitarios.GeoReferenciacion;

import android.app.Activity;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.location.Location;
import java.util.List;
import android.content.Context;
import pe.com.cmacica.flujocredito.Model.GeoReferenciacion.LocalizacionModel;

/**
 * by MFPE - 2019.
 */
public class GeoLocalizacion{

    public LocalizacionModel GetActualPosition(Activity activity){
        LocalizacionModel location = new LocalizacionModel();
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            String[] permisos = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(activity, permisos, 0);
        }

        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationManager locManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            assert locManager != null;
            List<String> providers= locManager.getProviders(true);

            for (String provider:providers) {
                if(locManager.isProviderEnabled(provider)) {
                    locManager.requestLocationUpdates(provider, 1000L, 0.0f, new LocationListener(){

                        @Override
                        public void onLocationChanged(Location location) { }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) { }

                        @Override
                        public void onProviderDisabled(String provider) { }
                    });
                    try {
                        Location localizacion = locManager.getLastKnownLocation(provider);
                        if (localizacion != null) {
                            location.setLongitud(localizacion.getLongitude());
                            location.setLatitud(localizacion.getLatitude());
                        }
                    }
                    catch (Exception e){
                        String ex = e.getMessage();
                    }
                }
            }
        }
        return location;
    }
}