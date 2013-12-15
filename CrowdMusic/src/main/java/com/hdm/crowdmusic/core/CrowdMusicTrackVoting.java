package com.hdm.crowdmusic.core;

public class CrowdMusicTrackVoting {

    public enum CATEGORY {
        UP,
        DOWN
    }

    private CrowdMusicTrack track;
    private CATEGORY category;

    public CrowdMusicTrackVoting(CrowdMusicTrack track, CATEGORY category) {
        track = track;
        this.category = category;
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
}
