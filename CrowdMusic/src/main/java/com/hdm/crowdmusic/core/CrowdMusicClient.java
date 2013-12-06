package com.hdm.crowdmusic.core;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import com.hdm.crowdmusic.util.Utility;
import java.util.ArrayList;

public class CrowdMusicClient {

    private Context context;
    private ArrayList<CrowdMusicTrack> trackList;

    private String ip;

    public CrowdMusicClient(Context context, String ip) {
        this.context = context;
        this.ip = ip;
        trackList = new ArrayList<CrowdMusicTrack>();
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

                trackList.add(new CrowdMusicTrack(id, ip, artist, title));
                Log.d(Utility.LOG_TAG_MEDIA, id + ", " + title + ", " + artist);
            } while (exCursor.moveToNext());

            exCursor.close();
        }
        else {
            Log.d(Utility.LOG_TAG_MEDIA, "No audio files found!");
        }
    }

    public ArrayList<CrowdMusicTrack> getTrackList() {
        return trackList;
    }
}
