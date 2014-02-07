package com.hdm.crowdmusic.core;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.hdm.crowdmusic.core.streaming.actions.IAction;
import com.hdm.crowdmusic.core.streaming.actions.IOnFailureHandler;
import com.hdm.crowdmusic.core.streaming.actions.SimplePostTask;
import com.hdm.crowdmusic.core.streaming.actions.Vote;
import com.hdm.crowdmusic.gui.support.NoServerResponseDialog;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private final Context context;
    private List<Track> playlist;
    private List<Track> trackList;

    private String clientIP;
    private String serverIP;

    private PropertyChangeListener clientView;

    private IOnFailureHandler noResponse = new IOnFailureHandler() {

        @Override
        public void execute() {
            new NoServerResponseDialog(context).show();
        }
    };

    public Client(Context context, String clientIP, String serverIP) {
        this.context = context;
        this.clientIP = clientIP;
        this.serverIP = serverIP;
        trackList = new ArrayList<Track>();
        playlist = new ArrayList<Track>();
    }

    public void init() {
        Log.i(Utility.LOG_TAG_MEDIA, "Init audio search...");

        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor exCursor = context.getContentResolver().query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        );

        Log.i(Utility.LOG_TAG_MEDIA, "Init audio search complete.");


        if(exCursor.moveToFirst()) {

            Log.d(Utility.LOG_TAG_MEDIA, "The following audio files have been found: ");

            int id;
            String title;
            String artist;

            int idIndex = exCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleIndex = exCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = exCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                id = exCursor.getInt(idIndex);
                title = exCursor.getString(titleIndex);
                artist = exCursor.getString(artistIndex);

                trackList.add(new Track(id, clientIP, artist, title));
                Log.d(Utility.LOG_TAG_MEDIA, id + ", " + title + ", " + artist);
            } while (exCursor.moveToNext());

            exCursor.close();
        }
        else {
            Log.d(Utility.LOG_TAG_MEDIA, "No audio files found!");
        }

        register();
    }

    public List<Track> getTrackList() {

        return trackList;
    }
    public String getClientIP() { return clientIP; }
    public String getServerIP() { return serverIP; }

    public void register() {
        SimplePostTask<String> task = new SimplePostTask<String>(getServerIP(), Constants.PORT, null, noResponse);
        task.execute(new IAction<String>() {
            @Override
            public String getPostTarget() {
                return "register";
            }

            @Override
            public String getParam() {
                return getClientIP();
            }
        });
    }
    public void unregister() {
        SimplePostTask<String> task = new SimplePostTask<String>(getServerIP(), Constants.PORT, null, noResponse);
        task.execute(new IAction<String>() {
            @Override
            public String getPostTarget() {
                return "unregister";
            }

            @Override
            public String getParam() {
                return getClientIP();
            }
        });
    }
    public void postAudio(final Track track) {
        //new PostAudioTask(serverIP, Constants.PORT).execute(track);
        SimplePostTask<Track> task = new SimplePostTask<Track>(getServerIP(), Constants.PORT, null, noResponse);
        task.execute(new IAction<Track>() {
            @Override
            public String getPostTarget() {
                return "track/post";
            }

            @Override
            public Track getParam() {
                return track;
            }
        });
    }
    public void upvoteTrack(final Vote vote) {
        SimplePostTask<Vote> task = new SimplePostTask<Vote>(getServerIP(), Constants.PORT, null, noResponse);
        task.execute(new IAction<Vote>() {
            @Override
            public String getPostTarget() {
                return "vote/up";
            }

            @Override
            public Vote getParam() {
                return vote;
            }
        });
    }
    public void downvoteTrack(final Vote vote) {
        SimplePostTask<Vote> task = new SimplePostTask<Vote>(getServerIP(), Constants.PORT, null, noResponse);
        task.execute(new IAction<Vote>() {
            @Override
            public String getPostTarget() {
                return "vote/down";
            }

            @Override
            public Vote getParam() {
                return vote;
            }
        });}

    public void notifyClientview() {
        if (this.clientView != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(this, "playlist", null, getPlaylist());
            clientView.propertyChange(event);
        }
    }

    public List<Track> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<Track> newList) {
        playlist = newList;
        notifyClientview();
    }

    public void registerClientView(PropertyChangeListener clientPlaylistView) {
        clientView = clientPlaylistView;
    }
}
