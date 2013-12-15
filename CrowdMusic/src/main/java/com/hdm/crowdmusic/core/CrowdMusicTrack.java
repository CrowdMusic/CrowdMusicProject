package com.hdm.crowdmusic.core;

import java.util.ArrayList;
import java.util.List;

public class CrowdMusicTrack {

    private int id;
    private String ip;

    private String artist;
    private String trackName;

    private int rating;
    private List<String> votedIPs = new ArrayList<String>();

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

    // Returns a copy, not the reference of the list
    public List<String> getVotedIPs() {
        ArrayList<String> copy = new ArrayList<String>();
        copy.addAll(votedIPs);
        return copy;
    }

    public void upvote(String ip) {
        votedIPs.add(ip);
        rating += 1;
    }

    public void downvote(String ip) {
        votedIPs.add(ip);
        rating -= 1;
    }
}
