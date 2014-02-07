package com.hdm.crowdmusic.gui.fragments;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.Server;
import com.hdm.crowdmusic.core.Track;
import com.hdm.crowdmusic.gui.activities.ServerActivity;
import com.hdm.crowdmusic.gui.support.IOnServerRequestListener;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class ServerPlaylistFragment extends ListFragment implements PropertyChangeListener {

    public static final String PLAYLIST_CHANGE = "playlist";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpAdapter();
        View v = inflater.inflate(R.layout.fragment_serverplaylist, container, false);

        ((ServerActivity) getActivity()).getServerData().registerServerview(this);
        setUpAdapter();
        return v;
    }

    public void setUpAdapter() {
        Server server = ((IOnServerRequestListener) getActivity()).getServerData();
        List<Track> objects;

        objects = server.getPlaylist().getPlaylist();

        setUpAdapter(objects);
    }

    public void setUpAdapter(final List<Track> newValue) {
        Activity activity = getActivity();
        if (activity == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaylistTrackAdapter adapter = new PlaylistTrackAdapter(getActivity(),
                        R.layout.fragment_serverplaylist, newValue);
                setListAdapter(adapter);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Activity activity = getActivity();
        Track track = (Track) getListAdapter().getItem(position);
        Server server = ((IOnServerRequestListener) activity).getServerData();
        server.getPlaylist().upvote(track.getId(), server.getServerIP());
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        //if  (propertyChangeEvent.getNewValue() instanceof Playlist)
        if  (propertyChangeEvent.getPropertyName().equals(ServerPlaylistFragment.PLAYLIST_CHANGE))
        {
            // We dont need any data here, just the event because we are the server ourselves
            //Playlist list = (Playlist) propertyChangeEvent.getNewValue();
            setUpAdapter();
        }
    }
}
