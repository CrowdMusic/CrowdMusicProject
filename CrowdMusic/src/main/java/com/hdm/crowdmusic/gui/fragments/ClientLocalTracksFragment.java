package com.hdm.crowdmusic.gui.fragments;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.core.devicelistener.CrowdDevicesBrowser;
import com.hdm.crowdmusic.core.streaming.PostAudioTask;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;

import org.teleal.cling.registry.RegistryListener;

public class ClientLocalTracksFragment extends ListFragment {

    private OnClientRequestedListener activity;
    private CrowdMusicClient client;
    private ArrayAdapter<CrowdMusicTrack> listAdapter;
    private RegistryListener registryListener;

    public interface OnClientRequestedListener {
        public CrowdMusicClient OnClientRequestedListener();
        public String OnServerRequestedListener();
        public int OnPortRequestedListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = activity.OnClientRequestedListener();
        listAdapter = new PlaylistTrackAdapter(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1, client.getTrackList());

        registryListener = new CrowdDevicesBrowser(getActivity(), listAdapter);
        setListAdapter(listAdapter);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.activity = (OnClientRequestedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final CrowdMusicTrack selectedTrack = (CrowdMusicTrack) listAdapter.getItem(position);
        new PostAudioTask(activity.OnServerRequestedListener(),activity.OnPortRequestedListener() ).execute(selectedTrack);
    }


}



