package com.hdm.crowdmusic.gui.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicTrack;

import java.util.List;

public class PlaylistTrackAdapter extends ArrayAdapter<CrowdMusicTrack> {

    public PlaylistTrackAdapter(Context context, int textViewResourceId, List<CrowdMusicTrack> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_playlistentry, null);

        }

        CrowdMusicTrack track = ((ArrayAdapter<CrowdMusicTrack>) this).getItem(position);

        if (track != null) {
            TextView trackName = (TextView) v.findViewById(R.id.playlist_item_track);
            TextView artist = (TextView) v.findViewById(R.id.playlist_item_artist);
            TextView rating = (TextView) v.findViewById(R.id.playlist_item_rating);


            //TODO: Extend for cover and duration, maybe upvote count

            if (artist != null) {
                artist.setText(track.getArtist());
            }

            if (trackName != null) {
                trackName.setText(track.getTrackName());
            }

            if (rating != null) {
                rating.setText(String.valueOf(track.getRating()));
            }


        }
        return v;
    }


}
