package com.hdm.crowdmusic.core;

import android.util.Log;

import com.hdm.crowdmusic.util.Utility;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class CrowdMusicPlaylist {

    private final int QUEUE_LENGTH = 20;
    private PriorityQueue<CrowdMusicTrack> playlist;

    private static CrowdMusicPlaylist instance;
    public static CrowdMusicPlaylist getInstance() {
        if (instance == null) {
            instance = new CrowdMusicPlaylist();
            return instance;
        } else {
            return instance;
        }
    }

    private CrowdMusicPlaylist() {
        playlist = new PriorityQueue<CrowdMusicTrack>(QUEUE_LENGTH, new ScoreComparator());
    }

    public CrowdMusicTrack getNextTrack() {
        if (playlist.size() > 0) {
            CrowdMusicTrack nextTrack = playlist.remove();
            return nextTrack;
        }
        Log.i(Utility.LOG_TAG_MEDIA, "Playlist is empty!");
        return null;
    }

    public void addTrack(CrowdMusicTrack track) {
        Log.i(Utility.LOG_TAG_MEDIA, "The following track was added to the playlist: ");
        Log.i(Utility.LOG_TAG_MEDIA, "ID: " + track.getId() + " | IP: " + track.getIp() + " | Artist: " + track.getArtist() + " | Track: " + track.getTrackName());
        playlist.add(track);
        Log.i(Utility.LOG_TAG_MEDIA, "The queue now contains the following elements: ");
        Iterator<CrowdMusicTrack> iterator = playlist.iterator();
        while (iterator.hasNext()) {
            CrowdMusicTrack t = iterator.next();
            Log.i(Utility.LOG_TAG_MEDIA, "ID: " + t.getId() + " | IP: " + t.getIp() + " | Artist: " + t.getArtist() + " | Track: " + t.getTrackName());
        }
    }

    public void removeTrack(CrowdMusicTrack track) {
        if (playlist.contains(track)) {
            playlist.remove(track);
        }
    }

    class ScoreComparator implements Comparator<CrowdMusicTrack> {
        @Override
        public int compare(CrowdMusicTrack lhs, CrowdMusicTrack rhs) {
            if ((lhs == null) && (rhs != null)) {
                return -1;
            } else if ((rhs == null) && (lhs != null)) {
                return 1;
            } else if (lhs.getRating() < rhs.getRating()) {
                return -1;
            } else if (lhs.getRating() > rhs.getRating()) {
                return 1;
            }
            return 0;
        }
    }
}
