package com.hdm.crowdmusic.core.streaming.actions;

import com.hdm.crowdmusic.core.Track;

import java.util.List;

public class Tracklist {
    private final List<Track> list;

    public Tracklist(List<Track> list) {
        this.list = list;
    }

    public List<Track> getList() {
        return list;
    }
}
