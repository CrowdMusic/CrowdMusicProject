package com.hdm.crowdmusic.core;

import android.util.Log;

import com.hdm.crowdmusic.core.streaming.actions.CrowdMusicTracklist;
import com.hdm.crowdmusic.core.streaming.actions.ICrowdMusicAction;
import com.hdm.crowdmusic.core.streaming.actions.PostPlaylistTask;
import com.hdm.crowdmusic.core.streaming.actions.SimplePostTask;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class CrowdMusicPlaylist {

    private ArrayList<CrowdMusicTrack> playlist;
    private Comparator<CrowdMusicTrack> comparator = new TrackComparator();

    public CrowdMusicPlaylist() {
        playlist = new ArrayList<CrowdMusicTrack>();
    }

    public CrowdMusicTrack getNextTrack() {
        if (playlist.size() > 0) {
            CrowdMusicTrack nextTrack = playlist.remove(0);
            sortPlaylist();
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
        sortPlaylist();
        notifyListener();
        Log.i(Utility.LOG_TAG_MEDIA, "The queue now contains the following elements: ");
        for (CrowdMusicTrack t : playlist) {
            Log.i(Utility.LOG_TAG_MEDIA, "ID: " + t.getId() + " | IP: " + t.getIp() + " | Artist: " + t.getArtist() + " | Track: " + t.getTrackName());
        }
    }

    public void removeTrack(CrowdMusicTrack track) {
        if (playlist.contains(track)) {
            playlist.remove(track);
            sortPlaylist();
            notifyListener();
        }
    }

    public void upvote(int id, String ip) {
        CrowdMusicTrack track = getFromPlaylistById(id);
        if (track != null) {
            track.upvote(ip);
            sortPlaylist();
            notifyListener();
        }
    }
    public void downvote(int id, String ip) {
        CrowdMusicTrack track = getFromPlaylistById(id);
        if (track != null) {
            track.downvote(ip);
            sortPlaylist();
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
        List<CrowdMusicTrack> copy = new ArrayList<CrowdMusicTrack>();
        copy.addAll(playlist);
        return copy;
    }

    // Ugly as hell, but it works
    public void notifyListener() {

        List<String> alreadyPostedIPs = new ArrayList<String>();
        for (CrowdMusicTrack track: playlist) {
            String clientIp = track.getIp();
            if (alreadyPostedIPs.contains(clientIp)) {
                // do nothing...
            } else {

                SimplePostTask<CrowdMusicTracklist> task = new SimplePostTask<CrowdMusicTracklist>(clientIp, Constants.PORT);
                task.execute(new ICrowdMusicAction<CrowdMusicTracklist>() {
                    @Override
                    public String getPostTarget() {
                        return "postplaylist";
                    }

                    @Override
                    public CrowdMusicTracklist getParam() {
                        return new CrowdMusicTracklist(getPlaylist());
                    }
                });
                alreadyPostedIPs.add(clientIp);
            }
        }
    }

    private void sortPlaylist() {
        Collections.sort(playlist, comparator);
    }

    private class TrackComparator implements Comparator<CrowdMusicTrack> {
        @Override
        public int compare(CrowdMusicTrack lhs, CrowdMusicTrack rhs) {
            return lhs.compareTo(rhs);
        }
    }
}
