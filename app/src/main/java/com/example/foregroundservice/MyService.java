package com.example.foregroundservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.foregroundservice.MyAppliction.CHANEL_ID;

public class MyService extends Service {

    public MediaPlayer mediaPlayer;
    public static final int ACTION_PAUSE=1;
    public static final int ACTION_RESUME=2;
    public static final int ACTION_CLEAR=3;
    public static final int ACTION_START=4;
    public boolean isPlaying;
    public Song mSong;




    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("aaa","MyService onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String str_data_intent=intent.getStringExtra("key_data_intent");
        //sendNotification(str_data_intent);
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
            Song song = (Song) bundle.get("object_song");
            if(song!=null){
                mSong=song;
                startMusic(song);
                sendNotification(song);
            }

        }
        int actionMusic=intent.getIntExtra("action_music_service",0);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;
    }


    private void handleActionMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                 resumeMusic();
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                break;

        }
    }
    private void startMusic(Song song) {
        if(mediaPlayer==null){
            mediaPlayer= MediaPlayer.create(getApplicationContext(),song.getResource());
        }
        mediaPlayer.start();
        isPlaying=true;
        sendNotification(mSong);
        sendActionToActivity(ACTION_START);
    }
    private void pauseMusic(){
        if(mediaPlayer!=null && isPlaying){
            mediaPlayer.pause();
            isPlaying=false;
            sendNotification(mSong);
            sendActionToActivity(ACTION_PAUSE);
        }
    }
    private void resumeMusic(){
        if(mediaPlayer!=null && !isPlaying){
            mediaPlayer.start();
            isPlaying=true;
            sendNotification(mSong);
            sendActionToActivity(ACTION_RESUME);
        }
    }


    private void sendNotification(Song song) {
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=
                PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),song.getImage());

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.tv_title_song,song.getTitle());
        remoteViews.setTextViewText(R.id.tv_single_song,song.getSingle());
        remoteViews.setImageViewBitmap(R.id.imgSong,bitmap);

        remoteViews.setImageViewResource(R.id.img_play_or_pause,R.drawable.ic_baseline_pause_24);


        if(isPlaying){
            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause,getPendingIntent(this,ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.img_play_or_pause,R.drawable.ic_baseline_pause_24);

        }else{
            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause,getPendingIntent(this,ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.img_play_or_pause,R.drawable.ic_play);
        }
        remoteViews.setOnClickPendingIntent(R.id.img_clear,getPendingIntent(this,ACTION_CLEAR));
        remoteViews.setImageViewResource(R.id.img_clear,R.drawable.ic_clea);

        Notification notification= new NotificationCompat.Builder(this,CHANEL_ID)
                .setSmallIcon(R.drawable.ic_music_note_24)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();
        startForeground(1,notification);
    }
    private PendingIntent getPendingIntent(Context context,int action){
        Intent intent = new Intent(this,MyReceiver.class);
        intent.putExtra("action_music",action);
        return PendingIntent.getBroadcast(context.getApplicationContext(),action,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("aaa","MyService onDestroy");
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    private void sendActionToActivity(int action){
        Intent intent = new Intent("send_data_to_activity");
        Bundle bundle= new Bundle();
        bundle.putSerializable("object_song",mSong);
        bundle.putBoolean("status_player",isPlaying);
        bundle.putInt("action_music",action);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}
