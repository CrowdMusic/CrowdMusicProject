package com.hdm.crowdmusic.gui.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.R;

import java.util.List;

public class PlaylistTrackAdapter extends ArrayAdapter<CrowdMusicTrack> {

    private List<CrowdMusicTrack> objects;


    public PlaylistTrackAdapter(Context context, int textViewResourceId, List<CrowdMusicTrack> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_playlistentry, null);

        }

        CrowdMusicTrack track = objects.get(position);

        if (track != null) {
            TextView trackName = (TextView) v.findViewById(R.id.playlist_item_track);
            TextView artist = (TextView) v.findViewById(R.id.playlist_item_artist);


            //TODO: Extend for cover and duration, maybe upvote count

            if (artist != null) {
                artist.setText(track.getArtist());
            }

            if (trackName != null) {
                trackName.setText(track.getTrackName());
            }


        }
        return v;
    }

}
