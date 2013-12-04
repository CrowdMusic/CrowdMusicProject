package com.hdm.crowdmusic.core;

import java.io.Serializable;

public class CrowdMusicTrack {

    private int id;
    private String ip;

    private String artist;
    private String trackName;

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
}
