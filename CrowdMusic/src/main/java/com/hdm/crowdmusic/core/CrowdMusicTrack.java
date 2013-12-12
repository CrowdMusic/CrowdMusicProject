package com.hdm.crowdmusic.core;

public class CrowdMusicTrack {

    private int id;
    private String ip;

    private String artist;
    private String trackName;

    private int rating;

    public CrowdMusicTrack(int id, String ip, String artist, String trackName) {
        this.id = id;
        this.ip = ip;
        this.artist = artist;
        this.trackName = trackName;

        rating = 1;
    }

    public int getId() { return id; }

    public String getIp() { return ip; }

    public String getArtist(){

        return artist;
    }

    public String getTrackName(){

        return trackName;
    }

    public int getRating() { return rating; }

    public void upvote() {
        rating =+ 1;
    }

    public void downvote() {
        rating -= 1;
    }
}
