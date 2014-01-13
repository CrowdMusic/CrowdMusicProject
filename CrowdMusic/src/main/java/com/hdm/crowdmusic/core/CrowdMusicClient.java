package com.hdm.crowdmusic.core;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import com.hdm.crowdmusic.core.streaming.actions.ICrowdMusicAction;
import com.hdm.crowdmusic.core.streaming.actions.SimplePostTask;
import com.hdm.crowdmusic.core.streaming.actions.Vote;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CrowdMusicClient {

    private Context context;
    private List<CrowdMusicTrack> playlist;
    private List<CrowdMusicTrack> trackList;

    private String clientIP;
    private String serverIP;

    private PropertyChangeListener clientView;

    public CrowdMusicClient(Context context, String clientIP, String serverIP) {
        this.context = context;
        this.clientIP = clientIP;
        this.serverIP = serverIP;
        trackList = new ArrayList<CrowdMusicTrack>();
        playlist = new ArrayList<CrowdMusicTrack>();
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

                trackList.add(new CrowdMusicTrack(id, clientIP, artist, title));
                Log.d(Utility.LOG_TAG_MEDIA, id + ", " + title + ", " + artist);
            } while (exCursor.moveToNext());

            exCursor.close();
        }
        else {
            Log.d(Utility.LOG_TAG_MEDIA, "No audio files found!");
        }

        register();
    }

    public List<CrowdMusicTrack> getTrackList() {

        return trackList;
    }
    public String getClientIP() { return clientIP; }
    public String getServerIP() { return serverIP; }

    public void register() {
        SimplePostTask<String> task = new SimplePostTask<String>(getServerIP(), Constants.PORT);
        task.execute(new ICrowdMusicAction<String>() {
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
        SimplePostTask<String> task = new SimplePostTask<String>(getServerIP(), Constants.PORT);
        task.execute(new ICrowdMusicAction<String>() {
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
    public void postAudio(final CrowdMusicTrack track) {
        //new PostAudioTask(serverIP, Constants.PORT).execute(track);
        SimplePostTask<CrowdMusicTrack> task = new SimplePostTask<CrowdMusicTrack>(getServerIP(), Constants.PORT);
        task.execute(new ICrowdMusicAction<CrowdMusicTrack>() {
            @Override
            public String getPostTarget() {
                return "track/post";
            }

            @Override
            public CrowdMusicTrack getParam() {
                return track;
            }
        });
    }
    public void upvoteTrack(final Vote vote) {
        SimplePostTask<Vote> task = new SimplePostTask<Vote>(getServerIP(), Constants.PORT);
        task.execute(new ICrowdMusicAction<Vote>() {
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
        SimplePostTask<Vote> task = new SimplePostTask<Vote>(getServerIP(), Constants.PORT);
        task.execute(new ICrowdMusicAction<Vote>() {
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

    public List<CrowdMusicTrack> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<CrowdMusicTrack> newList) {
        playlist = newList;
        notifyClientview();
    }

    public void registerClientView(PropertyChangeListener clientPlaylistView) {
        clientView = clientPlaylistView;
    }
}
