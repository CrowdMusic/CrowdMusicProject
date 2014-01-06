package com.hdm.crowdmusic.core.streaming.actions;

public class Vote {
    private String ip;
    private int trackId;

    public Vote(int trackId, String ip) {
        this.trackId = trackId;
        this.ip = ip;
    }

    public Vote() {

    }

    public int getTrackId() {
        return trackId;
    }

    public String getIp() {
        return ip;
    }
}
