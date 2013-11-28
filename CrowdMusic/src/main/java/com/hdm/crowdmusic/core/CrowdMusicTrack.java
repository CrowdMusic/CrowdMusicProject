package com.hdm.crowdmusic.core;

import android.graphics.Bitmap;
import android.net.Uri;

public class CrowdMusicTrack {

    private int id;
    private Uri uri;

    private String artist;
    private String trackName;
    private long duration;
    private Bitmap cover;

    public CrowdMusicTrack(int id, Uri uri, String artist, String trackName) {
        this.id = id;
        this.uri = uri;
        this.artist = artist;
        this.trackName = trackName;
    }

    public int getId() { return id; }

    public Uri getUri() {
        return uri;
    }

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
