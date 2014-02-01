package com.hdm.crowdmusic.core.streaming.actions;

import com.hdm.crowdmusic.core.CrowdMusicTrack;

import java.util.List;

public class CrowdMusicTracklist {
    private final List<CrowdMusicTrack> list;

    public CrowdMusicTracklist(List<CrowdMusicTrack> list) {
        this.list = list;
    }

    public List<CrowdMusicTrack> getList() {
        return list;
    }
}
