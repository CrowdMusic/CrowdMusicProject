package com.hdm.crowdmusic.gui.fragments;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdm.crowdmusic.core.Client;
import com.hdm.crowdmusic.core.Track;
import com.hdm.crowdmusic.core.devicelistener.DevicesBrowser;
import com.hdm.crowdmusic.gui.support.IOnClientRequestListener;
import com.hdm.crowdmusic.gui.support.LocalFilesTrackAdapter;

import org.teleal.cling.registry.RegistryListener;

public class ClientLocalTracksFragment extends ListFragment {

    private IOnClientRequestListener listener;
    private Client client;
    private ArrayAdapter<Track> listAdapter;
    private RegistryListener registryListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = listener.getClientData();
        listAdapter = new LocalFilesTrackAdapter(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1, client.getTrackList());

        registryListener = new DevicesBrowser(getActivity(), listAdapter);
        setListAdapter(listAdapter);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.listener = (IOnClientRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Track selectedTrack = (Track) listAdapter.getItem(position);
        client.postAudio(selectedTrack);
    }
}



