package pe.com.cmacica.flujocredito.Utilitarios;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtil {

    public static String CHANNEL_UPLOAD_IMAGE_ID = "UPLOAD_IMAGE";


    public static void createNotificationChannel(
            String channelId,
            String name,
            String descriptionText,
            NotificationManager notificationManager
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    channelId, name, importance
            );
            channel.setDescription(descriptionText);
            notificationManager.createNotificationChannel(channel);

        }

    }


    public static Notification createNotification(
            Context context,
            String channelId,
            int icon,
            String textTitle,
            String textContent,
            Boolean onGoing
    ) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(onGoing)
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId);
        }

        return builder.build();

    }

}
