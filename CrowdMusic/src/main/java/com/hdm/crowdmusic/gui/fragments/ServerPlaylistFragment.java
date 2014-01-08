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
        Activity activity = getActivity();
        List<CrowdMusicTrack> objects = new ArrayList<CrowdMusicTrack>();
        if (activity instanceof IOnClientRequestListener) {
            objects = ((IOnClientRequestListener) getActivity()).getClientData().getPlaylist();
        } else if (activity instanceof IOnServerRequestListener) {
            objects = ((IOnServerRequestListener) getActivity()).getServerData().getPlaylist().getPlaylist();
        } else {
            Log.e(Utility.LOG_TAG_MEDIA, "setUpAdapter awaits a ClientActivity or ServerActivity.");
        }
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
        if (activity instanceof IOnClientRequestListener) {
            CrowdMusicTrack track = (CrowdMusicTrack) getListAdapter().getItem(position);
            CrowdMusicClient client = ((IOnClientRequestListener) activity).getClientData();
            client.upvoteTrack(new Vote(track.getId(), client.getClientIP()));
        }
    }

}
