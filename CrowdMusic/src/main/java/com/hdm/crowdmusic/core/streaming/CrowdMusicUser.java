package com.hdm.crowdmusic.core.streaming;


import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.core.streaming.actions.Vote;

import java.util.ArrayList;

public class CrowdMusicUser{

    private String IpAddress;
    private ArrayList<CrowdMusicTrack> tracks;
    private ArrayList<Vote> votes;
    private long registeredSince;

    public CrowdMusicUser(String IpAddress)
    {
        this.IpAddress = IpAddress;
        this.registeredSince = new java.util.Date().getTime(); //Might be useful sometime

        this.tracks = new ArrayList<CrowdMusicTrack>();
        this.votes = new ArrayList<Vote>();
    }

    public void addTrack(CrowdMusicTrack track){
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
        final CrowdMusicUser other = (CrowdMusicUser) obj;

        if (!this.getIp().equals(other.getIp())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(getIp().replaceAll("\\.", ""));
    }

}
