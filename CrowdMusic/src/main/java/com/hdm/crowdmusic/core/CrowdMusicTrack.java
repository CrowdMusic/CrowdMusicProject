package com.hdm.crowdmusic.core;

import android.graphics.Bitmap;

public class CrowdMusicTrack {

    private int id;
    private String ip;

    private String artist;
    private String trackName;
    private long duration;
    private Bitmap cover;

    public CrowdMusicTrack(int id, String ip, String artist, String trackName) {
        this.id = id;
        this.ip = ip;
        this.artist = artist;
        this.trackName = trackName;
    }

    public int getId() { return id; }

    public String getIp() { return ip; }

    public String getArtist(){

        return artist;
    }

    public String getTrackName(){

        return trackName;
    }

    public long getDuration(){

       return duration;
   }

    public Bitmap getCover(){

       return cover;
   }
}
