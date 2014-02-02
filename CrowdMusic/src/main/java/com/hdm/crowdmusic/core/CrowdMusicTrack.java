package com.hdm.crowdmusic.core;

import java.util.HashSet;
import java.util.Set;

public class CrowdMusicTrack implements Comparable<CrowdMusicTrack>{

    private int id;
    private String ip;

    private String artist;
    private String trackName;

    private int rating;

    private Set<String> iPsThatAlreadyVoted = new HashSet<String>();

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

    public void upvote(String ip) {
        rating += 1;
        iPsThatAlreadyVoted.add(ip);
    }

    public void downvote(String ip) {
        rating -= 1;
        iPsThatAlreadyVoted.add(ip);
    }

    @Override
    public int compareTo(CrowdMusicTrack that) {
        return  that.getRating() - this.getRating();
    }

    public boolean alreadyVoted(String ip) {
        return iPsThatAlreadyVoted.contains(ip);
    }
}
