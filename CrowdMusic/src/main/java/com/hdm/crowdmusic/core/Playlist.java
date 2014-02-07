package com.hdm.crowdmusic.core;

import android.util.Log;
import com.hdm.crowdmusic.core.streaming.User;
import com.hdm.crowdmusic.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Playlist {

    private ArrayList<Track> playlist;
    private Comparator<Track> comparator = new TrackComparator();
    private Server server;

    private Playlist() {
        playlist = new ArrayList<Track>();
    }
    public Playlist(Server server) {
        this();
        this.server = server;
    }

    public Track getNextTrack() {
        if (playlist.size() > 0) {
            Track nextTrack = playlist.remove(0);
            sortPlaylist();
            notifyListener();
            return nextTrack;
        } else {
            Log.i(Utility.LOG_TAG_MEDIA, "Playlist is empty!");
            return null;
        }
    }

    public void addTrack(Track track) {
        Log.i(Utility.LOG_TAG_MEDIA, "The following track was added to the playlist: ");
        Log.i(Utility.LOG_TAG_MEDIA, "ID: " + track.getId() + " | IP: " + track.getIp() + " | Artist: " + track.getArtist() + " | Track: " + track.getTrackName());
        playlist.add(track);
        sortPlaylist();
        notifyListener();
        Log.i(Utility.LOG_TAG_MEDIA, "The queue now contains the following elements: ");
        for (Track t : playlist) {
            Log.i(Utility.LOG_TAG_MEDIA, "ID: " + t.getId() + " | IP: " + t.getIp() + " | Artist: " + t.getArtist() + " | Track: " + t.getTrackName());
        }
    }

    public void removeTrack(Track track) {
        if (playlist.contains(track)) {
            playlist.remove(track);
            sortPlaylist();
            notifyListener();
        }
    }

    public void upvote(int id, String ip) {
        Track track = getFromPlaylistById(id);
        if (track != null) {
            if (isServer(ip) || notAlreadyVoted(track, ip)) {
                track.upvote(ip);
                sortPlaylist();
                notifyListener();
            }
        }
    }

    public void downvote(int id, String ip) {
        Track track = getFromPlaylistById(id);
        if (track != null) {
            if (isServer(ip) || notAlreadyVoted(track, ip)) {
                track.downvote(ip);
                sortPlaylist();
                notifyListener();
            }
        }
    }

    public Track getFromPlaylistById(int id) {
        for(Track track: playlist) {
            if (track.getId() == id) {
                return track;
            }
        }
        return null;
    }

    public List<Track> getPlaylist() {
        List<Track> copy = new ArrayList<Track>();
        copy.addAll(playlist);
        return copy;
    }

    public void notifyListener() {
        if (server != null) {
            server.notifyAllClients();
        }
    }

    private void sortPlaylist() {
        Collections.sort(playlist, comparator);
    }

    public void removeTracks(User user) {
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).getIp().equals(user.getIp())) {
                playlist.remove(i);
            }
        }
        notifyListener();
    }

    private boolean isServer(String ip) {
        return server.getServerIP().equals(ip);
    }

    private boolean notAlreadyVoted(Track track, String ip) {
        return !track.alreadyVoted(ip);
    }

    private class TrackComparator implements Comparator<Track> {
        @Override
        public int compare(Track lhs, Track rhs) {
            return lhs.compareTo(rhs);
        }
    }
}
