package com.hdm.crowdmusic.core;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.InetAddress;

public class CrowdMusicTrack {

    private int id;
    private Uri uri;
    private InetAddress inetAddress;

    private String artist;
    private String trackName;
    private long duration;
    private Bitmap cover;

    public CrowdMusicTrack(int id, Uri uri, InetAddress inetAddress, String artist, String trackName) {
        this.id = id;
        this.uri = uri;
        this.inetAddress = inetAddress;
        this.artist = artist;
        this.trackName = trackName;
    }

    public int getId() { return id; }

    public Uri getUri() {
        return uri;
    }

    public InetAddress getInetAddress() { return inetAddress; }

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
