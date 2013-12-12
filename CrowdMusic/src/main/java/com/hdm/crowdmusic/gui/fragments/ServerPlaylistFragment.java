package com.hdm.crowdmusic.gui.fragments;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicPlaylist;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class ServerPlaylistFragment extends ListFragment implements PropertyChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrowdMusicPlaylist.getInstance().listener = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_serverplaylist, container, false);
        return v;
    }

    public void setUpAdapter() {

        List<CrowdMusicTrack> objects = CrowdMusicPlaylist.getInstance().getPlaylist();
        setUpAdapter(objects);
    }

    public void setUpAdapter(final List<CrowdMusicTrack> newValue) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaylistTrackAdapter adapter = new PlaylistTrackAdapter(getActivity(),
                        R.layout.fragment_serverplaylist, newValue);
                setListAdapter(adapter);
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

        Object newValue = propertyChangeEvent.getNewValue();
        if (newValue instanceof List) {
            this.setUpAdapter((List<CrowdMusicTrack>)newValue);
        }
    }
}
