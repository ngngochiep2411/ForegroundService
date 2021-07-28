package com.example.foregroundservice;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyAppliction extends Application {
    public static final String CHANEL_ID="channel_service_example";
    public static final String CHANEL_NAME="channel_service_example";
    @Override
    public void onCreate() {
        super.onCreate();
        createChanelNotificaion();
    }

    private void createChanelNotificaion() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=
                    new NotificationChannel(CHANEL_ID,CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            channel.setSound(null,null);
            if(manager!=null){
                manager.createNotificationChannel(channel);
            }

        }
    }

}
