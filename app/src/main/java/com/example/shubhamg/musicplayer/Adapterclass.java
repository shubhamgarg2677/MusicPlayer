package com.example.shubhamg.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class Adapterclass extends RecyclerView.Adapter<Adapterclass.ViewHolder> {
    Context context;
    SongInfo songInfo;
    ArrayList<SongInfo> list;
    MediaPlayer player;
    MusicPlayerService service;
    public Adapterclass(Context context,ArrayList<SongInfo> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.adapterclasslayout,parent,false);
        return new ViewHolder(view);
    }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        songInfo=list.get(position);
        holder.tv1.setText(songInfo.getSong_title().toString());
        holder.tv2.setText(songInfo.getSong_artist().toString());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("song_data");
                intent.putExtra("song_position",position);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                Log.e("@@@@","position"+position);
                //onpress(songInfo);
            //    service=new MusicPlayerService();
              //  service.setSong(position);
               // service.onCreate();
                //service.playSong();

            }
        });

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder
    {
     TextView tv1,tv2;
     ImageView img1,img2;
     CardView card;
        public ViewHolder(View itemView) {
            super(itemView);
            tv1=itemView.findViewById(R.id.recycle_tv_1);
            tv2=itemView.findViewById(R.id.recycle_tv_2);
            img1=itemView.findViewById(R.id.recycle_img_2);
            img2=itemView.findViewById(R.id.recycle_img_3);
            card=itemView.findViewById(R.id.recycle_cv_1);
        }
    }
}
