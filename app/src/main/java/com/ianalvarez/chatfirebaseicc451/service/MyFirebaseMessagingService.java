package com.ianalvarez.chatfirebaseicc451.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.ui.UsersActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        
        Log.d("FCM", "Mensaje recibido desde: " + remoteMessage.getFrom());

        // Si el mensaje trae una notificación (lo más común si se envía desde la consola)
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body);
        }
        
        // Si el mensaje trae "datos" (lo más común si lo enviamos por código/Cloud Functions)
        else if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            showNotification(title, body);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Nuevo token generado: " + token);
        // Este token es el que se usa para mandarle mensajes específicamente a ESTE dispositivo.
        // Lo ideal es guardar este token en Firestore dentro del documento del usuario, 
        // pero con imprimirlo basta para las pruebas manuales o envíos generales.
    }

    private void showNotification(String title, String body) {
        String channelId = "chat_notifications";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // A partir de Android Oreo (API 26) se exigen "Canales de Notificación"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, 
                    "Notificaciones de Chat", 
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Al tocar la notificación, abrimos UsersActivity
        Intent intent = new Intent(this, UsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 
                0, 
                intent, 
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Ícono por defecto
                .setContentTitle(title != null ? title : "Nuevo mensaje")
                .setContentText(body != null ? body : "Tienes un mensaje nuevo.")
                .setAutoCancel(true) // La notificación se borra al tocarla
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        // Mostramos la notificación. Usamos el tiempo actual como ID para que no se sobreescriban
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
