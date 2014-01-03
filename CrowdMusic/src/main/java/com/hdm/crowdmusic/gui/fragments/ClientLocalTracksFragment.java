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
import com.hdm.crowdmusic.gui.support.IOnClientRequestListener;
import com.hdm.crowdmusic.gui.support.LocalFilesTrackAdapter;

import org.teleal.cling.registry.RegistryListener;

public class ClientLocalTracksFragment extends ListFragment {

    private IOnClientRequestListener listener;
    private CrowdMusicClient client;
    private ArrayAdapter<CrowdMusicTrack> listAdapter;
    private RegistryListener registryListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = listener.getClientData();
        listAdapter = new LocalFilesTrackAdapter(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1, client.getTrackList());

        registryListener = new CrowdDevicesBrowser(getActivity(), listAdapter);
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
        final CrowdMusicTrack selectedTrack = (CrowdMusicTrack) listAdapter.getItem(position);
        client.postAudio(selectedTrack);;
    }
}



