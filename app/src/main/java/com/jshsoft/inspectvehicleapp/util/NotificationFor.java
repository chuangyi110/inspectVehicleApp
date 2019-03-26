package com.jshsoft.inspectvehicleapp.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jshsoft.inspectvehicleapp.R;

public class NotificationFor {
    /*
     * 简单的发送通知
     */
    public static void showNotification(String TAG, Context context) {
        String channelId = "channel_chat";
        Log.i(TAG,"this.is.channel");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.icon_see_pass)
                .setContentTitle("检车宝")
                .setContentText("程序后台运行")
                .build();
        int i = 1;
        notificationManager.notify(i++, notification);
    }
}
