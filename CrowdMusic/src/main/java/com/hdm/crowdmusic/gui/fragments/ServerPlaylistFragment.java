package com.hdm.crowdmusic.gui.fragments;


import android.os.Bundle;
import android.app.ListFragment;
import android.widget.ArrayAdapter;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;

import java.util.ArrayList;


/**
 * Created by jules on 04/11/13.
 */
public class ServerPlaylistFragment extends ListFragment {

  /*  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } */

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<CrowdMusicTrack> objects = new ArrayList<CrowdMusicTrack>();

        CrowdMusicTrack track1 = new CrowdMusicTrack();
        track1.setArtist("Metallica");
        track1.setTrackname("For whom the Bell tolls");

        CrowdMusicTrack track2 = new CrowdMusicTrack();
        track2.setArtist("Slayer");
        track2.setTrackname("Raining Blood");

        CrowdMusicTrack track3 = new CrowdMusicTrack();
        track3.setArtist("Sólstafir");
        track3.setTrackname("Fjara");

        objects.add(track1);
        objects.add(track2);
        objects.add(track3);


        PlaylistTrackAdapter adapter = new PlaylistTrackAdapter(getActivity(),
                R.layout.fragment_createserver, objects);



        setListAdapter(adapter);
        //firstLine.addAll(values);
    }
  /*  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serverplaylist, container, false);
        return v;
    } */


}
