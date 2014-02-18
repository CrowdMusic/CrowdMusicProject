package com.hdm.crowdmusic.gui.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.Track;

import java.util.List;

public class LocalFilesTrackAdapter extends ArrayAdapter<Track> {

    public LocalFilesTrackAdapter(Context context, int textViewResourceId, List<Track> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_localfileslistentry, null);

        }

        Track track = ((ArrayAdapter<Track>) this).getItem(position);

        if (track != null) {
            TextView trackName = (TextView) v.findViewById(R.id.localfileslist_item_track);
            TextView artist = (TextView) v.findViewById(R.id.localfileslist_item_artist);


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
