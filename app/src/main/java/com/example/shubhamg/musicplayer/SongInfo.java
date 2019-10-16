package com.example.shubhamg.musicplayer;

import java.io.Serializable;

public class SongInfo implements Serializable{
int Duration,song_id;
String song_path,song_title,song_artist;
    public SongInfo(String song_title,String song_path,String song_artist,int Duration,int song_id)
    {
this.song_title=song_title;
this.song_artist=song_artist;
this.song_path=song_path;
this.Duration=Duration;
this.song_id=song_id;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public int getDuration() {
        return Duration;
    }

    public String getSong_artist() {
        return song_artist;
    }

    public String getSong_path() {
        return song_path;
    }

    public String getSong_title() {
        return song_title;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public void setSong_artist(String song_artist) {
        this.song_artist = song_artist;
    }

    public void setSong_path(String song_path) {
        this.song_path = song_path;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }
}
