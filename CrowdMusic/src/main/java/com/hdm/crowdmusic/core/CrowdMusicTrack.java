package com.hdm.crowdmusic.core;

import android.graphics.Bitmap;

public class CrowdMusicTrack {

    private String artist;
    private String trackName;
    private long duration;
    private Bitmap cover;

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


    public void setArtist(String artist){

        this.artist = artist;
    }

    public void setTrackname(String trackname){

        this.trackName = trackname;
    }

    public void setDuration(long duration){

        this.duration = duration;
    }

    public void setCover(Bitmap cover){

        this.cover = cover;
    }


}
