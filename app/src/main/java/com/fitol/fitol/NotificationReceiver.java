package com.fitol.fitol;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import com.fitol.FitOl.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String notificationType = intent.getStringExtra("notification_type");

        if ("su".equals(notificationType) && preferences.getBoolean("su_hatirlatici", false)) {
            bildirimGonder(context, "Su İçmeyi Unutma!", "Suyu içmeyi unutma, sağlıklı kal!", ProfileFragment.class, R.drawable.ic_water);
        } else if ("egzersiz".equals(notificationType) && preferences.getBoolean("egzersiz_hatirlatici", false)) {
            bildirimGonder(context, "Egzersiz Zamanı!", "Harekete geçme zamanı geldi!", ProfileFragment.class, R.drawable.ic_fitness);
        }
    }

    public static void bildirimGonder(Context context, String title, String content, Class<?> targetActivity, int smallIcon) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "hatirlatma_kanali";
        String channelName = "Hatırlatmalar";


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }


        Intent hedefIntent = new Intent(context, targetActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, hedefIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Bildirim özelliklerini tanımladım
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(Color.BLUE);

        notificationManager.notify(0, builder.build());
    }
}