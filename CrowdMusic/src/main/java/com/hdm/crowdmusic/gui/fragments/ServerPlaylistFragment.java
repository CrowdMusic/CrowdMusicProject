package com.hdm.crowdmusic.gui.fragments;


import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;

import java.util.ArrayList;
import java.util.List;

public class ServerPlaylistFragment extends ListFragment {


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<CrowdMusicTrack> objects = new ArrayList<CrowdMusicTrack>();

        CrowdMusicTrack testTrack = new CrowdMusicTrack();
        testTrack.setArtist("In Extremo");
        testTrack.setTrackname("Poc Vecem");

        CrowdMusicTrack testTrack2 = new CrowdMusicTrack();
        testTrack2.setArtist("In Extremo");
        testTrack2.setTrackname("Poc Vecem");

        CrowdMusicTrack testTrack3 = new CrowdMusicTrack();
        testTrack3.setArtist("In Extremo");
        testTrack3.setTrackname("Poc Vecem");

        CrowdMusicTrack testTrack4 = new CrowdMusicTrack();
        testTrack4.setArtist("In Extremo");
        testTrack4.setTrackname("Poc Vecem");

        CrowdMusicTrack testTrack5 = new CrowdMusicTrack();
        testTrack5.setArtist("In Extremo");
        testTrack5.setTrackname("Poc Vecem");

        CrowdMusicTrack testTrack6 = new CrowdMusicTrack();
        testTrack6.setArtist("In Extremo");
        testTrack6.setTrackname("Poc Vecem");

        CrowdMusicTrack testTrack7 = new CrowdMusicTrack();
        testTrack7.setArtist("In Extremo");
        testTrack7.setTrackname("Poc Vecem");

        CrowdMusicTrack testTrack8 = new CrowdMusicTrack();
        testTrack8.setArtist("In Extremo");
        testTrack8.setTrackname("Poc Vecem");

        CrowdMusicTrack testTrack9 = new CrowdMusicTrack();
        testTrack9.setArtist("In Extremo");
        testTrack9.setTrackname("Poc Vecem");

        objects.add(testTrack);
        objects.add(testTrack2);
        objects.add(testTrack3);
        objects.add(testTrack4);
        objects.add(testTrack5);
        objects.add(testTrack6);
        objects.add(testTrack7);
        objects.add(testTrack8);
        objects.add(testTrack9);

        PlaylistTrackAdapter adapter = new PlaylistTrackAdapter(getActivity(),
            R.layout.fragment_serverplaylist, objects);

        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serverplaylist, container, false);
        return v;
    }
}
