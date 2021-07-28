package com.example.foregroundservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.foregroundservice.MyService.ACTION_CLEAR;
import static com.example.foregroundservice.MyService.ACTION_PAUSE;
import static com.example.foregroundservice.MyService.ACTION_RESUME;
import static com.example.foregroundservice.R.drawable.binz;

public class MainActivity extends AppCompatActivity {

    Button btnStart,btnStop;
    private RelativeLayout layoutBottom;
    private ImageView imgSong,imgPlayOrPause,imgClear;
    private TextView tvTitleSong,tvSingleSong;
    private Song mSong;
    private boolean isPlaying;
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            if(bundle==null){
                return;
            }
            mSong= (Song) bundle.get("object_song");
            isPlaying=bundle.getBoolean("status_player");
            int actionMusic=bundle.getInt("action_music");

            handleLayoutMusic(actionMusic);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("send_data_to_activity"));


        btnStart= findViewById(R.id.btn_start_service);
        btnStop= findViewById(R.id.btn_stop_service);
        layoutBottom=findViewById(R.id.layout_bottom);
        imgSong=findViewById(R.id.imgSong);
        imgPlayOrPause=findViewById(R.id.img_play_or_pause);
        imgClear=findViewById(R.id.img_clear);
        tvTitleSong=findViewById(R.id.tv_title_song);
        tvSingleSong=findViewById(R.id.tv_single_song);



        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStartService();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStopService();
            }
        });


    }

    private void handleLayoutMusic(int action) {
        switch (action){
            case MyService.ACTION_START:
                layoutBottom.setVisibility(View.VISIBLE);
                showInfoSong();
                setTatusButtonPlayOrPause();
                break;
            case MyService.ACTION_PAUSE:
                setTatusButtonPlayOrPause();
                break;
            case MyService.ACTION_RESUME:
                setTatusButtonPlayOrPause();
                break;
            case MyService.ACTION_CLEAR:
                layoutBottom.setVisibility(View.GONE);
                break;
        }

    }
    private void showInfoSong(){
        if(mSong==null){
            return;
        }
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),mSong.getImage());
        //imgSong.setImageResource(R.mipmap.ic_launcher);
        imgSong.setImageResource(mSong.getImage());
        tvTitleSong.setText(mSong.getTitle());
        tvSingleSong.setText(mSong.getSingle());

        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    sendActionToService(ACTION_PAUSE);
                }else {
                    sendActionToService(ACTION_RESUME);
                }
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(ACTION_CLEAR);
            }
        });
    }
    private void setTatusButtonPlayOrPause(){
        if(isPlaying){
            imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24);
        }else{
            imgPlayOrPause.setImageResource(R.drawable.ic_play);
        }
    }
    private void sendActionToService(int action){
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("action_music_service",action);
        startService(intent);
    }




    private void clickStopService() {
        Intent intent = new Intent(this,MyService.class);
        stopService(intent);
    }

    private void clickStartService() {
        Intent intent = new Intent(this,MyService.class);
        Song song= new Song("Big city boy","BinZ", binz,R.raw.bigcityboi);
        Bundle bundle=new Bundle();
        bundle.putSerializable("object_song",song);
        intent.putExtras(bundle);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}