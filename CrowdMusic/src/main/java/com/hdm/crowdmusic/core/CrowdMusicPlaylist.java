package com.hdm.crowdmusic.core;

import android.util.Log;
import com.hdm.crowdmusic.core.streaming.actions.PostPlaylistTask;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class CrowdMusicPlaylist {

    public List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    private final int QUEUE_LENGTH = 20;
    private PriorityQueue<CrowdMusicTrack> playlist;

    public CrowdMusicPlaylist() {
        playlist = new PriorityQueue<CrowdMusicTrack>(QUEUE_LENGTH, new ScoreComparator());
    }

    public CrowdMusicTrack getNextTrack() {
        if (playlist.size() > 0) {
            CrowdMusicTrack nextTrack = playlist.remove();
            notifyListener();
            return nextTrack;
        }
        Log.i(Utility.LOG_TAG_MEDIA, "Playlist is empty!");
        return null;
    }

    public void addTrack(CrowdMusicTrack track) {
        Log.i(Utility.LOG_TAG_MEDIA, "The following track was added to the playlist: ");
        Log.i(Utility.LOG_TAG_MEDIA, "ID: " + track.getId() + " | IP: " + track.getIp() + " | Artist: " + track.getArtist() + " | Track: " + track.getTrackName());
        playlist.add(track);
        notifyListener();
        Log.i(Utility.LOG_TAG_MEDIA, "The queue now contains the following elements: ");
        for (CrowdMusicTrack t : playlist) {
            Log.i(Utility.LOG_TAG_MEDIA, "ID: " + t.getId() + " | IP: " + t.getIp() + " | Artist: " + t.getArtist() + " | Track: " + t.getTrackName());
        }
    }

    public void removeTrack(CrowdMusicTrack track) {
        if (playlist.contains(track)) {
            playlist.remove(track);
            notifyListener();
        }
    }

    public void addListener(PropertyChangeListener listener) {
        listeners.add(listener);
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

    public void upvote(int id, String ip) {
        CrowdMusicTrack track = getFromPlaylistById(id);
        if (track != null) {
            track.upvote(ip);
            notifyListener();
        }
    }
    public void downvote(int id, String ip) {
        CrowdMusicTrack track = getFromPlaylistById(id);
        if (track != null) {
            track.downvote(ip);
            notifyListener();
        }
    }

    public CrowdMusicTrack getFromPlaylistById(int id) {
        for(CrowdMusicTrack track: playlist) {
            if (track.getId() == id) {
                return track;
            }
        }
        return null;
    }

    public List<CrowdMusicTrack> getPlaylist() {
        List<CrowdMusicTrack> list = new ArrayList<CrowdMusicTrack>();
        list.addAll(playlist);
        return list;

    }
    public void setPlaylist(List<CrowdMusicTrack> list) {
        playlist.clear();
        playlist.addAll(list);
    }
    // Ugly as hell, but it works
    public void notifyListener() {
        for (PropertyChangeListener listener: listeners) {
            if (listener == null) return;
            listener.propertyChange(new PropertyChangeEvent(this, "tracklist", null, getPlaylist()));
        }

        List<String> alreadyPostedIPs = new ArrayList<String>();
        for (CrowdMusicTrack track: playlist) {
            String clientIp = track.getIp();
            if (alreadyPostedIPs.contains(clientIp)) {
                // do nothing...
            } else {
                new PostPlaylistTask(clientIp, Constants.PORT).execute(getPlaylist());
                alreadyPostedIPs.add(clientIp);
            }
        }
    }
}
