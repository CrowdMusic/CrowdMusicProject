package com.hdm.crowdmusic.gui.fragments;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.Track;
import com.hdm.crowdmusic.gui.activities.ClientActivity;
import com.hdm.crowdmusic.gui.support.IOnClientRequestListener;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class ClientServerPlaylistFragment extends ListFragment implements PropertyChangeListener {
    public static final String PLAYLIST_CHANGE = "playlist";

    private IOnClientRequestListener activity;
    private boolean registered =false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serverplaylist, container, false);

        if(! registered){
        ((ClientActivity) getActivity()).getClientData().registerClientView(this);
        registered = true;
        }
        setUpAdapter();
        return v;
    }

    public void setUpAdapter() {
        if (isAdded()){
            List<Track> objects = ((ClientActivity) getActivity()).getClientData().getPlaylist();
            setUpAdapter(objects);
        }
    }

    public void setUpAdapter(final List<Track> newValue) {
        Activity activity = getActivity();
        if (activity == null)
            return;

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

      /*  Activity activity = getActivity();
        final Track track = (Track) getListAdapter().getItem(position);
        final Client client = ((IOnClientRequestListener) activity).getClientData();
        SimplePostTask<Vote> task = new SimplePostTask<Vote>(client.getServerIP(), Constants.PORT);
        task.execute(new IAction<Vote>() {
            @Override
            public String getPostTarget() {
                return "vote/up";
            }

            @Override
            public Vote getParam() {
                return  new Vote(track.getId(), client.getClientIP());
            }
        }); */
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.activity = (IOnClientRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ClientLocalTracksFragment.OnClientRequestedListener");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if  (propertyChangeEvent.getPropertyName().equals(PLAYLIST_CHANGE)) {
            setUpAdapter();
            //Playlist list = (Playlist) propertyChangeEvent.getNewValue();
            //setUpAdapter(list.getPlaylist());
        }
    }
}
