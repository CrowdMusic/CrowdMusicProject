package com.hdm.crowdmusic.gui.fragments;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.core.CrowdMusicServer;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.core.streaming.actions.Vote;
import com.hdm.crowdmusic.gui.support.IOnClientRequestListener;
import com.hdm.crowdmusic.gui.support.IOnServerRequestListener;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;
import com.hdm.crowdmusic.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ServerPlaylistFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpAdapter();
        View v = inflater.inflate(R.layout.fragment_serverplaylist, container, false);
        return v;
    }

    public void setUpAdapter() {
        List<CrowdMusicTrack> objects = ((IOnServerRequestListener) getActivity()).getServerData().getPlaylist().getPlaylist();
        setUpAdapter(objects);
    }

    public void setUpAdapter(final List<CrowdMusicTrack> newValue) {
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
        CrowdMusicTrack track = (CrowdMusicTrack) getListAdapter().getItem(position);
        CrowdMusicServer server = ((IOnServerRequestListener) activity).getServerData();
        server.getPlaylist().upvote(track.getId(), server.getServerIP());
    }
}
