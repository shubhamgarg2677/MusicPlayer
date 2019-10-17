package com.example.shubhamg.musicplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

TextView songnameview,totalduration,currenttime;
ImageView playbutton,pausebutton,nextbutton,prevbutton;
RecyclerView recyclerView;
SeekBar seekBar;
Intent intnt;
SongInfo songInfo;
MusicPlayerService musicservice;
ArrayList<SongInfo> SongsList;
ArrayList<String> Song_Title=new ArrayList<String>();
ArrayList<String> Song_Artist_Name=new ArrayList<String>();
ArrayList<String> Song_Path=new ArrayList<String>();
ArrayList<Integer> Song_Duration=new ArrayList<Integer>();
ArrayList<Integer> Song_Id=new ArrayList<Integer>();

int i=0 , position,min=0,sec=0,count=0,l=0;
boolean musicbound=false,musicThreadFinished=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findview();
        SongsList=new ArrayList<SongInfo>();
        getpermision();
        Collections.sort(SongsList, new Comparator<SongInfo>(){
            public int compare(SongInfo a, SongInfo b){
                return a.getSong_title().compareTo(b.getSong_title());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapterclass(this,SongsList));
        musicservice=new MusicPlayerService();
        onpress();
    }

    ServiceConnection connection=new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicPlayerService.MusicBinder binder= (MusicPlayerService.MusicBinder) service;
        musicservice=binder.getService();
        musicbound=true;
      //  musicservice.setCallbacks(MainActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
     musicbound=false;
    }

    };

    @Override
    protected void onStart() {

        super.onStart();
        broadcastreceiver();

    }

    public String getlength(int length)
    {
        String v = "00:00";
        if(musicbound){
            // int length=musicservice.getDur();
        Log.e("###",""+ length);
        final int local=length/(1000);
        //seekBar.setMax(local);
        int temp=local/(60);

        Log.e("min",""+temp);
        int mod=local%60;
        Log.e("sec",""+mod);
        if(temp>=0){min=temp;}

        sec=mod;
        if(mod==0){sec=1;}
        //sec=length-(mod*60);
        Log.e("%%%",min+":"+sec);
        if(min<10&&sec<10){v="0"+min+":0"+sec+"";}
        else{
            if(sec<10) { v=""+min+":0"+sec+""; }
            else{if(min<10){ v="0"+min+":"+sec+""; }
                  else{if(min<10){ v=""+min+":"+sec+""; }}
            }

        }

        // totalduration.setText(v);
    }
    return v;
    }

    public void broadcastreceiver()
    {
        BroadcastReceiver receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("song_data")){
                position= intent.getIntExtra("song_position",20);
                Log.e("&&&","position"+position);


                if(intent!=null)
                {
                    intnt=new Intent(getApplicationContext(),MusicPlayerService.class);
                    Log.e("###","position"+position);
                    intnt.putExtra("position",position);
                    intnt.putExtra("BUNDLE",SongsList);

                    bindService(intnt,connection, Context.BIND_AUTO_CREATE);

                    playbutton.setVisibility(View.INVISIBLE);
                    pausebutton.setVisibility(View.VISIBLE);
                    startService(intnt);

//                    SongInfo songInfo1=SongsList.get(position);
//                    songnameview.setText(songInfo1.getSong_title());
                    songnameview.setEnabled(true);
                    songnameview.setSelected(true);
                }
            }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("song_data"));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        BroadcastReceiver receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent!=null){
                     int pos=intent.getIntExtra("song_pos",10);
                    int dur=intent.getIntExtra("song_dur",30);
                    SongInfo songInfo1=SongsList.get(pos);
                    songnameview.setText(songInfo1.getSong_title());

                    seekBar.setMax(dur);
                    totalduration.setText(getlength(dur));

                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            seekBar.setProgress(musicservice.getPosn());
                        }
                    },0,1000);
                     final Handler mHandler = new Handler();
//                    Make sure you update Seekbar on Contacts.Intents.UI thread
                  runOnUiThread(new Runnable() {

            @Override
            public void run() {

                    String x=getlength(musicservice.getPosn());
                    currenttime.setText(x.toString());

                    mHandler.postDelayed(this, 1000);
            }
        });


                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("song_position"));

    }

    public void findview()
    {
        seekBar=findViewById(R.id.Musicplayer_seekbar_1);
        recyclerView=findViewById(R.id.Musicplayer_recyleview_1);
        songnameview=findViewById(R.id.musicplayer_textview_1);
        playbutton=findViewById(R.id.musicplayer_imageview_1);
        pausebutton=findViewById(R.id.musicplayer_imageview_2);
        nextbutton=findViewById(R.id.musicplayer_imageview_3);
        prevbutton=findViewById(R.id.musicplayer_imageview_4);
        totalduration=findViewById(R.id.musicplayer_textview_3);
        currenttime=findViewById(R.id.musicplayer_textview_2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        musicservice.onUnbind(intnt);
        stopService(intnt);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicservice.onUnbind(intnt);
        stopService(intnt);
    }

    public void getpermision()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            UpdateSongInfo();
        }
        else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(MainActivity.this,"permisson allowed is compulsory for further proceeding",Toast.LENGTH_LONG).show();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},20);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==20){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(permissions[0]== Manifest.permission.READ_EXTERNAL_STORAGE){ UpdateSongInfo();}

            }
        }
    }

    public void UpdateSongInfo()
    {
        ContentResolver resolver=getContentResolver();
        Uri Mediastorage= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor=resolver.query(Mediastorage,null,null,null,MediaStore.Audio.Media.TITLE+" ASC");
       if(cursor!=null)
       {
           int song_id=cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int Song_Titlecolumn=cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int Song_Pathcolumn=cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int Song_Durationcolumn=cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int Song_Artistnamecolumn=cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        while (cursor.moveToNext())
        { Song_Id.add(cursor.getInt(song_id));
          Song_Title.add(cursor.getString(Song_Titlecolumn));
          Song_Path.add(cursor.getString(Song_Pathcolumn));
          Song_Artist_Name.add(cursor.getString(Song_Artistnamecolumn));
          Song_Duration.add(cursor.getInt(Song_Durationcolumn));
          SongsList.add(new SongInfo(Song_Title.get(i),Song_Path.get(i),Song_Artist_Name.get(i),Song_Duration.get(i),Song_Id.get(i)));
          i++;
        }
       }
    }
//public void setSeekBaronchange()
//{
//
//            int currentposition=0;
//            if(musicbound) {
//                for (int k = 0; k < musicservice.getDur(); k++) {
//                    try {
//                        currentposition=musicservice.getPosn();
//                        Thread.sleep(1000);
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                     seekBar.setProgress(currentposition);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (musicservice.isPng())
//                        {
//                            currenttime.setText(getlength(musicservice.getPosn()));
//                        }
//                        }
//                    });
//                }
//            }
//}

    public void onpress()
    {
     playbutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if(musicbound)
     {

         if(musicservice.isPng()==false){
             musicservice.go();
             pausebutton.setVisibility(View.VISIBLE);
             playbutton.setVisibility(View.INVISIBLE);
         }

         if(!musicservice.isPng()){playbutton.setVisibility(View.VISIBLE);}
         musicThreadFinished=false;
      }
    }
      });
       prevbutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(musicbound){
            if(!musicservice.isPng()){
                pausebutton.setVisibility(View.VISIBLE);
                playbutton.setVisibility(View.INVISIBLE); }

         musicservice.playPrev();
         //songnameview.setText(musicservice.gettitle().toString());

        }
    }
      });
        nextbutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(musicbound){
            if(!musicservice.isPng()){
                pausebutton.setVisibility(View.VISIBLE);
                playbutton.setVisibility(View.INVISIBLE); }
        musicservice.playNext();
        //songnameview.setText(musicservice.gettitle().toString());

        }
    }
        });
        pausebutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(musicbound){
            if(musicservice.isPng()){
            musicservice.pausePlayer();
            playbutton.setVisibility(View.VISIBLE);
            pausebutton.setVisibility(View.INVISIBLE);
        }
        }
    }
     });

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(musicbound) {
            seekBar.setMax(musicservice.getDur());
            if (fromUser) {
                musicservice.seek(progress);
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch( SeekBar seekBar) {
        if(musicbound){currenttime.setText(getlength(seekBar.getProgress()).toString());}
        Log.e("progress",""+getlength(seekBar.getProgress()).toString());
       //  musicservice.seek(10);
        }
     });
    }
}
