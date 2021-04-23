package pe.com.cmacica.flujocredito.ViewModel.NuevoExpedienteCredito.Manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.text.style.IconMarginSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import pe.com.cmacica.flujocredito.AgenteServicio.RESTService;
import pe.com.cmacica.flujocredito.AgenteServicio.SrvCmacIca;
import pe.com.cmacica.flujocredito.AgenteServicio.VolleyFutureSingleton;
import pe.com.cmacica.flujocredito.R;
import pe.com.cmacica.flujocredito.Utilitarios.Constantes;
import pe.com.cmacica.flujocredito.Utilitarios.NotificationUtil;

public class UploadImageWorker extends Worker {

    public static final String TAG = "UploadImageWorker";

    private Context context;

    public UploadImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        return uploadImage();
    }

    private Result uploadImage() {

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        try {

            NotificationUtil.createNotificationChannel(
                    NotificationUtil.CHANNEL_UPLOAD_IMAGE_ID,
                    "AddExpediente",
                    "Agregar Expediente",
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE) )
            );

            Notification notification = NotificationUtil.createNotification(
                    context,
                    NotificationUtil.CHANNEL_UPLOAD_IMAGE_ID,
                    R.mipmap.ic_launcher,
                    "CrediMovil",
                    "Enviando información de expediente de credito",
                    true
            );


            notificationManagerCompat.notify(0, notification);

            int idCar = getInputData().getInt("idCar", 0);
            String codigoCliente = getInputData().getString("codigoCliente");
            String codigoCuenta = getInputData().getString("codigoCuenta");
            String image = context.getSharedPreferences(Constantes.SHARED_PREF_NUEVO_EXPEDIENTE_CREDITO, Context.MODE_PRIVATE)
                    .getString(Constantes.PREF_IMAGE_SERVER, "");
            String user = getInputData().getString("user");
            int tipoCodigo = getInputData().getInt("tipoCodigo", 0);

            JSONObject json = new JSONObject();
            json.put("nIdCar", idCar);
            json.put("cPerscod", codigoCliente);
            json.put("cCtaCod", codigoCuenta);
            json.put("ccImagen", image);
            json.put("Usuario", user);
            json.put("cTipoCod", tipoCodigo);

            HashMap<String, String> cabeceras = new HashMap<>();

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    SrvCmacIca.SAVE_EXPEDIENTE,
                    json.toString(),
                    future,
                    future
            );

            VolleyFutureSingleton.getInstance(getApplicationContext())
                    .addToRequestQueue(request);

            JSONObject response = future.get(60, TimeUnit.SECONDS);

            if (response.getBoolean("IsCorrect")) {
                Log.d(TAG, "uploadImage: success");
                notificationManagerCompat.cancel(0);
                return Result.success();
            } else {
                Log.d(TAG, "uploadImage: error: " + response.getString("Message"));
                notificationManagerCompat.cancel(0);
                return Result.failure();
            }

        } catch (Exception exception) {
            Log.d(TAG, "uploadImage: error: " + exception.getMessage());
            notificationManagerCompat.cancel(0);
            return Result.failure();
        }

    }

}
