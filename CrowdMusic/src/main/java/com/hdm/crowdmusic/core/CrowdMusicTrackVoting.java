package com.hdm.crowdmusic.core;

public class CrowdMusicTrackVoting {

    public enum CATEGORY {
        UP,
        DOWN
    }

    private CrowdMusicTrack track;
    private CATEGORY category;
    private String ip;

    public CrowdMusicTrackVoting(CrowdMusicTrack track, CATEGORY category, String ip) {
        this.track = track;
        this.category = category;
        this.ip = ip;
    }

    public CATEGORY getCategory() {
        return category;
    }

    public void setCategory(CATEGORY category) {
        this.category = category;
    }

    public CrowdMusicTrack getTrack() {
        return track;
    }

    public void setTrack(CrowdMusicTrack track) {
        this.track = track;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
