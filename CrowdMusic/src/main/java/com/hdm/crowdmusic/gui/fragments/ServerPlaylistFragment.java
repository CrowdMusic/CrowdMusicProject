package com.hdm.crowdmusic.gui.fragments;


import android.os.Bundle;
import android.app.ListFragment;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jules on 04/11/13.
 */
public class ServerPlaylistFragment extends ListFragment {


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<CrowdMusicTrack> objects = new ArrayList<CrowdMusicTrack>();

        PlaylistTrackAdapter adapter = new PlaylistTrackAdapter(getActivity(),
                R.layout.fragment_createserver, objects);

        setListAdapter(adapter);

    }
}
