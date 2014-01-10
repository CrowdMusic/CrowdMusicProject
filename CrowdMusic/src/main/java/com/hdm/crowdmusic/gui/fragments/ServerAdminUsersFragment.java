package com.hdm.crowdmusic.gui.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.streaming.CrowdMusicUser;
import com.hdm.crowdmusic.core.streaming.CrowdMusicUserList;
import com.hdm.crowdmusic.gui.activities.ServerActivity;
import com.hdm.crowdmusic.gui.support.UserAdminAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ServerAdminUsersFragment extends ListFragment implements PropertyChangeListener {
    private UserAdminAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.adapter =  new UserAdminAdapter(getActivity(),
                R.layout.fragment_serveradminusers,new ArrayList<CrowdMusicUser>());
        setListAdapter(adapter);
        ((ServerActivity) getActivity()).getServerData().registerListener(this);
        setupAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serveradminusers, container, false);
        setupAdapter();
        return v;
    }

    public void setupAdapter(){
        List<CrowdMusicUser> userList = ((ServerActivity) getActivity()).getServerData().getClientList();
        setupAdapter(userList);
    }

    public void setupAdapter(final List<CrowdMusicUser> userList){
        Activity activity = getActivity();
        if (activity == null){
            return;
        }

        Handler mainHandler = new Handler(activity.getApplicationContext().getMainLooper());
        mainHandler.post(

                new Runnable() {
                    @Override
                    public void run() {
                        UserAdminAdapter adapter = new UserAdminAdapter(getActivity(),
                                R.layout.fragment_serveradminusers, userList);
                       setListAdapter(adapter);

                    }
                });

    }

    public void addUser(CrowdMusicUser user){
        adapter.add(user);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
       if  (propertyChangeEvent.getNewValue() instanceof CrowdMusicUserList)
       {
           CrowdMusicUserList list = (CrowdMusicUserList) propertyChangeEvent.getNewValue();
            setupAdapter(list.getUserList());
       }
    }
}
