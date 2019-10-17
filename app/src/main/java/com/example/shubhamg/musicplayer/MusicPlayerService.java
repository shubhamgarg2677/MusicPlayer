package com.example.shubhamg.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Random;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener {
   //MediaPlayer player;
   MediaPlayer player;
   MainActivity activity;
    ArrayList<SongInfo> songs;
    int songpos;
    final IBinder binder = new MusicBinder();
    String songtitle="";
    Random rand;
    SeekBar seek;
    int NOTIFY_ID=1;
    boolean shuffle=false,songplay=false;
    //private ServiceCallback serviceCallbacks;
    @Override
    public void onCreate() {
        super.onCreate();
        songpos=0;
        //random
        rand=new Random();
        activity=new MainActivity();
        //create player
        player = new MediaPlayer();
        //initialize
        initMusicPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setSong(intent.getIntExtra("position",10));
      //  Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<SongInfo> songInfos= (ArrayList<SongInfo>) intent.getSerializableExtra("BUNDLE");
        setList(songInfos);
        playSong();
       // getbroadcastintent();
        new Thread(new Runnable() {
            @Override
            public void run() {
               // activity.setSeekBaronchange();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);

    }

    private void initMusicPlayer() {
//
//        player.setWakeMode(getApplicationContext(),
//                PowerManager.PARTIAL_WAKE_LOCK);
      //  player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<SongInfo> theSongs)
    {   songs=new ArrayList<SongInfo>();
        songs=theSongs;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        player.pause();
        player.stop();
        player.release();
        return false;
    }
//    public void setCallbacks(ServiceCallback callbacks) {
//        serviceCallbacks = callbacks;
//    }

    public  void playSong()
    {

        player.reset();
        //get song
        if (songplay){

            SongInfo playSong = songs.get(songpos);
            //get title
            songtitle=playSong.getSong_title();
            //get id
            long currSong = playSong.getSong_id();
            //set uri
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);
            //set the data source
            try{
                player.setDataSource(playSong.getSong_path());
            }
            catch(Exception e){
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            player.prepareAsync();
        }
        else{Log.e("postion log","postion is null");}

    }

    public void setSong(int songIndex)
    {
        songpos=songIndex;
        songplay=true;
    }
    @Override
    public void onCompletion(MediaPlayer mp)
    {
        if(player.getCurrentPosition()>0){
            mp.reset();
            player=mp;
            playNext();
        }


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        player=mp;
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        //start playback
        mp.start();
        player=mp;

        Intent intent = new Intent("song_position");
        intent.putExtra("song_pos",songpos);
        intent.putExtra("song_dur",mp.getDuration());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Notification.Builder builder = new Notification.Builder(this);
//
//        builder.setContentIntent(pendInt)
//                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
//                .setTicker(songtitle)
//                .setOngoing(true)
//                .setContentTitle("Playing")
//                .setContentText(songtitle);
//        Notification not = builder.build();
        //startForeground(NOTIFY_ID, not);


    }

    public String gettitle()
    {
        return songtitle;
    }

    public int getPosn()
    {
        return player.getCurrentPosition();
    }

    public int getDur()
    {
        return player.getDuration();
    }

    public boolean isPng()
    {
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    //skip to previous track
    public void playPrev(){
        songpos--;
        if(songpos<0) songpos=songs.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = songpos;
            while(newSong==songpos){
                newSong=rand.nextInt(songs.size());
            }
            songpos=newSong;
        }
        else{
            songpos++;
            if(songpos>=songs.size()) songpos=0;
        }
        playSong();

    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    //toggle shuffle
    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }



    public class MusicBinder extends Binder {
        MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

}
