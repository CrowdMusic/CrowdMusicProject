package com.hdm.crowdmusic.core.streaming;


import com.hdm.crowdmusic.core.Track;
import com.hdm.crowdmusic.core.streaming.actions.Vote;

import java.util.ArrayList;

public class User {

    private String IpAddress;
    private ArrayList<Track> tracks;
    private ArrayList<Vote> votes;
    private long registeredSince;

    public User(String IpAddress)
    {
        this.IpAddress = IpAddress;
        this.registeredSince = new java.util.Date().getTime(); //Might be useful sometime

        this.tracks = new ArrayList<Track>();
        this.votes = new ArrayList<Vote>();
    }

    public void addTrack(Track track){
        this.tracks.add(track);
    }

    public void addVote(Vote vote){
        this.votes.add(vote);
    }

    public String getIp(){
        return this.IpAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;

        if (!this.getIp().equals(other.getIp())) {
            return false;
        }
        return true;
    }
}
