package com.pratamatechnocraft.tokason.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pratamatechnocraft.tokason.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String CHANNEL_ID = "TOKASON_NOTIFICATION";
    Intent resultIntent;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        storeRegIdInPref(s);


        Log.e( TAG, "sendRegistrationToServer: " + s );

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", s);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence channelName = getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(123, builder.build());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        //parsing json data
        /*String title = remoteMessage.getData().get( "title" );
        String message = remoteMessage.getData().get( "message" );
        String jenis_notif = remoteMessage.getData().get( "jenis_notif" );
        String id = remoteMessage.getData().get( "id" );

        Log.d( "TAG", "onMessageReceived: "+jenis_notif );

        //creating MyNotificationManager object
        NotificationUtils mNotificationManager = new NotificationUtils(getApplicationContext());

        if (jenis_notif.equals( "surat masuk" )){
            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("idSuratMasuk", id);
        }else if(jenis_notif.equals( "surat keluar" )){
            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("idSuratKeluar", id);
        }else{
            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("idDisposisi", id);
        }

        //if there is no image

        //displaying small notification
        mNotificationManager.showSmallNotification(title, message, resultIntent);
        mNotificationManager.playNotificationSound();*/
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(Integer.parseInt("Tokason Notification"));
            String description = getString(Integer.parseInt("Notifikasi Untuk Aplikasi Tokason"));
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
